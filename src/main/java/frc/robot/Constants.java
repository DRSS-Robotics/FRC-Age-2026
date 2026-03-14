package frc.robot;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import static edu.wpi.first.units.Units.*;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import static edu.wpi.first.units.Units.*;

import edu.wpi.first.units.measure.Angle;

import static edu.wpi.first.units.Units.Degrees;

public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 1;
  }
  public static class VisionConstants {
    public static final String kLimelightName = "limelight";
    public static final String kLimelightStreamURL = "http://limelight.local:5800";
    
    // TODO: fill in all offset values
    // Distance from the center of the turret to the center of the Limelight
    public static final Distance kLLTurretCenterDist = Inches.of(99999);
    // Offset values are measured from the center of the robot horizontally, and from the floor vertically
    public static final Distance kTurretForwardOffset = Inches.of(99999);
    public static final Distance kTurretSideOffset = Inches.of(99999);
    public static final Distance kLimelightHeightOffset = Inches.of(18);
    public static final Angle kLimelightPitchOffset = Degrees.of(15); 
      
    // TODO: get correct driver camera and hopper camera id
    public static final int kDriverCameraId = 0;
    public static final int kHopperCameraId = 1;

    public static final String kOutputStreamName = "Output Stream";
    public static final String kHopperCameraStreamName = "Hopper Camera";

    public static final int kLimelightBumpersPipeline = 0;
    public static final int kLimelightAprilTagsPipeline = 1;
  }

  public static class SuperstructureConstants {
    public static final int kIntakeMotorId = 15;
    public static final double kMaxIntakeDPS2 = 10800;
    public static final double kMaxIntakeDPS3 = 21600;
    public static final int kStorageMotorId = 16;
    public static final int kSoupMotorId = 18;
    public static final double kMaxStorageDPS = 28800;
    public static final double kMaxStorageDPS2 = 28800 * 4;
    public static final double kDefaultSoupSpeedDPS = 18000; // 3000 rpm, max kraken power?
    public static final double kMaxSoupDPS2 = 19600 * 3;
    public static final double kMaxSoupDPS3 = 19600 * 3;
    public static final int kTransferMotorId = 14;
    public static final double kDefaultIntakeSpeed = 2500;
    public static final double kDefaultTransferSpeed = 3000;
    public static final double kMaxTransferDPS = 15000;
    public static final double kMaxTransferDPSPS = 15000;

    /**
     * A degree value that affects the tolerance of when the Fuel storage wall is
     * considered to be closed/open.
     */
    public static final double kStorageStateTolerance = 32;

    /**
     * Target setpoint (in motor degrees) for the Fuel storage wall in its CLOSED
     * state
     */
    public static final double kStorageClosedRotations = 0;

    /**
     * Target setpoint (in motor degrees) for the Fuel storage wall in its OPEN
     * state
     */
    public static final double kStorageOpenRotations = 31 * 360; // temp, converting rottions to fdegrees

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
    public static final double kShooterMaxManualSpeedDPS = 6000;
    public static final double kTurretMaxManualSpeedDPS = 105;

    public static final double kMaxShooterDPS2 = 36000; // accel
    public static final double kMaxShooterDPS3 = 72000; // jerk

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
