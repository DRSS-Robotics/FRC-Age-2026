package frc.robot.commands;

import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.Constants.SuperstructureConstants;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class ToggleIntakeCommand extends Command {

  private SuperstructureSubsystem m_intake;

  /**
   * Toggles the intake on/off, based on its current state and the default
   * intake speed in {@link SuperstructureConstants#kDefaultIntakeSpeed}
   */
  public ToggleIntakeCommand(SuperstructureSubsystem intakeSubsystem) {
    m_intake = intakeSubsystem;
    addRequirements(intakeSubsystem);
  }
  

  @Override
  public void initialize() {
    if (m_intake.getIntakeSpeed().isEquivalent(DegreesPerSecond.of(0))) {
        m_intake.runIntake(SuperstructureConstants.kDefaultIntakeSpeed);
      } else {
        m_intake.runIntake(0);
    }
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


