// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.Degrees;

import edu.wpi.first.units.measure.Angle;

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
  
  public static final double kGravIN = 0;

  public static class ShooterConstants {
    public static final double kShooterManualDriveDPSScale = 1;
    public static final double kShooterAngleTolerance = 1;

    public static final double kMaxTestLaunchMotorSpeedErrorPercentage = 0.3;
    public static final double kMaxTestLaunchMotorTimeToSpinUp = 4.;
    public static final double kMinTestLaunchMotorTimeToMaintainSpeed = 10;
    //DPS is degrees per second
    public static final double kMaxTestLaunchMotorTargetDPS = 540;

    public static final double kMaxTestYawMotorErrorPercentage = 0.3;
    public static final double kMaxTestYawMotorTimeToReachPosition = 4.;
    public static final Angle  kTestYawMotorTargetPosition = Degrees.of(0);
  }
}
