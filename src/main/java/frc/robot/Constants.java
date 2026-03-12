package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;

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
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import static edu.wpi.first.units.Units.*;

import edu.wpi.first.math.geometry.Pose3d;

//import edu.wpi.first.units.Degrees;

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


    public static final double kMaxTestHangErrorPercentage = 0.3;
    public static final double kMaxTesthangTimeToReachHeight = 4.0;
    public static final Angle kTestTargetHangle = Degrees.of(0);

  }

  public static class SuperstructureConstants {
    public static final int kIntakeMotorId = 15;
    public static final int kStorageMotorId = 16;
    public static final int kSoupMotorId = 18;
    public static final int kTransferMotorId = 14;
    public static final double kDefaultIntakeSpeed = 2500;
    public static final double kDefaultSoupSpeed = 1080;
    public static final double kDefaultTransferSpeed = 1080;
    public static final int kAgitatorMotorId = 6767; //CHANGE THE MOTOR ID!!!!!!!!

    /**
     * A degree value that affects the tolerance of when the Fuel storage wall is
     * considered to be closed/open.
     */
    public static final double kStorageStateTolerance = 1;
    
    /**
     * Target setpoint (in motor degrees) for the Fuel storage wall in its CLOSED state
     */
    public static final double kStorageClosedRotations = 0;
  
    /**
     * Target setpoint (in motor degrees) for the Fuel storage wall in its OPEN state
     */
    public static final double kStorageOpenRotations = 21 * 360; // temp, converting rottions to fdegrees



    public static final double kMaxTestIntakeSpeedErrorPercentage = 3.;
    public static final double kMaxTestIntakeTimeToSpinUp = 0.25 ; 
    public static final double kMinTestIntakeTimeToMaintainSpeed = 20; 
    public static final double kTestIntakeTargetDPS = 540; 

    public static final double kMaxTestSoupSpeedErrorPercentage = 3;
    public static final double kMaxTestSoupTimeToSpinUp = 0.25;
    public static final double kMinTestSoupTimeToMaintainSpeed = 20;
    public static final double kTestSoupTargetDPS = 540;
//guh

    
    public static final double kMaxTestWallErrorPercentage = 0.3;
    public static final double kMaxTestWallTimeToReachHeight = 4.0;
    public static final Angle  kTestWallTargetAngle = Degrees.of(0);

  }
  
}
