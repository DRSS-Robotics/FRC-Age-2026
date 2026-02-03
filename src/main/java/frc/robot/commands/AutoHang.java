// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.HangSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

public class AutoHang extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final HangSubsystem m_hang;
  public int hangTag;
  public boolean canItHang = false;
  public boolean isItAuto;
  //private final VisionSubsystem m_vision;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public AutoHang(HangSubsystem hangSubsystem, int idealHangPosition boolean isItAuto) { //For auto in the code they specify which one with varible, still figuring out how teleop will work
    m_hang = hangSubsystem;
    //It doesn't exist in this branch, but add the vison subsystem too
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(hangSubsystem);
  }


  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    //Detect april tag
    hangTag = getAprilTag(hangTagNumbers);
    double distanceX = m_vision.getDistanceToAprilTag(hangTag);
    double distanceY = m_vision.getDistanceToAprilTag(hangTag);
    //double angle = m_vision.getAngleToAprilTag(hangTag); //TODO determine if angle is nescessary

    //Calculate distance to ideal hang
    double distanceNeededToHangX = Constants.idealHangPositionX - distanceX;
    double distanceNeededToHangY = Constants.idealHangPositionY - distanceY;
    //double angleNeededToHang = Constants.idealHangAngle - angle; 

    
    double robotMovingThisMuchX = distanceNeededToHangX - distanceX;
    double robotMovingThisMuchY = distanceNeededToHangY - distanceY;
    //double robotRotateThisMuch = angleNeededToHang - angle;

    while(robotMovingThisMuchX >= 0 && robotMovingThisMuchX <= 10 && robotMovingThisMuchY >= 0 && robotRotateThisMuchY <= 10 && isitAuto != true){
      System.out.println("Move X by: " + robotMovingThisMuchX);
      System.out.println("Move Y by: " + robotMovingThisMuchY);
      //TODO: Research how to add changing variables to driver station dashboard
      if(robotMovingThisMuchX != 0 && robotMovingThisMuchY  != 0) { 
        //Give it some leeway on the position maybe, depends on how accurate we can be, maybe like 0.3 meters
        System.out.println("You are bad to hang D:");
        //Same thing as todo
      }
      else if(robotMovingThisMuchX >= 0 && robotMovingThisMuchX <= 10 && robotMovingThisMuchY == 0 && robotMovingThisMuchY <= 10){
        //some sort of stop moving?
        canItHang = true;
        System.out.println("You are good to hang :D");
        //SmartDashboard.putBoolean("Hang?", canitHang); I think this works
        break;
      }
    }


    //Autonomous Hang v
    if(isItAuto){
      while(robotMovingThisMuchX >= 0 && robotMovingThisMuchX <= 10 && robotMovingThisMuchY >= 0 && robotRotateThisMuchY <= 10 && isitAuto != true){
        if(robotMovingThisMuchX != 0 && robotMovingThisMuchY != 0) { 
          moveRobotX(robotMovingThisMuchX); //I am aware this is not how you move, I will fix later
          moveRobotY(distanceNeededToHangY);
          System.out.println("You are bad to hang D:");
        }
        else if(robotMovingThisMuchX >= 0 && robotMovingThisMuchX <= 10 && robotMovingThisMuchY == 0 && robotMovingThisMuchY <= 10){
          stopMoving(please);
          canItHang = true;
          System.out.println("You are good to hang :D");
          //SmartDashboard.putBoolean("Hang?", canitHang);
          break;
        }
      }
      //Go up to hang 
      m_hang.setHangMotorPosition(HangConstants.kHangL1Rotations);
      //hold position until command is over
      if(getHangState() == HangState.kIsL1 && canItHang){
        m_hang.setHangMotorSpeed(1);
      }
    }
    //TODO figure out the code for choosing hanging side and all the variables with that
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // guh nathan was here
    m_hang.setHangMotorSpeed(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}

