// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoCommands;

import frc.robot.subsystems.BumperDectectionSubsystem;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.Vision;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.LimelightHelpers;
import frc.robot.LimelightHelpers.LimelightTarget_Detector;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.units.measure.Velocity;

/** An example command that uses an example subsystem. */
public class BumperDetectionCommand extends Command{
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
private final BumperDectectionSubsystem m_BumperDetector;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public BumperDetectionCommand(BumperDectectionSubsystem bumpDetector_Camera) { 
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
    LimelightHelpers.setPipelineIndex("PipeLine_BumperDetection", 0);
    distanceFromLimelightToGoalInches
  }
    //putting this here to not forget but the getting the data itself code may be better suited as a subsystems
  

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
