// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.*;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.shooter.ShotCalculator;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj2.command.Command;

public class AutoShoot extends Command {

  private final ShooterSubsystem m_shooterSubsystem;
  private final Supplier<Pose3d> poseSupplier;
  private double horizDist;
  private double power;
  private RunLaunchMotor previousCommand;

  public AutoShoot(ShooterSubsystem shooter, Supplier<Pose3d> poseSupplier) {
    m_shooterSubsystem = shooter;
    this.poseSupplier = poseSupplier;
    
    addRequirements(m_shooterSubsystem);
  }


  @Override
  public void initialize() {}


  @Override
  public void execute() {
    m_shooterSubsystem.runLaunchMotor(
      DegreesPerSecond.of(calcShotPower(findDist(), ShooterConstants.kPitch, ShooterConstants.kHeight))
    );
  }

  public double findDist() {
    Pose3d robotPose = poseSupplier.get();
    Pose3d hubPose = FieldConstants.kHubPose;
    Transform3d robotTransform = robotPose.minus(hubPose);
    double horizDist = Math.sqrt(Math.pow(robotTransform.getX(), 2) + Math.pow(robotTransform.getY(), 2));
    
    return horizDist;
  }

  public static double calcShotPower(double horizDist, double pitch, double h_launcher){
        
    double numerator = FieldConstants.kGravIN * Math.pow(horizDist, 2);
    double denominator = 2 * (-72 + horizDist * Math.tan(pitch) + h_launcher) * Math.pow(Math.cos(pitch), 2);
    double power = Math.sqrt(numerator / denominator);

    return power;
  }


  @Override
  public void end(boolean interrupted) {
    m_shooterSubsystem.runLaunchMotor(DegreesPerSecond.of(0));
  }


  @Override
  public boolean isFinished() {
    return false;
  }
}
