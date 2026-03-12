// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.HangSubsystem;

import java.util.List;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.HangConstants;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import frc.robot.AutoHangCalculator;


public class AutoHangPose extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final HangSubsystem m_hang;
  public boolean canItHang = false;
  public boolean isItAuto;
  public boolean typeOfTeleopGuh; //true is directions, false is auto alignment
  Pose2d targetPose;
  Pose2d currentPose;
  public boolean canItBePrintedWeCanHang = true;
  //private SwerveDrivePoseEstimator m_poseEstimator;
  Pose3d currentPoseEst; //pose estimator thing Felix is doing I think

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
    


  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double [] hangCalc = AutoHangCalculator.getHangCalculation(currentPoseEst);
    Pose2d [] tarCurPose = AutoHangCalculator.getTargetAndCurrent(currentPoseEst);
    currentPose = tarCurPose[0];
    targetPose = tarCurPose[1];
    Command pathfindingCommand = AutoBuilder.pathfindToPose(targetPose, null, null); //contraints?
    if (isItAuto){
       pathfindingCommand.schedule();
       }

    if(isItAuto != true && typeOfTeleopGuh == true){
        SmartDashboard.putNumber("Move X by:", hangCalc[0]); //This is xdiff
        SmartDashboard.putNumber("Move Y by:", hangCalc[1]); //This is ydiff
        SmartDashboard.putNumber("Rotate by: ", hangCalc[2]); //This is rdiff

      if(canItBePrintedWeCanHang) { 
        canItHang = false;
        //System.out.println("You are bad to hang D:");
        SmartDashboard.putBoolean("Hang?", canItHang);
      }
      else if(canItBePrintedWeCanHang != true){
        canItHang = true;
        //System.out.println("You are good to hang :D");
        SmartDashboard.putBoolean("Hang?", canItHang);
      }
    }
    if (isItAuto != true && typeOfTeleopGuh == false){
      //auto alignment, really just small adjustment for precision
      pathfindingCommand.schedule();
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
