package frc.robot;

import edu.wpi.first.units.measure.Angle;

import static edu.wpi.first.units.Units.Degrees;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;

  }

  public static class SuperstructureConstants {
    public static final int kIntakeMotorId = 15;
    public static final double kMaxIntakeDPS = 3600;
    public static final double kMaxIntakeDPSPS = 3600;
    public static final int kStorageMotorId = 16;
    public static final int kSoupMotorId = 18;
    public static final double kMaxSoupDPS = 3600;
    public static final double kMaxSoupDPSPS = 3601;
    public static final int kTransferMotorId = 14;
    public static final double kDefaultIntakeSpeed = 2500;
    public static final double kDefaultSoupSpeed = 2800;
    public static final double kDefaultTransferSpeed = 1540;
    public static final double kMaxTransferDPS = 3600;
    public static final double kMaxTransferDPSPS = 3600;

    /**
     * A degree value that affects the tolerance of when the Fuel storage wall is
     * considered to be closed/open.
     */
    public static final double kStorageStateTolerance = 1;

    /**
     * Target setpoint (in motor degrees) for the Fuel storage wall in its CLOSED
     * state
     */
    public static final double kStorageClosedRotations = 0;

    /**
     * Target setpoint (in motor degrees) for the Fuel storage wall in its OPEN
     * state
     */
    public static final double kStorageOpenRotations = 29 * 360; // temp, converting rottions to fdegrees

    public static final double kMaxTestIntakeSpeedErrorPercentage = 3.;
    public static final double kMaxTestIntakeTimeToSpinUp = 0.25;
    public static final double kMinTestIntakeTimeToMaintainSpeed = 20;
    public static final double kTestIntakeTargetDPS = 540;

    public static final double kMaxTestSoupSpeedErrorPercentage = 3;
    public static final double kMaxTestSoupTimeToSpinUp = 0.25;
    public static final double kMinTestSoupTimeToMaintainSpeed = 20;
    public static final double kTestSoupTargetDPS = 540;
    // guh guh

    public static final double kMaxTestWallErrorPercentage = 0.3;
    public static final double kMaxTestWallTimeToReachHeight = 4.0;
    public static final Angle kTestWallTargetAngle = Degrees.of(0);

  }

  public static class ShooterConstants {
    public static final double kShooterManualDriveDPSScale = 1;
    public static final double kShooterAngleTolerance = 1;
    public static final double kShooterMaxManualSpeedDPS = 360 * 10;
    public static final double kTurretMaxManualSpeedDPS = 105;

    public static final double kMaxShooterDPS = 6000;
    public static final double kMaxShooterDPSPS = 4000;

    public static final double kMaxTestLaunchMotorSpeedErrorPercentage = 0.3;
    public static final double kMaxTestLaunchMotorTimeToSpinUp = 4.;
    public static final double kMinTestLaunchMotorTimeToMaintainSpeed = 10;
    // DPS is degrees per second
    public static final double kMaxTestLaunchMotorTargetDPS = 540;

    public static final double kMaxTestYawMotorErrorPercentage = 0.3;
    public static final double kMaxTestYawMotorTimeToReachPosition = 4.;
    public static final Angle kTestYawMotorTargetPosition = Degrees.of(0);
  }

  public static double kGravIN;
}
