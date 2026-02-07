// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.FieldConstants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.shooter.*;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class RotateToHub extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_shooterSubsystem;
  private final TurretControl m_turretController;
  private final Pose3d hubPose = FieldConstants.kHubPose;
  private final Supplier<Pose3d> poseSupplier;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public RotateToHub(ShooterSubsystem shooterSubsystem, Supplier<Pose3d> poseSupplier) {
    m_shooterSubsystem = shooterSubsystem;
    m_turretController = m_shooterSubsystem.getTurretControl();
    this.poseSupplier = poseSupplier;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_turretController);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    
    
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
