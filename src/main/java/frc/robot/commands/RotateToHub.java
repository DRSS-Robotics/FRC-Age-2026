// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.shooter.TurretControl;
import frc.robot.LimelightHelpers;
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers.LimelightResults;
import frc.robot.LimelightHelpers.LimelightTarget_Detector;
import frc.robot.LimelightHelpers.RawFiducial;
import frc.robot.subsystems.Vision;

import static edu.wpi.first.units.Units.Degrees;
import java.util.function.Supplier;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.Encoder;

public class RotateToHub extends Command {

  private final TurretControl m_subsystem;

  public RotateToHub(TurretControl turret) {
    m_subsystem = turret;
    addRequirements(turret);
  }
  
  @Override
  public void execute() {
    double totalTicksPerRev = 8192;
    double ticksPerDegree = totalTicksPerRev/360;
    //Need to convert from angle to double for calculation- can't cast not primitive type
    RawFiducial[] fiducials = LimelightHelpers.getRawFiducials(VisionConstants.kLimelightName);
    double targetOffsetHorizontal = fiducials[0].txnc;
    double targetOffsetDistance = fiducials[0].distToRobot;
    //Gets the secant of distToRobot/txnc to find the offset angle to the hub
    double targetAngle = Math.toDegrees(1/Math.cos(targetOffsetDistance/targetOffsetHorizontal));
    double targetTicks = targetAngle * ticksPerDegree;
    //speed that turret rotates
    double speed = 0.3;
    //Sets the motor position to the target angle.
    m_subsystem.runTurretMotor(speed, targetTicks);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public InterruptionBehavior getInterruptionBehavior() {
    return InterruptionBehavior.kCancelIncoming;
  }
}
