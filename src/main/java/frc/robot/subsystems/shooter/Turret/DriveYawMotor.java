// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter.Turret;

import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.function.Supplier;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.Command;

public class DriveYawMotor extends Command {

  private final TurretSubsystem m_turretSubsystem;
  private Supplier<AngularVelocity> speed;

  public DriveYawMotor(TurretSubsystem turret, Supplier<AngularVelocity> speedSupplier) {
    m_turretSubsystem = turret;
    speed = speedSupplier;
    addRequirements(turret);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    m_turretSubsystem.setTurretPower(0.1);
  }

  @Override
  public void end(boolean interrupted) {
    m_turretSubsystem.setTurretPower(0);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
