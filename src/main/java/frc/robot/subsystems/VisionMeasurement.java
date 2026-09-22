// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;

public class VisionMeasurement {

  public Pose2d pose;
  public double timestampSeconds;
  public boolean isValid;

  /** Class that contains information needed to update the pose estimator, should be used every tick */
  public VisionMeasurement(Pose2d pose, double timestampSeconds, boolean isValid) {
    this.pose = pose;
    this.timestampSeconds = timestampSeconds;
    this.isValid = isValid;
  }

}