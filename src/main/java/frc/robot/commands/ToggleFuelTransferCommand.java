package frc.robot.commands;

import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class ToggleFuelTransferCommand extends Command {

  private SuperstructureSubsystem m_intake;



  /**
   * Toggles the transfer motor on/off, based on its current state and the default
   * speed in {@link SuperstructureConstants#kDefaultTransferSpeed}
   */
  public ToggleFuelTransferCommand(SuperstructureSubsystem intakeSubsystem) {
    m_intake = intakeSubsystem;
    addRequirements(intakeSubsystem);
  }
  


  @Override
  public void initialize() {
    if (m_intake.getTransferSpeed().isEquivalent(DegreesPerSecond.of(0))) {
        m_intake.runTransferMotor(SuperstructureConstants.kDefaultTransferSpeed);
      } else {
        m_intake.runTransferMotor(0);
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


