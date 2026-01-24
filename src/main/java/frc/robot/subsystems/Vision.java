// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.LimelightHelpers;

public class Vision extends SubsystemBase {

  public static enum Camera {
    kLimelight,
    kDriverCamera
  }
  public Camera currentCamera = Camera.kDriverCamera;

  private HTTPCamera limelight;
  private UsbCamera driverCamera;

  /** Creates a new Vision. */
  public Vision() {
    // Initialize Limelight
    LimelightHelpers.setPipelineIndex(Constants.kLimelightName, 0);
    LimelightHelpers.setCameraPose_RobotSpace(Constants.kLimelightName, 
      Constants.kLimelightForwardOffset,
      Constants.kLimelightSideOffset,
      Constants.kLimelightHeightOffset,
      Constants.kLimelightRollOffset,
      Constants.kLimelightPitchOffset,
      Constants.kLimelightYawOffset
    );
    limelight = new HTTPCamera(Constants.kLimelightName, "http://limelight.local:5801/stream.mjpg");

    // Initialize driver camera
    driverCamera = new UsbCamera(Constants.kDriverCameraName, 0);

    // TODO: Initialize output stream 
  }

  /**
   * Example command factory method.
   *
   * @return a command
   */

  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // The following code uses April tag data to update the swerve drive pose estimate.
    // TODO: import m_poseEstimator and m_gyro

    double robotYaw = m_gyro.getYaw();
    LimelightHelpers.SetRobotOrientation(Constants.kLimelightName, robotYaw, 0.0, 0.0, 0.0, 0.0, 0.0);

    LimelightHelpers.PoseEstimate limelightMeasurement = LimelightHelpers.getBotPoseEstimate_wpiBlue(Constants.kLimelightName);

    m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.5, .5, 9999999));
    m_poseEstimator.addVisionMeasurement(
      limelightMeasurement.pose,
      limelightMeasurement.timestampSeconds
    );
    
  }
    
  // Not sure if this function will be used but it seems useful to have
  public RawFiducial getAprilTag(int id) {
    RawFiducial[] fiducials = LimelightHelpers.getRawFiducials(Constants.kLimelightName);
    for (RawFiducial fiducial : fiducials) {
      if (fiducial.id == id) {
        return fiducial
      }
    }
    return null
  }

  public void setCamera(Camera camera) {
    // TODO: implement camera switching logic
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
