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

    shooterSpeedMap.put(0.0, 3350.0);
    shooterSpeedMap.put(96.0, 3350.0);
    shooterSpeedMap.put(108.0, 3525.0);
    shooterSpeedMap.put(121.0, 3700.0);
    shooterSpeedMap.put(128.0, 3825.0);
    shooterSpeedMap.put(140.0, 3960.0);
    shooterSpeedMap.put(150.0, 4075.0);
    shooterSpeedMap.put(500.0, 4075.0);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    Translation2d distanceFromHub = Constants.kHubPoseCenter.getTranslation().minus(turretPoseSupplier.get().getTranslation());
    AngularVelocity speed = DegreesPerSecond.of(shooterSpeedMap.get(Meters.of(distanceFromHub.getNorm()).in(Inches)));
    m_subsystem.runShooterMotors(speed);
    SmartDashboard.putNumber("Dist from Hub", Meters.of(distanceFromHub.getNorm()).in(Inches));
    SmartDashboard.putNumber("rotatorrrr", speed.in(DegreesPerSecond));
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
