package frc.robot.subsystems;

import java.lang.invoke.ConstantBootstraps;
import java.net.NetworkInterface;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LimelightHelpers;

public class BumperDectectionSubsystem extends SubsystemBase {
    NetworkTable Table;
    NetworkTableEntry ty;
    double targetOffsetAngle_Vertical;
    double limelightMountAngleDegrees;
    double limelightLensHeightInches;
    double goalHeightInches;
    double angleToGoalDegrees;
    double angleToGoalRadians;
    double realDisToBumpers;
    public BumperDectectionSubsystem() {
        Table = NetworkTableInstance.getDefault().getTable("limelight");
            ty = Table.getEntry("ty");
        targetOffsetAngle_Vertical = ty.getDouble(0.0);
        // how many degrees back is your limelight rotated from perfectly vertical?
        limelightMountAngleDegrees = 15.0; 
    
        // distance from the center of the Limelight lens to the floor
        limelightLensHeightInches = 18.0; 
    
        // distance from the target to the floor
        goalHeightInches = 60.0; 
        //Still need measures from automation
        angleToGoalDegrees = limelightMountAngleDegrees + targetOffsetAngle_Vertical;
        angleToGoalRadians = angleToGoalDegrees * (3.14159 / 180.0);
    }
    public double CalculateDis(){
            //calculate distanc
            double LimelightGoal = goalHeightInches -  limelightLensHeightInches / Math.tan(angleToGoalRadians);
            return LimelightGoal;
    }
        public class LimelightTarget_Detector{
            // Basic targeting data
double tx = LimelightHelpers.getTX("");  // Horizontal offset from crosshair to target in degrees
double ty = LimelightHelpers.getTY("");  // Vertical offset from crosshair to target in degrees
double ta = LimelightHelpers.getTA("");  // Target area (0% to 100% of image)
boolean hasTarget = LimelightHelpers.getTV(""); // Do you have a valid target?

double txnc = LimelightHelpers.getTXNC("");  // Horizontal offset from principal pixel/point to target in degrees
double tync = LimelightHelpers.getTYNC("");  // Vertical offset from principal pixel/point to target in degrees
}
public void setDisToBumpers(double sentDisToBumpers){
    realDisToBumpers = sentDisToBumpers;
}
public double giveDisToBumpers(){
    return realDisToBumpers;
}
}
