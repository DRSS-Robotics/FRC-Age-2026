package frc.robot.subsystems.shooter.Turret;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.shooter.Turret.TurretSubsystem;

public class DriveTurretToDashboard extends Command {
    private final TurretSubsystem m_turret;

    public DriveTurretToDashboard(TurretSubsystem turret) {
        m_turret = turret;
        addRequirements(turret);
    }

    @Override
    public void execute() {
        // Read the absolute mechanism rotation requested on Elastic
        double targetColumnRotation = m_turret.getDashboardTargetRotations();
        
        // Command the subsystem to move to that position with 0 feedforward
        m_turret.setTurretPosition(targetColumnRotation, 0.0);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop moving when the command ends
        m_turret.setTurretVelocity(0.0);
    }
}