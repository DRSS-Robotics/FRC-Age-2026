package frc.robot;

import edu.wpi.first.units.measure.Angle;

import static edu.wpi.first.units.Units.Degrees;
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
  
  public static class SuperstructureConstants {
    public static final int kIntakeMotorId = 5;
    public static final int kStorageMotorId = 6;
    public static final int kTransferMotorId = 6;
    public static final double kDefaultIntakeSpeed = 16;
    public static final double kDefaultTransferSpeed = 16;

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
    public static final double kStorageOpenRotations = 800;



    public static final double kMaxTestIntakeSpeedErrorPercentage = 3.;
    public static final double kMaxTestIntakeTimeToSpinUp = 0.25 ; 
    public static final double kMinTestIntakeTimeToMaintainSpeed = 20; 
    public static final double kTestIntakeTargetDPS = 540; 

    public static final double kMaxTestTransferSpeedErrorPercentage = 3;
    public static final double kMaxTestTransferTimeToSpinUp = 0.25;
    public static final double kMinTestTransferTimeToMaintainSpeed = 20;
    public static final double kTestTransferTargetDPS = 540;
    
    public static final double kMaxTestWallErrorPercentage = 0.3;
    public static final double kMaxTestWallTimeToReachHeight = 4.0;
    public static final Angle  kTestWallTargetAngle = Degrees.of(0);

  }

}
