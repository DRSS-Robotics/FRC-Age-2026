// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.shooter.TurretControl;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import edu.wpi.first.wpilibj2.command.Command;

public class RotateTurretMotor extends Command {

  private final TurretControl m_turretController;

  public RotateTurretMotor(TurretControl turretControl) {
    m_turretController = turretControl;
    addRequirements(m_turretController);
  }


  @Override
  public void initialize() {
    m_turretController.setTurretPosition(
      Degrees.of(25)
    );
  }


  @Override
  public void execute() {}


  @Override
  public void end(boolean interrupted) {
    m_turretController.setTurretPosition(
      Degrees.of(0));
  }


  @Override
  public boolean isFinished() {
    return false;
  }
}
