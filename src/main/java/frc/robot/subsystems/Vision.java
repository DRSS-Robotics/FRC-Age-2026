// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.UsbCameraInfo;
import edu.wpi.first.cscore.VideoSink;
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
  private UsbCamera hopperCamera; 
  private MjpegServer outputStream;

  private Supplier<Angle> turretAngleSupplier;
  private SwerveDrivePoseEstimator m_poseEstimator;
  private CorePigeon2 m_pigeon;

  /** Creates a new Vision subsystem */
  public Vision(Supplier<Angle> turretAngleSupplier, SwerveDrivePoseEstimator poseEstimator, CorePigeon2 pigeon) {
    this.turretAngleSupplier = turretAngleSupplier;
    m_poseEstimator = poseEstimator;
    m_pigeon = pigeon;

    // Initialize Limelight
    updateLimelightPosition();
    limelight = new HttpCamera(VisionConstants.kLimelightStreamName, VisionConstants.kLimelightStreamURL);
    CameraServer.startAutomaticCapture(limelight);

    // Start pose estimation command (runs forever)
    new VisionPoseEstimation(this, m_poseEstimator, m_pigeon);

    // Initialize driver camera
    //driverCamera = new UsbCamera(VisionConstants.kDriverCameraStreamName, VisionConstants.kDriverCameraId);
    driverCamera = CameraServer.startAutomaticCapture(VisionConstants.kDriverCameraStreamName, VisionConstants.kDriverCameraId);

    // Initialize and start streaming hopper camera
    hopperCamera = CameraServer.startAutomaticCapture(VisionConstants.kHopperCameraStreamName, VisionConstants.kHopperCameraId);

    // Initialize output stream and start streaming driver camera
    outputStream = CameraServer.addSwitchedCamera(VisionConstants.kOutputStreamName);
    CameraServer.addServer(outputStream);
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

    System.out.println("CAN DETECT: " + CameraServer.getServer(VisionConstants.kOutputStreamName));
  }

  public void updateLimelightPosition() {
    Angle turretAngle = turretAngleSupplier.get();

    // The Limelight's position is modeled in polar coordinates (<distance from Limelight to center>, <turret angle>)
    // These coordinates are converted to Cartesian coordinates to find the position relative to the center of the turret
    double limelightForwardOffset = VisionConstants.kLLTurretCenterDist.in(Meters) * Math.sin(turretAngle.in(Radians));
    double limelightSideOffset = VisionConstants.kLLTurretCenterDist.in(Meters) * Math.cos(turretAngle.in(Radians));

    // Set the Limelight position in robot space using offset constants
    LimelightHelpers.setCameraPose_RobotSpace(VisionConstants.kLimelightName, 
      /* forward offset */ VisionConstants.kTurretForwardOffset.in(Meters) + limelightForwardOffset,
      /* side offset    */ VisionConstants.kTurretSideOffset.in(Meters) + limelightSideOffset,
      /* height offset  */ VisionConstants.kLimelightHeightOffset.in(Meters),
      /* roll offset    */ 0,
      /* pitch offset   */ VisionConstants.kLimelightPitchOffset.in(Degrees),
      /* yaw offset     */ turretAngle.in(Degrees)
    );
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

  @Override
  public void periodic() {}

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}