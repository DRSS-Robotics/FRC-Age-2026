// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import edu.wpi.first.wpilibj2.command.Command;

public class RunLaunchMotor extends Command {

  private final ShooterSubsystem m_subsystem;
  private final double power;

  public RunLaunchMotor(ShooterSubsystem shooter, double speed) {
    m_subsystem = shooter;
    addRequirements(shooter);
    power = speed;
  }


  @Override
  public void initialize() {
    m_subsystem.runLaunchMotor(
      DegreesPerSecond.of(power)
    );
  }


  @Override
  public void execute() {}


  @Override
  public void end(boolean interrupted) {
    m_subsystem.runLaunchMotor(DegreesPerSecond.of(0));
  }


  @Override
  public boolean isFinished() {
    return false;
  }
}
