// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.shooter.TurretControl;
import frc.robot.LimelightHelpers;
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers.RawFiducial;

import edu.wpi.first.wpilibj2.command.Command;

public class RotateToHub extends Command {

  private final TurretControl m_subsystem;

  public RotateToHub(TurretControl turret) {
    m_subsystem = turret;
    addRequirements(turret);
  }
  
  @Override
  public void execute() {
    //Thorughbore encoder specs
    double totalTicksPerRev = 8192;
    //Calculates how many ticks it takes to rotate 1 degree
    double ticksPerDegree = totalTicksPerRev/360;
    //Get the april tag target values
    RawFiducial[] fiducials = LimelightHelpers.getRawFiducials(VisionConstants.kLimelightName);
    double targetOffsetHorizontal = fiducials[0].txnc;
    double targetOffsetDistance = fiducials[0].distToRobot;
    //Gets the secant of distToRobot/txnc to find the offset angle to the hub
    double targetAngle = Math.toDegrees(1/Math.cos(targetOffsetDistance/targetOffsetHorizontal));
    //Determine how many ticks are needed to turn to the target angle
    double targetTicks = targetAngle * ticksPerDegree;
    //speed that turret rotates
    double speed = 0.3;
    //Sets the motor position to the target angle
    m_subsystem.runTurretMotor(speed, targetTicks);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
    public void end(boolean interrupted) {
    }

  @Override
  public InterruptionBehavior getInterruptionBehavior() {
    return InterruptionBehavior.kCancelIncoming;
  }
}
