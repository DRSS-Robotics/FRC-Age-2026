// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.LimelightHelpers;
import frc.robot.subsystems.shooter.Turret.TurretSubsystem;
import frc.robot.Constants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.VisionConstants;
import frc.robot.LimelightHelpers.RawFiducial;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;

import java.util.function.Supplier;

import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class RotateToHub extends Command {

  private final TurretSubsystem m_turretSubsystem;
  // Thorughbore encoder specs
  private final double totalTicksPerRev = 8192;
  // Calculates how many degrees are in 1 tick
  private final double degreesPerTick = 360/totalTicksPerRev;

  private PoseEstimator poseEstimator;

  public RotateToHub(TurretSubsystem turret, PoseEstimator poseEstimator) {
    m_turretSubsystem = turret;
    this.poseEstimator = poseEstimator;

    addRequirements(turret);

  }
  
  @Override
  public void execute() {
    // Get rotation of turret relative to robot, must be rotated 180deg to be accurate
    Angle relativeTurretRotation = m_turretSubsystem.getTurretAngle().plus(ShooterConstants.kShooterYawOffset);

    // get field pose of turret with the rotation of the robot 
    Pose2d robotPose = poseEstimator.getEstimatedPosition();
    Pose2d turretPose = new Pose2d(robotPose.getTranslation().plus((m_turretSubsystem.turretOffset).rotateBy(robotPose.getRotation())), 
                                          new Rotation2d(relativeTurretRotation.plus(Degrees.of(robotPose.getRotation().getDegrees()))));

    // fill this out with a reference to rad per second, whether from pigeon or pose
    double omegaRadiansPerSecond = 0;

    Translation2d turretLinearMomentum = new Translation2d(
        ShooterConstants.kShooterSideOffset.times(-omegaRadiansPerSecond),
        ShooterConstants.kShooterForwardOffset.times(omegaRadiansPerSecond)
    );

    Translation2d distanceFromHub = Constants.kHubPoseCenter.getTranslation().minus(turretPose.getTranslation());
    Angle hubAzimuth = Radians.of(Math.atan2(distanceFromHub.getY(),distanceFromHub.getX())).plus(hubOffsetAngle(turretPose));    


    Angle rotationNeeded = relativeTurretRotation.plus(hubAzimuth).plus(Degrees.of(robotPose.getRotation().getDegrees()));
    // //Gets the secant of distToRobot/txnc to find the offset angle to the hub
    // double targetAngle = Math.toDegrees(1/Math.cos(targetOffsetDistance/targetOffsetHorizontal));
    // //Determine how many ticks are needed to turn to the target angle
    // double targetTicks = targetAngle * ticksPerDegree;
    //speed that turret rotates
    SmartDashboard.putNumber("Hub Angle Needed", rotationNeeded.in(Degrees));
    double speed = 0.3;
    //Sets the motor position to the target angle
    // m_turretControl.runTurretMotor(speed, targetTicks);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  public static double mapUnclamped(double val, double inMin, double inMax, double outMin, double outMax){
    return outMin + (val - inMin) * (outMax - outMin) / (inMax - inMin);
  }

  public Angle hubOffsetAngle(Pose2d turretPose){
    Translation2d distanceFromHub = Constants.kHubPoseCenter.getTranslation().minus(turretPose.getTranslation());
    
    boolean posX = (Constants.kHubPoseCenter.getMeasureX().minus(turretPose.getMeasureX())).in(Meters) > 0;
    boolean posY = (Constants.kHubPoseCenter.getMeasureY().minus(turretPose.getMeasureY())).in(Meters) > 0;

    if(posX && posY){
      return Radians.of(Math.atan2(distanceFromHub.getY(),distanceFromHub.getX()) + (Math.PI / 2));
    } else if(!posX && posY){
      return Radians.of(Math.atan2(distanceFromHub.getY(),distanceFromHub.getX()) + Math.PI);
    } else if(!posX && !posY){
      return Radians.of(Math.atan2(distanceFromHub.getY(),distanceFromHub.getX()) + (3 * Math.PI / 2));
    } else if(posX && !posY){
      return Radians.of(Math.atan2(distanceFromHub.getY(),distanceFromHub.getX()));
    }

    return null;
  }


  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public InterruptionBehavior getInterruptionBehavior() {
    return InterruptionBehavior.kCancelIncoming;
  }
}