package frc.robot.commands;

import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class ToggleFuelTransferCommand extends Command {

  private SuperstructureSubsystem m_intake;
  private ShooterSubsystem m_shooter;



  /**
   * Toggles the transfer motor on/off, based on its current state and the default
   * speed in {@link SuperstructureConstants#kDefaultSoupSpeedDPS}
   */
  public ToggleFuelTransferCommand(SuperstructureSubsystem intakeSubsystem, ShooterSubsystem shooter) {
    m_intake = intakeSubsystem;
    m_shooter = shooter;
    addRequirements(intakeSubsystem);
  }
  


  @Override
  public void initialize() {
    if (m_intake.getSoupSpeed().isEquivalent(DegreesPerSecond.of(0))) {
        m_intake.runSoupMotor(SuperstructureConstants.kDefaultSoupSpeedDPS);
        m_shooter.runTransferMotor(SuperstructureConstants.kDefaultTransferSpeed);
      } else {
        m_intake.runSoupMotor(0);
        m_shooter.runTransferMotor(0);
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


