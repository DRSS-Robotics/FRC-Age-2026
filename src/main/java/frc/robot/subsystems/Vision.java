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
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers;

public class Vision extends SubsystemBase {

  private HttpCamera limelight;
  private UsbCamera driverCamera;
  private MjpegServer outputStream;

  /** Creates a new Vision subsystem */
  public Vision() {
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
    // TODO: add whiteboard calculations
    LimelightHelpers.setCameraPose_RobotSpace(VisionConstants.kLimelightName, 
      VisionConstants.kLimelightForwardOffset,
      VisionConstants.kLimelightSideOffset,
      VisionConstants.kLimelightHeightOffset,
      VisionConstants.kLimelightRollOffset,
      VisionConstants.kLimelightPitchOffset,
      VisionConstants.kLimelightYawOffset
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

  public void updatePoseEstimate(SwerveDrivePoseEstimator m_poseEstimator, double angularVelocity) {
    updateLimelightPosition();
    // Use April tag data to update swerve drive pose estimate (MegaTag2)
    LimelightHelpers.SetRobotOrientation(VisionConstants.kLimelightName, m_poseEstimator.getEstimatedPosition().getRotation().getDegrees(), 0, 0, 0, 0, 0);
    LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(VisionConstants.kLimelightName);
    // only update if angular velocity is less than 360 degrees per second and at least 1 tag is detected
    if (Math.abs(angularVelocity) < 360 && mt2.tagCount > 0) {
      m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.7, .7, 9999999));
      m_poseEstimator.addVisionMeasurement(mt2.pose, mt2.timestampSeconds);
    }
  }

  @Override
  public void periodic() {}

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
