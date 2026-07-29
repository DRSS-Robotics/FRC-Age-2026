package frc.robot.subsystems.shooter.Turret;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class RotateToTag extends Command {

    private final TurretSubsystem m_turret;
    private final PIDController m_visionPID;
    private boolean m_scanningLeft = true;
    private static final double SCANNING_SPEED_DPS = 40.0;

    // Maximum speed allowed during autonomous tracking (in Degrees per Second)
    private static final double MAX_VISION_SPEED_DPS = 250.0;

    public RotateToTag(TurretSubsystem turret) {
        m_turret = turret;

        m_visionPID = new PIDController(4.0, 0.0, 0.05);

        m_visionPID.setSetpoint(0.0);

        addRequirements(turret);
    }

    @Override
    public void initialize() {
        m_visionPID.reset();
    }

    @Override
    public void execute() {
        boolean hasTarget = NetworkTableInstance.getDefault()
                .getTable("AprilTagDetectionPipeline").getEntry("tv").getDouble(1) == 1.0;

        double tx = NetworkTableInstance.getDefault()
                .getTable("AprilTagDetectionPipeline").getEntry("tx").getDouble(1);

        if (hasTarget) {

            double targetChassisRotation = tx / 360.0;
            double currentTurretRotation = m_turret.getTurretAngle();

            double rotationError = MathUtil.inputModulus(targetChassisRotation -
                    currentTurretRotation, -0.5, 0.5);

            // roughly 7 degrees
            if (Math.abs(rotationError) < 0.019) {
                m_turret.setTurretVelocity(0.0);
                System.out.println("VISION LOCK - TARGET ARRIVED AND BRAKED");
                return;
            }

            double targetKp = 300.0;
            double targetVelocityDPS = rotationError * targetKp;
            double finalVelocityDPS = Math.max(-200.0, Math.min(200.0,
                    targetVelocityDPS));

            m_turret.setTurretVelocity(finalVelocityDPS);

            System.out.println("VISION - Error: " + rotationError + " | Target Speed: " +
                    finalVelocityDPS);
        } else {
            // If the camera loses sight of the tag, stop the turret motor completely
            m_turret.setTurretVelocity(0.0);
        }
        SmartDashboard.putBoolean("Turret On Target", false);

    }

    @Override
    public void end(boolean interrupted) {
        m_turret.setTurretVelocity(0.0);

        SmartDashboard.putBoolean("Turret On Target", false);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}

// private final TurretSubsystem m_turret;

// public RotateToTag(TurretSubsystem turret) {
// m_turret = turret;
// // Require the turret subsystem so it cancels manual joystick control
// addRequirements(turret);
// }

// @Override
// public void initialize() {
// }

// @Override
// public void execute() {
// // 1. Read target visibility (tv) and horizontal angle error (tx) from the
// // chassis Limelight
// boolean hasTarget = NetworkTableInstance.getDefault()
// .getTable("AprilTagDetectionPipeline").getEntry("tv").getDouble(1) == 1.0;

// double tx = NetworkTableInstance.getDefault()
// .getTable("AprilTagDetectionPipeline").getEntry("tx").getDouble(1);

// // TEMPORARY DEBUG PRINTS: Look at your RioLog / VS Code Terminal
// System.out.println("DEBUG - Camera Has Target: " + hasTarget + " | tx
// Offset:" + tx);

// if (hasTarget) {

// double tagOffsetRotations = tx / 360.0;
// double currentTurretRotations = m_turret.getTurretAngle();
// double targetTurretRotations = currentTurretRotations + tagOffsetRotations;

// m_turret.setTurretPosition(targetTurretRotations);
// } else {
// // If the camera doesn't see a tag, hold the motor completely still where it
// is
// m_turret.setTurretVelocity(0.0);
// }
// }

// @Override
// public void end(boolean interrupted) {
// // Safely stop motor power when the command ends or button is released
// m_turret.setTurretVelocity(0.0);
// }

// @Override
// public boolean isFinished() {
// // Return false so it actively tracks if the robot or tag moves while holding
// // the button
// return false;
// }
// }
