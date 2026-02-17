// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
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
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }
  public static class HangConstants {
    public static final double kHangGroundRotations = 0;
    public static final double kHangL1Rotations = 0;
    public static final double kHangLevelTolerance = 0;
    /**
     *DPS is Degrees Per Second
     */
    public static final double kHangManualDriveDPSScale = 0;
    

    //automation stuff

    //target pose
    private static final double hangLeftX = 0;
    private static final double hangLeftY = 0;
    private static final Rotation2d hangLeftRotate = Rotation2d.fromDegrees(120);
    public static final Pose2d hangLeftPose = new Pose2d(hangLeftX, hangLeftY, hangLeftRotate);

    private static final double hangRightX = 0;
    private static final double hangRightY = 0;
    public static final Rotation2d hangRightRotate = Rotation2d.fromDegrees(120);
    public static final Pose2d hangRightPose = new Pose2d(hangRightX, hangRightY, hangRightRotate);
    
    //thresholds
    public static final double hangThresholdPose = 0;
    public static final double hangThresholdAngle = 0;

    //TODO: Get the actual target values (measure with limelight with robot)

  }
}
