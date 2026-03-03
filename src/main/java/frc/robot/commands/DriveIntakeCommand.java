package frc.robot.commands;

import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class DriveIntakeCommand extends Command {

  private SuperstructureSubsystem m_intake;
  private Supplier<Double> guh;

  /**
   * Toggles the intake on/off, based on its current state and the default
   * intake speed in {@link SuperstructureConstants#kDefaultIntakeSpeed}
   */
  public DriveIntakeCommand(SuperstructureSubsystem intakeSubsystem, Supplier<Double> intakeSpeedSupplier) {
    m_intake = intakeSubsystem;
    guh = intakeSpeedSupplier;
  }

  @Override
  public void execute() {
    m_intake.runIntake(guh.get() * SuperstructureConstants.kDefaultIntakeSpeed);
  }

  @Override
  public void end(boolean interrupted) {
    m_intake.runIntake(0);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
