// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.HangSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.HangConstants;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;


public class AutoHangPose extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final HangSubsystem m_hang;
  public boolean canItHang = false;
  public boolean isItAuto;
  public int idealHangPositionLeft;
  public int idealHangPositionRight;
  Pose2d targetPose;
  Pose2d currentPose;
  public boolean guh = true;
  enum hangSide {
    LEFT,
    RIGHT
  }
  //double currentPose = m_vision.updatePoseEstimate();

  //private final VisionSubsystem m_vision;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public AutoHangPose(HangSubsystem hangSubsystem, boolean isItAuto) { 
    //m_vision = VisionSubsystem;
    m_hang = hangSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(hangSubsystem);
  }

  public static void getDistance(){
    
  }

  public hangSide getSide(){
    //find the nearest one using the Pose2d.nearest()
    hangSide nearestHangSide = hangSide.LEFT; //Add actual logic for nearest side, this for testing

    switch(nearestHangSide) {
      case LEFT:
      targetPose = HangConstants.hangLeftPose;
        break;
      case RIGHT:
      targetPose = HangConstants.hangRightPose;
        break;
      
    
  }
  return nearestHangSide;
  }


  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
      //if left is closer: go to left pose

      //We gotta use the pose estimate from the vision subsystem, it's not a double btw
       //Figure out what you need, add to constants, then put those here

       if (isItAuto){
       //Exchange the data to path planner
       //Then run the path in path planner
       //Command pathfindingCommand = AutoBuilder.pathfindToPose(targetPose, constraints, 0.0);
       //figure out what is needed to add to the class to get the scheduler
       //scheduler.schedule(pathfindingCommand);
       }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(isItAuto != true){
      //double howMuchtoMove = targetPose - currentPose;
      double currentPoseX = currentPose.getX();
      double currentPoseY = currentPose.getY();
      double targetPoseX = targetPose.getX();
      double targetPoseY = targetPose.getY();

      //Pose2d distancemaybe = currentPose.relativeTo(targetPose);
      //howMuchtoMove = Distance.minus();
      
      // SmartDashboard.putNumber("Move X by:", howMuchtoMove);
      // SmartDashboard.putNumber("Move Y by:", howMuchtoMove);

      //Figure out how you will get how to get there
      //I think it should be some math with the current and target pose like the simple one I had before
      if(guh) { 
        canItHang = false;
        //System.out.println("You are bad to hang D:");
        SmartDashboard.putBoolean("Hang?", canItHang);
      }
      else if(guh != true){
        canItHang = true;
        //System.out.println("You are good to hang :D");
        SmartDashboard.putBoolean("Hang?", canItHang);
      }
    }

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // guh nathan was here
    m_hang.setHangMotorSpeed(0);//NOt sure if this is needed, the next command will be sending the hang upb
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}


//TODO: Research how to use path planner for the movement, check in with teleop about other thing?
//TODO: Stop moving code

//TODO: Correct April tag stuff, drawing on camera feed with Luka?

//TODO: Doubled check where code should be 
//TODO: Recheck the logic, walk through it
//TODO: Add comments explaining code