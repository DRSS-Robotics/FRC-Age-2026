// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.HangConstants;
import frc.robot.subsystems.HangSubsystem;
import frc.robot.subsystems.HangSubsystem.HangState;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class ToggleHangLevelCommand extends Command {
  
  private HangSubsystem m_hang;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public ToggleHangLevelCommand(HangSubsystem hangSubsystem) {
    m_hang = hangSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(hangSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    HangState state = m_hang.getHangState();
    boolean willOpen = (state == HangState.kIsGrounded || 
                state == HangState.kIsGoingToGround);

    m_hang.setHangMotorPosition(willOpen ? 
      HangConstants.kHangL1Rotations : 
      HangConstants.kHangGroundRotations);
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
    return false;
  }
}
