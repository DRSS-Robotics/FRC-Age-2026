package frc.robot.commands;

import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class SoupKickback extends Command {

  private SuperstructureSubsystem m_intake;
  private ShooterSubsystem m_shooter;
  private Supplier<Double> guh;

  /**
   * Toggles the intake on/off, based on its current state and the default
   * intake speed in {@link SuperstructureConstants#kDefaultIntakeSpeed}
   */
  public SoupKickback(SuperstructureSubsystem transferSubsystem, ShooterSubsystem shooter) {
    m_intake = transferSubsystem;
    m_shooter = shooter;
  }

  @Override
  public void execute() {
   m_intake.runSoupMotor(-SuperstructureConstants.kDefaultSoupSpeedDPS);
    m_shooter.runTransferMotor(-SuperstructureConstants.kDefaultTransferSpeed);
  }

  @Override
  public void end(boolean interrupted) {
    m_intake.runSoupMotor(0);
    m_shooter.runTransferMotor(0);

  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
