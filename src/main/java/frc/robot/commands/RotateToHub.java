// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.shooter.TurretControl;
import frc.robot.LimelightHelpers;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers.RawFiducial;

import static edu.wpi.first.units.Units.Degrees;

import java.util.function.Supplier;

import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;

public class RotateToHub extends Command {

  private final TurretControl m_turretControl;
  // Thorughbore encoder specs
  private final double totalTicksPerRev = 8192;
  // Calculates how many degrees are in 1 tick
  private final double degreesPerTick = 360/totalTicksPerRev;

  private PoseEstimator poseEstimator;

  public RotateToHub(TurretControl turret, PoseEstimator poseEstimator) {
    m_turretControl = turret;
    this.poseEstimator = poseEstimator;

    addRequirements(turret);

  }
  
  @Override
  public void execute() {
    // Get rotation of turret relative to robot, must be rotated 180deg to be accurate
    Angle relativeTurretRotation = Degrees.of(m_turretControl.getEncoderTicks() * degreesPerTick).plus(ShooterConstants.kShooterYawOffset);

    Pose2d robotPose = poseEstimator.getEstimatedPosition();
    Pose2d turretPose = new Pose2d(robotPose.getTranslation().plus((m_turretControl.turretOffset).rotateBy(robotPose.getRotation())), 
                                          new Rotation2d(relativeTurretRotation.plus(Degrees.of(robotPose.getRotation().getDegrees()))));

    // //Gets the secant of distToRobot/txnc to find the offset angle to the hub
    // double targetAngle = Math.toDegrees(1/Math.cos(targetOffsetDistance/targetOffsetHorizontal));
    // //Determine how many ticks are needed to turn to the target angle
    // double targetTicks = targetAngle * ticksPerDegree;
    //speed that turret rotates
    double speed = 0.3;
    //Sets the motor position to the target angle
    m_turretControl.runTurretMotor(speed, targetTicks);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public InterruptionBehavior getInterruptionBehavior() {
    return InterruptionBehavior.kCancelIncoming;
  }
}

// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.kinematics.ChassisSpeeds;

// // Where is the turret relative to the center of the robot?
// // (e.g., 0.2 meters forward, 0.0 meters left/right)
// final Translation2d TURRET_OFFSET_ROBOT_RELATIVE = new Translation2d(0.2, 0.0);

// // Get current state
// Pose2d robotPose = poseEstimator.getEstimatedPosition();
// ChassisSpeeds fieldSpeeds = getFieldRelativeSpeeds(); 

// // --- 1. POSITION OFFSET ---
// // Rotate the robot-relative offset into the field frame
// Translation2d turretOffsetFieldRelative = TURRET_OFFSET_ROBOT_RELATIVE.rotateBy(robotPose.getRotation());

// // Add the offset to the robot's center to get the turret's actual (X,Y) on the field
// Translation2d turretFieldPose = robotPose.getTranslation().plus(turretOffsetFieldRelative);

// // --- 2. VELOCITY OFFSET (Tangential Velocity) ---
// // Formula for tangential velocity: v = omega x r
// // v_x = -omega * y_offset, v_y = omega * x_offset
// double omegaRadiansPerSecond = fieldSpeeds.omegaRadiansPerSecond;
// Translation2d turretTangentialVelocityRobotRelative = new Translation2d(
//     -omegaRadiansPerSecond * TURRET_OFFSET_ROBOT_RELATIVE.getY(),
//     omegaRadiansPerSecond * TURRET_OFFSET_ROBOT_RELATIVE.getX()
// );

// // Rotate the tangential velocity to the field frame
// Translation2d turretTangentialVelocityFieldRelative = turretTangentialVelocityRobotRelative.rotateBy(robotPose.getRotation());

// // Add tangential velocity to the robot's driving velocity
// Translation2d totalTurretVelocity = new Translation2d(
//     fieldSpeeds.vxMetersPerSecond, 
//     fieldSpeeds.vyMetersPerSecond
// ).plus(turretTangentialVelocityFieldRelative);


// // --- 3. TARGETING MATH ---
// // Now do the shoot-on-the-move math using the TURRET'S position and velocity
// Translation2d targetHub = new Translation2d(4.03, 4.04); // Assuming Alliance-Relative odometry

// // Direct vector from the TURRET to the hub
// Translation2d directTargetVector = targetHub.minus(turretFieldPose);
// double distanceToHub = directTargetVector.getNorm();

// // Time of flight
// double timeOfFlight = distanceToHub / 12.0; // 12.0 m/s cargo exit velocity

// // Virtual target offset using the TURRET'S total velocity
// Translation2d turretDisplacement = totalTurretVelocity.times(timeOfFlight);
// Translation2d virtualTargetVector = directTargetVector.minus(turretDisplacement);

// // Final angles (same as before)
// Rotation2d fieldCentricAimAngle = virtualTargetVector.getAngle();
// Rotation2d turretAngleRelativeToChassis = fieldCentricAimAngle.minus(robotPose.getRotation());