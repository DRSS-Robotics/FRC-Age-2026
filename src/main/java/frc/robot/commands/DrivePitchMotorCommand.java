package frc.robot.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.shooter.ShooterSubsystem;

public class DrivePitchMotorCommand extends Command {

    private ShooterSubsystem m_shooterSubsystem;
    private Supplier<Double> speed;

    public DrivePitchMotorCommand(ShooterSubsystem shooter, Supplier<Double> speedSupplier) {
        speed = speedSupplier;
        m_shooterSubsystem = shooter;
        addRequirements(m_shooterSubsystem);
    }

    @Override
    public void execute() {
        m_shooterSubsystem.setPitchMotorPosition(speed.get() * ShooterConstants.kShooterManualDriveDPSScale);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
    
}
