// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SuperstructureSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class ToggleIntakeCommand extends Command {

  private SuperstructureSubsystem m_intake;

  /**
   * Creates a new command
   *
   * @param subsystem The subsystem used by this command.
   */
  public ToggleIntakeCommand(SuperstructureSubsystem intakeSubsystem) {
    m_intake = intakeSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(intakeSubsystem);
  }
  

  @Override
  public void initialize() {
    m_intake.runIntake(-0.5);
  }

  @Override
  public void execute() {}

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return true;
  }
}


