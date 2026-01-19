// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SuperstructureSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class ToggleWallCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private SuperstructureSubsystem m_wall;
  /**
   * Creates a new command
   *
   * @param subsystem The subsystem used by this command.
   */
//                               \/ this refers to the mechanism that opens and closes the wall on the intake
  public void IntakeWallCommand(SuperstructureSubsystem wallSubsystem) {
    //This opens and closes the wall so the intake can get in then not fall out. 
    m_wall = wallSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(wallSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_wall.setWallMotorPosition(0.5);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    m_wall.setWallMotorPosition(-0.5);
    return true;
  }

}
