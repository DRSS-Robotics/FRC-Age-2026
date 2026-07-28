package frc.robot;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Inches;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;

public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;

  }

  public static enum Driver {
    kMax,
    kGavin
  }


  public static final Driver driverForThisComp = Driver.kGavin;

  public static class SuperstructureConstants {
    public static final int kIntakeMotorId = 15;
    public static final double kMaxIntakeDPS2 = 10800;
    public static final double kMaxIntakeDPS3 = 21600;
    public static final double kMaxStorageDPS = 14400;
    public static final double kMaxStorageDPS2 = 28800;
    public static final double kDefaultIntakeSpeed = 6000;

    /**
     * A degree value that affects the tolerance of when the Fuel storage wall is
     * considered to be closed/open.
     */
    public static final double kStorageStateTolerance = 360;

    /**
     * Target setpoint (in motor degrees) for the Fuel storage wall in its CLOSED
     * state
     */
    public static final double kStorageClosedRotations = 0;

    /**
     * Target setpoint (in motor degrees) for the Fuel storage wall in its OPEN
     * state
     */
    public static final double kStorageOpenRotations = 28 * 360; // temp, converting rottions to fdegrees

    public static final double kMaxTestIntakeSpeedErrorPercentage = 3.;
    public static final double kMaxTestIntakeTimeToSpinUp = 0.25;
    public static final double kMinTestIntakeTimeToMaintainSpeed = 20;
    public static final double kTestIntakeTargetDPS = 540;

    // guh guh

  }

  public static double kGravIN;
}
