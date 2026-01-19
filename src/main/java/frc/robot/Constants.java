package frc.robot;

import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  /**
   * A collection of constants pertaining to the various subsystems under the control of the Operator.
   * This includes things like their controller's USB port, the Fuel wall's open/closed positions, etc.
   */
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;

    /**
     * Encoder position for the Fuel storage wall in its CLOSED state
     */
    public static final double kWallClosedPosition = 0;

    /**
     * Encoder position for the Fuel storage wall in its OPEN state
     */
    public static final double kWallOpenPosition = 0;
  }
}
