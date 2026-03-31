// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.function.Supplier;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.Command;

public class DriveLaunchMotor extends Command {

  private final ShooterSubsystem m_subsystem;
  private Supplier<AngularVelocity> shooterSpeed;
    private Supplier<AngularVelocity> transfer;
  
    public DriveLaunchMotor(ShooterSubsystem shooter, Supplier<AngularVelocity> launchSpeed, Supplier<AngularVelocity> transferSpeed) {
      m_subsystem = shooter;
      shooterSpeed = launchSpeed;
      transfer = transferSpeed;
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    m_subsystem.runLaunchMotors(shooterSpeed.get());
    m_subsystem.runTransferMotor(transfer.get());
  }

  @Override
  public void end(boolean interrupted) {
    m_subsystem.runLaunchMotors(0);
    m_subsystem.runTransferMotor(0);
        
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
