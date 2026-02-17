// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.HangSubsystem;

import java.util.List;

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
  public boolean typeOfTeleopGuh; //true is directions, false is auto alignment
  Pose2d targetPose;
  public boolean guh = true;
  private SwerveDrivePoseEstimator m_poseEstimator;
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

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

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
    if(isItAuto != true && typeOfTeleopGuh == true){
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
    if (isItAuto != true && typeOfTeleopGuh == false){
      //auto alignment, really just small adjustment for precision
      double guh = AutoHangPose.getHangCalculation(guhguh);
      //move xDiff and move yDiff and rDiff
      //until the all diffs are 0 (does threshold stuff in getHangcalc(), )
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


//TODO: Teleop correct pose slightly, automation adjustment v if drivers don't like this
//TODO: Teleop directions 
//TODO: Automation, path planner? I think pose estimator can be used here too