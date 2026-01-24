// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class SwapCameraKart extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final Vision m_vision;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public SwapCameraKart(Vision vision) {
    m_vision = vision;
  
    
    
    // Use addRequirements() here to declare subsystem dependencies.
    // addRequirements(subsystem);
}

// Called when the command is initially scheduled.
@Override
public void initialize() {
    Vision.setCamera(Vision.Camera.kLimelight);
}

// Called every time the scheduler runs while the command is scheduled.
@Override
public void execute() {}

// Called once the command ends or is interrupted.
@Override
public void end(boolean interrupted) {
    Vision.setCamera(Vision.Camera.kDriverCamera);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
