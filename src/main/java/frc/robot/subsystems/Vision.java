// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator3d;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.UsbCameraInfo;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import static edu.wpi.first.units.Units.*;
import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.units.measure.Angle;
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers;
import frc.robot.commands.VisionPoseEstimation;
import java.util.function.Supplier;
import com.ctre.phoenix6.hardware.Pigeon2;

import java.lang.Math;
import com.ctre.phoenix6.hardware.core.CorePigeon2;

public class Vision extends SubsystemBase {

  private HttpCamera limelight;
  private UsbCamera driverCamera;
  private MjpegServer outputStream;

  private SwerveDrivePoseEstimator3d m_poseEstimator;
  private CorePigeon2 m_pigeon;
  private CommandSwerveDrivetrain drivetrain;

  public VisionPoseEstimation viscommand;

  /** Creates a new Vision subsystem */
  public Vision(SwerveDrivePoseEstimator3d poseEstimator, CorePigeon2 pigeon, CommandSwerveDrivetrain drivetrain) {
    m_poseEstimator = poseEstimator;
    m_pigeon = pigeon;
    this.drivetrain = drivetrain;

    // Initialize Limelight
    updateLimelightPosition();
    setLimelightPipeline(VisionConstants.kLimelightAprilTagsPipeline);
    limelight = new HttpCamera("limelight", VisionConstants.kLimelightStreamURL);
    limelight.setConnectionStrategy(ConnectionStrategy.kKeepOpen);

    // Start pose estimation command (runs forever)
    viscommand = new VisionPoseEstimation(this, m_poseEstimator, m_pigeon, drivetrain);
    
    
    // Initialize driver camera
    driverCamera = new UsbCamera("driverCamera", VisionConstants.kDriverCameraId);
    driverCamera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
    
    // Initialize and start streaming hopper camera
    UsbCamera hopperCamera = CameraServer.startAutomaticCapture(VisionConstants.kHopperCameraStreamName,
    VisionConstants.kHopperCameraId);
    
    // Initialize output stream and start streaming driver camera
    outputStream = CameraServer.addSwitchedCamera(VisionConstants.kOutputStreamName);
    useDriverCamera();
    
    // Camera status logging
    for (UsbCameraInfo cam : UsbCamera.enumerateUsbCameras()) {
      System.out.print("VISION: USB camera detected: name: \"");
      System.out.print(cam.name);
      System.out.print("\" id: ");
      System.out.println(cam.dev);
    }
    System.out.println("VISION: Limelight connected: " + limelight.isConnected());
    System.out.println("VISION: Driver camera connected: " + driverCamera.isConnected());
    System.out.println("VISION: Hopper camera connected: " + hopperCamera.isConnected());
  }
  
  /**
   * Update the Limelight's position relative to the robot. This should be called
   * before pose estimation.
   */
  public void updateLimelightPosition() {
    
    // The Limelight's position is modeled in polar coordinates (<distance from
    // Limelight to center>, <turret angle>)
    // These coordinates are converted to Cartesian coordinates to find the position
    // relative to the center of the turret
    
    // Set the Limelight position in robot space using offset constants
    LimelightHelpers.setCameraPose_RobotSpace(VisionConstants.kLimelightName,
    /* forward offset */ VisionConstants.kTurretForwardOffset.in(Meters),
    /* side offset */ VisionConstants.kTurretSideOffset.in(Meters),
    /* height offset */ VisionConstants.kLimelightHeightOffset.in(Meters),
    /* roll offset */ 0,
    /* pitch offset */ VisionConstants.kLimelightPitchOffset.in(Degrees),
    /* yaw offset */ 180);
  }
  
  /** Switch output stream to Limelight */
  public void useLimelight() {
    outputStream.setSource(limelight);
    System.out.println("VISION: Set output stream source to Limelight");
  }

  /** Switch output stream to driver camera */
  public void useDriverCamera() {
    outputStream.setSource(driverCamera);
    System.out.println("VISION: Set output stream source to driver camera");
  }
  
  /** Set the Limelight's pipeline */
  public void setLimelightPipeline(int index) {
    LimelightHelpers.setPipelineIndex(VisionConstants.kLimelightName, index);
    System.out.println("VISION: Set Limelight pipeline to " + index);
  }
  
  @Override
  public void periodic() {
  }
  
  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
