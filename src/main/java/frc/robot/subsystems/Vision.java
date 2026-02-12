// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.UsbCameraInfo; // Do not remove, used for testing purposes
import edu.wpi.first.cscore.VideoSink;
import static edu.wpi.first.units.Units.*;
import edu.wpi.first.units.measure.Angle;
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers;
import frc.robot.commands.VisionPoseEstimation;
import java.util.function.Supplier;
import java.lang.Math;
import com.ctre.phoenix6.hardware.core.CorePigeon2;

public class Vision extends SubsystemBase {

  private HttpCamera limelight;
  private UsbCamera driverCamera;
  private UsbCamera hopperCamera;
  private VideoSink outputStream;

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
    limelight = new HttpCamera(VisionConstants.kLimelightName, VisionConstants.kLimelightStreamURL);
    
    // Start pose estimation command (runs forever)
    new VisionPoseEstimation(this, m_poseEstimator, m_pigeon);

    // Initialize driver camera
    driverCamera = new UsbCamera("USB Camera " + VisionConstants.kDriverCameraId, VisionConstants.kDriverCameraId);

    // Initialize hopper camera
    hopperCamera = new UsbCamera("USB Camera " + VisionConstants.kHopperCameraId, VisionConstants.kHopperCameraId);

    // Initialize output stream and start streaming driver camera
    //outputStream = new MjpegServer(VisionConstants.kOutputStreamName, VisionConstants.kOutputStreamPort);
    outputStream = CameraServer.addServer("guh");
    useDriverCamera();

    outputStream.setSource(hopperCamera);

    // TODO: add hopperCamera to top right of outputStream

    for (UsbCameraInfo cam : UsbCamera.enumerateUsbCameras()) {
      System.out.print("VISION: USB camera detected: name: \"");
      System.out.print(cam.name);
      System.out.print("\" id: ");
      System.out.println(cam.dev);
    }
    
    //System.out.print("VISION: Limelight connected: ");
    //System.out.println(limelight.getActualFPS());
    //System.out.print("VISION: Driver camera connected: ");
    //System.out.println(driverCamera.getActualFPS());
    System.out.print("VISION: Hopper camera connected: ");
    System.out.println(hopperCamera.isConnected());
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

  /** Switch camera stream to Limelight */
  public void useLimelight() {
    outputStream.setSource(limelight);
  }

  /** Switch camera stream to driver camera */
  public void useDriverCamera() {
    outputStream.setSource(driverCamera);
  }

  @Override
  public void periodic() {}

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
