// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.units.Meters;
import edu.wpi.first.units.Degrees;
import edu.wpi.first.units.DegreesPerSecond;
import edu.wpi.first.units.measure.Angle;
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers;
import java.util.function.Supplier;
import java.lang.Math;

public class Vision extends SubsystemBase {

  private HttpCamera limelight;
  private UsbCamera driverCamera;
  private MjpegServer outputStream;

  // This is just used for testing purposes, it should be false during competition
  private final boolean logPoseEstimate = false;

  /** Creates a new Vision subsystem */
  public Vision(Supplier<Angle> turretAngleSupplier) {
    // Initialize Limelight
    updateLimelightPosition();
    limelight = new HttpCamera(VisionConstants.kLimelightName, VisionConstants.kLimelightStreamURL);

    // Initialize driver camera
    driverCamera = new UsbCamera(VisionConstants.kDriverCameraName, VisionConstants.kDriverCameraId);

    // Initialize output stream and start streaming driver camera
    outputStream = new MjpegServer(VisionConstants.kOutputStreamName, VisionConstants.kOutputStreamPort);
    useDriverCamera();
  }

  public void updateLimelightPosition() {
    double turretAngle = turretAngleSupplier.get().in(Degrees);

    // The Limelight's position is modeled in polar coordinates (<distance from Limelight to center>, <turret angle>)
    // These coordinates are converted to Cartesian coordinates to find the position relative to the center of the turret
    double limelightForwardOffset = VisionConstants.kLLCenterDist.in(Meters) * Math.sin(turretAngle);
    double limelightSideOffset = VisionConstants.kLLCenterDist.in(Meters) * Math.cos(turretAngle);

    // Set the Limelight position in robot space using offset constants
    LimelightHelpers.setCameraPose_RobotSpace(VisionConstants.kLimelightName, 
      /* forward offset */ VisionConstants.kTurretForwardOffset.in(Meters) + limelightForwardOffset,
      /* side offset    */ VisionConstants.kTurretSideOffset.in(Meters) + limelightSideOffset,
      /* height offset  */ VisionConstants.kLimelightHeightOffset.in(Meters),
      /* roll offset    */ 0,
      /* pitch offset   */ VisionConstants.kLimelightPitchOffset.in(Meters),
      /* yaw offset     */ turretAngle
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

  public void updatePoseEstimate(SwerveDrivePoseEstimator m_poseEstimator, Pigeon2 m_pigeon) {
    updateLimelightPosition();

    // Use April tag data to update swerve drive pose estimate (MegaTag2)
    LimelightHelpers.SetRobotOrientation(VisionConstants.kLimelightName, m_poseEstimator.getEstimatedPosition().getRotation().getDegrees(), 0, 0, 0, 0, 0);
    LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(VisionConstants.kLimelightName);
    // only update if angular velocity is less than 360 degrees per second and at least 1 tag is detected
    if (Math.abs(m_pigeon.getAngularVelocityZWorld().getValue().in(DegreesPerSecond)) < 360 && mt2.tagCount > 0) {
      m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.7, .7, 9999999));
      m_poseEstimator.addVisionMeasurement(mt2.pose, mt2.timestampSeconds);
    }

    // log pose estimate
    // note that measurements are assumed to be in meters
    if (logPoseEstimate) {
      Pose2d estimatedPosition = m_poseEstimator.getEstimatedPosition();
      System.out.print("pose estimate: (");
      System.out.print(estimatedPosition.getX());
      System.out.print(", ");
      System.out.print(estimatedPosition.getY());
      System.out.println(")");
    }
  }

  @Override
  public void periodic() {}

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
