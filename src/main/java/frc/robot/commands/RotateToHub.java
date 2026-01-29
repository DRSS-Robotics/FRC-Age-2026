// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.RobotContainer;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.shooter.*;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class RotateToHub extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final RobotContainer m_robotContainer;
  private final TurretControl m_turretController;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public RotateToHub(RobotContainer robotContainer) {
    m_robotContainer = robotContainer;
    m_turretController = m_robotContainer.m_outtakeSubsystem.m_turretController;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_turretController);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // Pose3d robotPose = new Pose3d(m_robotContainer.m_poseEstimator.getEstimatedPosition);
    Pose3d hubPose = m_robotContainer.hubPose;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
