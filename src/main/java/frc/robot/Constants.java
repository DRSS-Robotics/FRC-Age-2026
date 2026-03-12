// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

  public static final int kPigeonID = 50;

  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 0;
  }
  

  public static class ShooterConstants {
    public static final double kCenterOffset = 0.3;

    public static final double kShooterManualDriveDPSScale = 1;
    public static final double kShooterAngleTolerance = 1;
    public static final double kSetPitchPositionTimeoutSeconds = 1;

    public static final double kMinPitchDegrees = 0;
    public static final double kMaxPitchDegrees = 0;

    // TODO: set value
    public static final int kPowerID = 0;
    public static final int kTurretID = 0;
    public static final double kPitch = 65;
    public static final double kHeight = 20;

    // TODO: must do experimenting to find this, see what power/voltage values equal what inches per second
    public static final double kPowerScalingFactor = 0;
  }

  public static class FieldConstants {
    // TODO: find pose of the hub, might be online somewhere for a value
    public static final Pose3d kHubPoseRed = new Pose3d(12.03,4.06,0,new Rotation3d());
    public static final Pose3d kHubPoseBlue = new Pose3d(4.74,4.08,0,new Rotation3d());
    public static final Pose3d kHubPose = kHubPoseRed;

    public static final double kGravIN = 0;
  }
}
