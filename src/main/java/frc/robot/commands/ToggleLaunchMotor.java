// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.function.Supplier;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.Command;

public class ToggleLaunchMotor extends Command {

  private final ShooterSubsystem m_subsystem;
  private Supplier<AngularVelocity> speed;
  private Supplier<Boolean> endOnTrue;

  public ToggleLaunchMotor(ShooterSubsystem shooter, Supplier<AngularVelocity> speedSupplier,
      Supplier<Boolean> endCommandWhenTrue) {
    m_subsystem = shooter;
    speed = speedSupplier;
    endOnTrue = endCommandWhenTrue;
    addRequirements(shooter);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    m_subsystem.runLaunchMotors(speed.get());
    m_subsystem.runTransferMotor(speed.get().times(0.45));
  }

  @Override
  public void end(boolean interrupted) {
    m_subsystem.runLaunchMotors(0);
    m_subsystem.runTransferMotor(0);
  }

  @Override
  public boolean isFinished() {
    return endOnTrue.get();
  }
}
