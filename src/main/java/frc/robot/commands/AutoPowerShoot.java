// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.function.Supplier;

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.Command;

public class AutoPowerShoot extends Command {

  private final ShooterSubsystem m_subsystem;
  private Supplier<Boolean> endOnTrue;

  // Essentially works as a function: input distance from the hub, returns power that will shoot into the hub correctly
  private InterpolatingDoubleTreeMap shooterSpeedMap = new InterpolatingDoubleTreeMap();

  public AutoPowerShoot(ShooterSubsystem shooter, Supplier<Boolean> endCommandWhenTrue) {
    m_subsystem = shooter;
    endOnTrue = endCommandWhenTrue;
    addRequirements(shooter);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    AngularVelocity speed = DegreesPerSecond.of(3850);
    m_subsystem.runShooterMotors(speed);
  }

  @Override
  public void end(boolean interrupted) {
    m_subsystem.runShooterMotors(
        DegreesPerSecond.of(0));
  }

  @Override
  public boolean isFinished() {
    return endOnTrue.get();
  }
}
