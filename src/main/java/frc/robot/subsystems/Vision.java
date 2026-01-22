// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.LimelightHelpers;

public class Vision extends SubsystemBase {

  /** Creates a new Vision. */
  public Vision() {
    LimelightHelpers.setPipelineIndex(Constants.kLimelightName, 0);
    /*
    TODO: set camera position relative to robot
    LimelightHelpers.setCameraPose_RobotSpace("", 
        0.5,    // Forward offset (meters)
        0.0,    // Side offset (meters)
        0.5,    // Height offset (meters)
        0.0,    // Roll (degrees)
        30.0,   // Pitch (degrees)
        0.0     // Yaw (degrees)
    );
    */
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
    /* 
    The following code uses April tag data to update the swerve drive post estimate.
    This could be useful in the future.
    make sure to properly retrive m_poseEstimator

    double robotYaw = m_gyro.getYaw();  
    LimelightHelpers.SetRobotOrientation(Constants.kLimelightName, robotYaw, 0.0, 0.0, 0.0, 0.0, 0.0);

    LimelightHelpers.PoseEstimate limelightMeasurement = LimelightHelpers.getBotPoseEstimate_wpiBlue(Constants.kLimelightName);

    m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.5, .5, 9999999));
    m_poseEstimator.addVisionMeasurement(
        limelightMeasurement.pose,
        limelightMeasurement.timestampSeconds
    );
    }*/

    // Get April tag detections
    RawFiducial[] fiducials = LimelightHelpers.getRawFiducials(Constants.kLimelightName);
    for (RawFiducial fiducial : fiducials) {
        /*fiducial.id;               // Tag ID
        fiducial.txnc;             // X offset (no crosshair)
        fiducial.tync;             // Y offset (no crosshair)
        fiducial.ta;               // Target area
        fiducial.distToCamera;     // Distance to camera
        fiducial.distToRobot;      // Distance to robot
        fiducial.ambiguity;        // Tag pose ambiguity*/
    
        // TODO: do something with detection data
    }    
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
