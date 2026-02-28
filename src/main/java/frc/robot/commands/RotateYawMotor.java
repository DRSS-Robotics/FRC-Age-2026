// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.Degrees;
import java.util.function.Supplier;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;

public class RotateYawMotor extends Command {

  private final ShooterSubsystem m_subsystem;
  private final Supplier<Angle> angle;

  private Angle targetAngle;

  public RotateYawMotor(ShooterSubsystem shooter, Supplier<Angle> angleSupplier) {
    m_subsystem = shooter;
    angle = angleSupplier;
    addRequirements(shooter);
  }
  
  @Override
  public void execute() {
    targetAngle = angle.get();
    m_subsystem.setYawMotorPosition(
        targetAngle);
  }

  @Override
  public boolean isFinished() {
    return m_subsystem.getYawEncoder().isNear(targetAngle, Degrees.of(1)); // magic number
  }

  @Override
  public InterruptionBehavior getInterruptionBehavior() {
    return InterruptionBehavior.kCancelIncoming;
  }
}
