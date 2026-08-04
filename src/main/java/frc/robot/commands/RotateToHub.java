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
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
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

  private Pose2d mainPose;


  StructPublisher<Translation2d> publisher = NetworkTableInstance.getDefault()
    .getTable("SmartDashboard")
    .getSubTable("translationjohn")
    .getStructTopic("Value", Translation2d.struct)
    .publish();

  public RotateToHub(TurretSubsystem turret, PoseEstimator poseEstimator) {
    m_turretSubsystem = turret;
    this.poseEstimator = poseEstimator;

    addRequirements(turret);

  }

  public RotateToHub(TurretSubsystem turret, Pose2d poser) {
    m_turretSubsystem = turret;
    mainPose = poser;

    addRequirements(turret);

  }
  
  @Override
  public void execute() {
    // Get rotation of turret relative to robot, must be rotated 180deg to be accurate
    Angle relativeTurretRotation = (Degrees.of(
        mapUnclamped(m_turretSubsystem.getTurretAngle().in(Degrees),-90,90,0,180)))
        .plus(ShooterConstants.kShooterYawOffset);

    // get field pose of turret with the rotation of the robot 
    // Pose2d robotPose = poseEstimator.getEstimatedPosition();
    Pose2d robotPose = mainPose;

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


    Angle rotationNeeded = hubAzimuth.minus(Degrees.of(turretPose.getRotation().getDegrees()));
    // //Gets the secant of distToRobot/txnc to find the offset angle to the hub
    // double targetAngle = Math.toDegrees(1/Math.cos(targetOffsetDistance/targetOffsetHorizontal));
    // //Determine how many ticks are needed to turn to the target angle
    // double targetTicks = targetAngle * ticksPerDegree;
    //speed that turret rotates
    SmartDashboard.putNumber("Hub Angle Needed", rotationNeeded.in(Degrees));
    SmartDashboard.putNumber("rotatorrrr", turretPose.getRotation().getDegrees());
    SmartDashboard.putNumber("Hub Azimuth", hubAzimuth.in(Degrees));
    SmartDashboard.putNumber("actual angel thus can be wrong", Math.atan2(distanceFromHub.getY(),distanceFromHub.getX()));
    double speed = 0.3;

    System.out.println(rotationNeeded.in(Degrees));
    publisher.set(distanceFromHub);
    //Sets the motor position to the target angle
    // m_turretControl.runTurretMotor(speed, targetTicks);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  public static double mapUnclamped(double val, double inMin, double inMax, double outMin, double outMax){
    return outMin + ((val - inMin) * (outMax - outMin)) / (inMax - inMin);
  }


  public Angle hubOffsetAngle(Pose2d turretPose){
    Translation2d distanceFromHub = Constants.kHubPoseCenter.getTranslation().minus(turretPose.getTranslation());
    
    boolean posX = (distanceFromHub.getX() > 0);
    boolean posY = (distanceFromHub.getY() > 0);

    if(posX && posY){
      return Radians.of(Math.PI / 2);
    } else if(!posX && posY){
      return Radians.of(Math.PI);
    } else if(!posX && !posY){
      return Radians.of(3 * Math.PI / 2);
    } else if(posX && !posY){
      return Radians.of(0);
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