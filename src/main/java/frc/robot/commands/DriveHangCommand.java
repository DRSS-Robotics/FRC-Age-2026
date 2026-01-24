// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.HangConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.HangSubsystem;
import frc.robot.subsystems.HangSubsystem.HangState;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class DriveHangCommand extends Command {
  
  private HangSubsystem m_hang;
  private Supplier<Double> speedSupplier;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public DriveHangCommand(HangSubsystem hangSubsystem, Supplier<Double> speedSupplier) {
    m_hang = hangSubsystem;
    this.speedSupplier = speedSupplier;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(hangSubsystem);
  }


  
  @Override
  public void initialize() {

  }



  @Override
  public void execute() {
    m_hang.setHangMotorSpeed(HangConstants.kHangManualDriveDPSScale * speedSupplier.get());
  }



  @Override
  public void end(boolean interrupted) {
    m_hang.setHangMotorSpeed(0);

  }

  

  @Override
  public boolean isFinished() {
    return false;
  }
}
