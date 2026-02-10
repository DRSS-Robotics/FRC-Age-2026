package frc.robot;

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
import edu.wpi.first.units.Inches;
import edu.wpi.first.units.Degrees;


//import edu.wpi.first.units.Degrees;

public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;

  }
  public static final double kGravIN = 0;
  public static class SuperstructureConstants {
    public static final int kIntakeMotorId = 5;
    public static final int kStorageMotorId = 6;
    public static final int kAgitatorMotorId = 6;
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
  }

   public static class HangConstants {
    public static final double kHangGroundRotations = 0;
    public static final double kHangL1Rotations = 0;
    public static final double kHangLevelTolerance = 0;
    /**
     *DPS is Degrees Per Second
     */
    public static final double kHangManualDriveDPSScale = 0;
  }

  public static class VisionConstants {
    public static final String kLimelightName = "limelight";
    public static final String kLimelightStreamURL = "http://limelight.local:5801/stream.mjpg";

    // TODO: fill in all offset values
    // Distance from the center of the turret to the center of the Limelight
    public static final Distance kLLTurretCenterDist = Inches.of(99999);
    // Offset values are measured from the center of the robot horizontally, and from the floor vertically
    public static final Distance kTurretForwardOffset = Inches.of(99999);
    public static final Distance kTurretSideOffset = Inches.of(99999);
    public static final Distance kLimelightHeightOffset = Inches.of(18);
    public static final Angle kLimelightPitchOffset = Degrees.of(15); 
    
    public static final String kOutputStreamName = "Guh";
    public static final int kOutputStreamPort = 3141;
  
    // TODO: get correct driver camera name and id
    public static final String kDriverCameraName = "";
    public static final int kDriverCameraId = 0;
  }
  
}
