// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoCommands;

import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.SuperstructureSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.LimelightHelpers;
import frc.robot.LimelightHelpers.LimelightTarget_Detector;

/** An example command that uses an example subsystem. */
public class BumperDetectionCommand extends Command{
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final LimelightTarget_Detector m_BumperDetector;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public BumperDetectionCommand(LimelightTarget_Detector bumpDetector_Camera) { 
    m_BumperDetector = bumpDetector_Camera;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(bumpDetector_Camera);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    //m_intake.runIntake(-0.5);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
