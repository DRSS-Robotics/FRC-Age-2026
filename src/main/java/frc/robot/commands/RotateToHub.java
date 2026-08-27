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

import edu.wpi.first.math.MathUtil;
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

  StructPublisher<Pose2d> PosePublisher = NetworkTableInstance.getDefault()
    .getTable("SmartDashboard")
    .getSubTable("turretPose")
    .getStructTopic("Value", Pose2d.struct)
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
    Angle relativeTurretRotation = m_turretSubsystem.getTurretAngle().plus(ShooterConstants.kShooterYawOffset);

    // get field pose of turret with the rotation of the robot 
    // TODO: actually add in realistic pose, instead of static positions
    // Pose2d robotPose = poseEstimator.getEstimatedPosition();
    Pose2d robotPose = mainPose;
    
    // This is NOT actually the absolute rotation of the turret, it is the rotation from the rotation's pole to 
    // face the center of the turret, so calculations can be held here
    Rotation2d absoluteTurretCenterRotation = robotPose.getRotation().plus(new Rotation2d(ShooterConstants.kShooterYawOffset));
    
    // add the turret relative pose to the robot pose, where the turret pose is rotated by robot rotation with turret yaw offset
    // the rotation of turret pose is the relative plus robot rotation
    Pose2d turretPose = new Pose2d(robotPose.getTranslation().plus((m_turretSubsystem.turretOffset)
                                  .rotateBy(absoluteTurretCenterRotation)), 
                                  new Rotation2d(relativeTurretRotation.plus(
                                  Degrees.of(robotPose.getRotation().getDegrees()))));

    // fill this out with a reference to rad per second, whether from pigeon or pose
    double omegaRadiansPerSecond = 0;

    Translation2d turretLinearMomentum = new Translation2d(
        ShooterConstants.kShooterSideOffset.times(-omegaRadiansPerSecond),
        ShooterConstants.kShooterForwardOffset.times(omegaRadiansPerSecond)
    );

    Translation2d distanceFromHub = Constants.kHubPoseCenter.getTranslation().minus(turretPose.getTranslation());
    // Angle hubAzimuth = Degrees.of(MathUtil.inputModulus(
    //                               Radians.of(Math.PI/2-Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX())))
    //                               .plus(hubOffsetAngle(turretPose)).in(Degrees), -180, 180));

    Angle hubAzimuth = hubOffsetAngle(turretPose);

    // The system setup to find the angle input needed for turret rotation to face the hub is a bit strange
    // It will take the turret's initial offset (typically 180 deg) and subtract the turret's absolute rotation.
    // This might seem arbitrary, but it's done because the rotation needed is a difference in angles
    Angle rotationNeededFromCenter = normalizeAngle(hubAzimuth.plus(ShooterConstants.kShooterYawOffset)
                                                    .minus(Degrees.of(robotPose.getRotation().getDegrees())));
    
    Angle rotationNeeded = normalizeAngle(hubAzimuth.plus(ShooterConstants.kShooterYawOffset)
                                                    .minus(Degrees.of(robotPose.getRotation().getDegrees())
                                                    .plus(m_turretSubsystem.getTurretAngle())));

    // //Gets the secant of distToRobot/txnc to find the offset angle to the hub
    // double targetAngle = Math.toDegrees(1/Math.cos(targetOffsetDistance/targetOffsetHorizontal));
    // //Determine how many ticks are needed to turn to the target angle
    // double targetTicks = targetAngle * ticksPerDegree;
    //speed that turret rotates
    SmartDashboard.putNumber("Hub Angle Needed", rotationNeeded.in(Degrees));
    SmartDashboard.putNumber("rotatorrrr", turretPose.getRotation().getDegrees());
    SmartDashboard.putNumber("Hub Azimuth", hubAzimuth.in(Degrees));
    SmartDashboard.putNumber("actual angel thus can be wrong", Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    double speed = 0.3;

    boolean canRotate = Math.abs(rotationNeeded.in(Degrees)) < 90;
    SmartDashboard.putBoolean("Can Rotate to autoaim", canRotate);
    SmartDashboard.putNumber("fromcenter", rotationNeededFromCenter.in(Degrees));

    publisher.set(distanceFromHub);
    PosePublisher.set(turretPose);
    //Sets the motor position to the target angle
    // m_turretControl.runTurretMotor(speed, targetTicks);

    SmartDashboard.putBoolean("Over/X", distanceFromHub.getX() > 0);
    SmartDashboard.putBoolean("Over/Y", distanceFromHub.getY() > 0);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  public static double mapUnclamped(double val, double inMin, double inMax, double outMin, double outMax){
    return outMin + ((val - inMin) * (outMax - outMin)) / (inMax - inMin);
  }

  // This function gives the angle needed to rotate from the top axis (X+ axis, or forward in most cases)
  // to the hub, given the turret's pose on the field
  public Angle hubOffsetAngle(Pose2d turretPose){
    Translation2d distanceFromHub = Constants.kHubPoseCenter.getTranslation().minus(turretPose.getTranslation());
    
    // getting variables for which quadrant the turret is in relative to the hub
    boolean posX = (distanceFromHub.getX() > 0);
    boolean posY = (distanceFromHub.getY() > 0);

    double angleOffset = 0;
    double angle = Math.atan(distanceFromHub.getY() / distanceFromHub.getX());

    // Coordinate angle offset jank: because atan only returns values from -pi/2 to pi/2,
    // we need to offset the angle based on which quadrant the turret is in relative to the hub
    // Also, if the angle returned from atan is not meant to add onto the corected axis, we need to subtract it from pi/2
    // which is why this is handled in a function. Janky solution, but best I could think of
    if(posX && posY){
      angleOffset = 0;
      angle = Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    } else if(posX && !posY){
      angleOffset = 3 * Math.PI / 2;
      angle = Math.PI/2-Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    } else if(!posX && !posY){
      angleOffset = Math.PI;
      angle = Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    } else if(!posX && posY){
      angleOffset = Math.PI / 2;
      angle = Math.PI/2-Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    }

    // the angleModulus is to make sure the angle is between -pi and pi
    return normalizeAngle(angle + angleOffset);
  }

  public Angle normalizeAngle(double angle){
    return Radians.of(MathUtil.angleModulus(angle));
  }

  public Angle normalizeAngle(Angle angle){
    return Radians.of(MathUtil.angleModulus(angle.in(Radians)));
  }


  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public InterruptionBehavior getInterruptionBehavior() {
    return InterruptionBehavior.kCancelIncoming;
  }
}