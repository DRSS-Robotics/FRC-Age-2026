// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import static edu.wpi.first.units.Units.*;

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
      
    // TODO: get correct driver camera and hopper camera name and id
    public static final int kDriverCameraId = 0;
    public static final int kHopperCameraId = 1;

    public static final String kOutputStreamName = "Output Stream";
    public static final String kHopperCameraStreamName = "Hopper Camera";
  }
}
