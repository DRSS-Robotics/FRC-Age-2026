package frc.robot.commands;

import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class Kickback extends Command {

  private SuperstructureSubsystem m_intake;
  private ShooterSubsystem m_shooter;
  private Supplier<Double> guh;

  /**
   * kicks the fuel back
   */
  public Kickback(SuperstructureSubsystem transferSubsystem, ShooterSubsystem shooter, Supplier<Double> relSpeed) {
    m_intake = transferSubsystem;
    m_shooter = shooter;
    guh = relSpeed;
  }

  @Override
  public void execute() {
   m_intake.runSoupMotor(-SuperstructureConstants.kDefaultSoupSpeedDPS * guh.get());
    m_shooter.runTransferMotor(-SuperstructureConstants.kDefaultTransferSpeed * guh.get());
    m_shooter.runLaunchMotors(-ShooterConstants.kShooterMaxManualSpeedDPS * guh.get());
  }
  
  @Override
  public void end(boolean interrupted) {
    m_intake.runSoupMotor(0);
    m_shooter.runTransferMotor(0);
    m_shooter.runLaunchMotors(0);

  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
