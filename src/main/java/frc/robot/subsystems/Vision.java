// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers;

public class Vision extends SubsystemBase {

  private HTTPCamera limelight;
  private UsbCamera driverCamera;

  /** Creates a new Vision subsystem */
  public Vision() {
    // Initialize Limelight
    LimelightHelpers.setCameraPose_RobotSpace(VisionConstants.kLimelightName, 
      VisionConstants.kLimelightForwardOffset,
      VisionConstants.kLimelightSideOffset,
      VisionConstants.kLimelightHeightOffset,
      VisionConstants.kLimelightRollOffset,
      VisionConstants.kLimelightPitchOffset,
      VisionConstants.kLimelightYawOffset
    );
    limelight = new HTTPCamera(VisionConstants.kLimelightName, VisionConstants.kLimelightStreamURL);
  
    // Initialize and start streaming driver camera
    driverCamera = CameraServer.getInstance().startAutomaticCapture(VisionConstants.kDriverCameraName, VisionConstants.kDriverCameraId);
  }

  /** Switch camera stream to Limelight */
  public void useLimelight() {
    CameraServer.getInstance().getServer().setSource(limelight);
  }

  /** Switch camera stream to driver camera */
  public void useDriverCamera() {
    CameraServer.getInstance().getServer().setSource(driverCamera);
  }

  /**
   * Returns April tag detection data (RawFiducial) if detected, otherwise null.
   * Not sure if this function will be used but it seems useful to have.
   */
  public RawFiducial getAprilTag(int id) {
    RawFiducial[] fiducials = LimelightHelpers.getRawFiducials(VisionConstants.kLimelightName);
    for (RawFiducial fiducial : fiducials) {
      if (fiducial.id == id) {
        return fiducial
      }
    }
    return null
  }

  @Override
  public void periodic() {
    // Use April tag data to update swerve drive pose estimate (MegaTag2)
    // TODO: import m_gyro and m_poseEstimator
    LimelightHelpers.SetRobotOrientation(VisionConstants.kLimelightName, m_poseEstimator.getEstimatedPosition().getRotation().getDegrees(), 0, 0, 0, 0, 0);
    LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(VisionConstants.kLimelightName);
    // only update if angular velocity is less than 360 degrees per second and at least 1 tag is detected
    if (Math.abs(m_gyro.getRate()) < 360 && mt2.tagCount > 0) {
      m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.7,.7,9999999));
      m_poseEstimator.addVisionMeasurement(mt2.pose, mt2.timestampSeconds);
    }
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
