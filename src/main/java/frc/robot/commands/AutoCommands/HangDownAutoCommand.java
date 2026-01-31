// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoCommands;

import frc.robot.Constants.HangConstants;
import frc.robot.subsystems.HangSubsystem;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;
import static edu.wpi.first.units.Units.Degrees;

/** An example command that uses an example subsystem. */
public class HangDownAutoCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final HangSubsystem m_hang;
  public static Angle hangGroundElevation = Degrees.of(HangConstants.kHangGroundRotations);

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public HangDownAutoCommand(HangSubsystem hang) {
    m_hang = hang;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(hang);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_hang.setHangMotorPosition(hangGroundElevation);
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

