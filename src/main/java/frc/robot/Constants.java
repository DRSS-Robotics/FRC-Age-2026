package frc.robot;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
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

   public static class VisionConstants {
    public static final String kLimelightName = "limelight";
    public static final String kLimelightStreamURL = "http://limelight.local:5800";

    public static final Distance kLimelightHeightOffset = Inches.of(18);
    public static final Distance kLimelightSideOffset = Inches.of(0);
    public static final Distance kLimelightForwardOffset = Inches.of(-12);
    public static final Angle kLimelightYawOffset = Degrees.of(0);
    public static final Angle kLimelightPitchOffset = Degrees.of(15);

    public static final int kLimelightAprilTagPipeline = 1;

    public static final Transform3d questNavOffset = new Transform3d(
            new Translation3d(0,0,0), 
            new Rotation3d(0,0,0));
  }


  public static final Driver driverForThisComp = Driver.kGavin;

  public static class SuperstructureConstants {
    public static final int kIntakeMotorId = 15;
    public static final double kMaxIntakeDPS2 = 10800;
    public static final double kMaxIntakeDPS3 = 21600;
    public static final int kStorageMotorId = 14;
    public static final int kSoupMotorId = 16;
    public static final double kMaxStorageDPS = 14400;
    public static final double kMaxStorageDPS2 = 28800;
    public static final double kDefaultSoupSpeedDPS = 4500;
    public static final double kMaxSoupDPS2 = 19601 * 4;
    public static final double kMaxSoupDPS3 = 19600 * 4;
    public static final int kTransferMotorId = 18;
    public static final double kDefaultIntakeSpeed = 6000;
    public static final double kDefaultTransferSpeed = 6001;
    public static final double kMaxTransferDPS2 = 48000;
    public static final double kMaxTransferDPS3 = 48000;

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
    public static final int kShooterMotorLeftId = 21;
    public static final int kShooterMotorRightId = 22;
    public static final double kShooterManualDriveDPSScale = 1;
    public static final double kShooterAngleTolerance = 1;
    public static final double kShooterMaxManualSpeedDPS = 9000;
    public static final double kTurretMaxManualSpeedDPS = 600; 
    public static final double kHoodMaxManualSpeedDPS = 10; //-3.6

    public static final Distance kShooterHeightOffset = Inches.of(12);
    public static final Distance kShooterSideOffset = Inches.of(0);
    public static final Distance kShooterForwardOffset = Inches.of(12);
    public static final Angle kShooterYawOffset = Degrees.of(180);

    public static final double kMaxShooterDPS2 = 36000; // accel
    public static final double kMaxShooterDPS3 = 72000; // jerk

    public static final double kMaxTestLaunchMotorSpeedErrorPercentage = 0.3;
    public static final double kMaxTestLaunchMotorTimeToSpinUp = 4.;
    public static final double kMinTestLaunchMotorTimeToMaintainSpeed = 10;
    // DPS is degrees per second
    public static final double kMaxTestLaunchMotorTargetDPS = 540;

    public static final int kYawMotorId = 19;
    public static final double kMaxTestYawMotorErrorPercentage = 0.3;
    public static final double kMaxTestYawMotorTimeToReachPosition = 4.;
    public static final Angle kTestYawMotorTargetPosition = Degrees.of(0);
    public static final Pose2d kShooterOffset = new Pose2d(0,0,new Rotation2d());
    public static final double kTurretGearRatio = 250.0 / 36.0;
    public static final double kTurretEncoderOffset = 0.902099609375; 
    public static final double kMaxForwardRotation = 1.3;  // Maximum Left limit
    public static final double kMaxReverseRotation = -1.3; // Maximum Right limit


    public static final int kHoodMotorId = 20;
  }


  public static Distance kFieldLengthMeters = Inches.of(651.22);
  // if Blue alliance, use shorter distance, otherwise use longer
  public static Distance kHubXPos = (DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == Alliance.Red) ? 
                                    kFieldLengthMeters.minus(Inches.of(182.11)) : 
                                    Inches.of(182.11);
  public static Pose2d kHubPoseCenter = new Pose2d(kHubXPos.in(Meters),Inches.of(158.84).in(Meters),new Rotation2d(0));


  public static double kGravIN;
}
