// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class AutoPowerShoot extends Command {

  private final ShooterSubsystem m_subsystem;
  private Supplier<Boolean> endOnTrue;
  private Supplier<Pose2d> turretPoseSupplier;

  // Essentially works as a function: input distance from the hub, returns power that will shoot into the hub correctly
  private InterpolatingDoubleTreeMap shooterSpeedMap = new InterpolatingDoubleTreeMap();

  public AutoPowerShoot(ShooterSubsystem shooter, Supplier<Pose2d> turretPoseSupplier, Supplier<Boolean> endCommandWhenTrue) {
    m_subsystem = shooter;
    endOnTrue = endCommandWhenTrue;
    this.turretPoseSupplier = turretPoseSupplier;
    addRequirements(shooter);

    shooterSpeedMap.put(0.0, 18900.0);
    shooterSpeedMap.put(96.0, 18900.0);
    shooterSpeedMap.put(108.0, 20028.0);
    shooterSpeedMap.put(121.0, 21015.7);
    shooterSpeedMap.put(128.0, 21579.9);
    shooterSpeedMap.put(140.0, 22341.5);
    shooterSpeedMap.put(150.0, 23131.4);
    shooterSpeedMap.put(169.0, 24823.9);
    shooterSpeedMap.put(178.0, 25670.2);
    shooterSpeedMap.put(5000.0, 25388.1);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    Distance distanceFromHub = Meters.of(Constants.kHubPoseCenter.getTranslation().getDistance(turretPoseSupplier.get().getTranslation()));
    AngularVelocity speed = DegreesPerSecond.of(shooterSpeedMap.get(distanceFromHub.in(Inches)));
    // m_subsystem.runShooterMotors(speed);
    double scaleFactor = 5.6418;
    m_subsystem.runShooterMotors(speed);
    // m_subsystem.runShooterMotors(4250);
    SmartDashboard.putNumber("Dist from Hub", distanceFromHub.in(Inches));
    SmartDashboard.putNumber("Shooter/Shooter Speed (DPS)", speed.in(DegreesPerSecond));
  }

  @Override
  public void end(boolean interrupted) {
    m_subsystem.runShooterMotors(
        DegreesPerSecond.of(0));
  }

  @Override
  public boolean isFinished() {
    return endOnTrue.get();
  }
}
