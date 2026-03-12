package frc.robot;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import frc.robot.Constants.HangConstants;

import java.util.List;

public class AutoHangCalculator {
    private static SwerveDrivePoseEstimator m_poseEstimator;
    
    public static double[] getHangCalculation(Pose3d pose3d) {
        //uses speed supplier thing for current pose
        Pose2d currentPose = pose3d.toPose2d();
        //finds which target pose is closer and sets it to that one
        Pose2d targetPose = m_poseEstimator.getEstimatedPosition().nearest(List.of(HangConstants.hangLeftPose, HangConstants.hangRightPose));
        //I think that there needs to be a seperate function for returning the target and current poses, this only returns the diffs
        double currentPoseX = currentPose.getX();
        double currentPoseY = currentPose.getY();
        double currentPoseRot = currentPose.getRotation().getDegrees();
        double targetPoseX = targetPose.getX();
        double targetPoseY = targetPose.getY();
        double targetPoseRot = targetPose.getRotation().getDegrees();
        

        double xDiff = targetPoseX - currentPoseX;
        double yDiff = targetPoseY - currentPoseY;
        //rDiff is rotation of yaw
        double rDiff = targetPoseRot - currentPoseRot;

        double threshold = HangConstants.hangThresholdPose;
        if(Math.abs(xDiff) < threshold){
            xDiff = 0;
        }
        if(Math.abs(yDiff) < threshold){
            yDiff = 0;
        }
        if (Math.abs(rDiff) < HangConstants.hangThresholdAngle){
            rDiff = 0;
        }
        
        return new double[] { xDiff, yDiff, rDiff};
    }

    public static Pose2d[] getTargetAndCurrent (Pose3d pose3d){
        Pose2d currentPose = pose3d.toPose2d();
        //finds which target pose is closer and sets it to that one
        Pose2d targetPose = m_poseEstimator.getEstimatedPosition().nearest(List.of(HangConstants.hangLeftPose, HangConstants.hangRightPose));


        return new Pose2d[] {currentPose,targetPose};
    }
}