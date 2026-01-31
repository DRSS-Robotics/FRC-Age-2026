package frc.robot.commands;

import static edu.wpi.first.units.Units.Degrees;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.shooter.ShooterSubsystem;

public class SetPitchPositionCommand extends Command {

    public static final Angle ALLOWED_TOLERANCE = Degrees.of(ShooterConstants.kShooterAngleTolerance);
    ShooterSubsystem m_shooter;
    Angle target;
    double startTime;

    public SetPitchPositionCommand(ShooterSubsystem shooter, Angle degrees){
        m_shooter = shooter;
        target = degrees;

        addRequirements(shooter);
    }

    @Override
    public void initialize() {
        m_shooter.setPitchMotorPosition(target);
        startTime = Timer.getFPGATimestamp();

    }

    @Override
    public boolean isFinished() {
        // if the command has been running for more than a second or 
        // it has reached its target, end the command
        return m_shooter.getDistanceFromTarget().lte(ALLOWED_TOLERANCE) ||
             Timer.getFPGATimestamp() - startTime >= ShooterConstants.kSetPitchPositionTimeoutSeconds;
    }
}
