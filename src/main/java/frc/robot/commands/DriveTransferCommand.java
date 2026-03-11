package frc.robot.commands;

import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class DriveTransferCommand extends Command {

  private SuperstructureSubsystem m_intake;
  private Supplier<Double> guh;

  /**
   * Toggles the intake on/off, based on its current state and the default
   * intake speed in {@link SuperstructureConstants#kDefaultIntakeSpeed}
   */
  public DriveTransferCommand(SuperstructureSubsystem transferSubsystem, Supplier<Double> transferSpeedSupplier) {
    m_intake = transferSubsystem;
    guh = transferSpeedSupplier;
  }

  @Override
  public void execute() {
   m_intake.runSoupMotor(guh.get() * SuperstructureConstants.kDefaultSoupSpeed);
    m_intake.runTransferMotor(guh.get() * SuperstructureConstants.kDefaultTransferSpeed);
  }

  @Override
  public void end(boolean interrupted) {
    m_intake.runSoupMotor(0);
    m_intake.runTransferMotor(0);

  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
