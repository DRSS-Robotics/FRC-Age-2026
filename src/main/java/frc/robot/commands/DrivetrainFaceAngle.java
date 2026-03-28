// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.FieldConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

import java.util.function.Supplier;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator3d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;

public class DrivetrainFaceAngle extends Command {

  private final CommandSwerveDrivetrain m_drivetrain;
  private final Supplier<Double> xSpeed;
  private final Supplier<Double> ySpeed;
  private final Supplier<Rotation2d> heading;
  private final SwerveRequest.FieldCentricFacingAngle faceDirection = new SwerveRequest.FieldCentricFacingAngle();

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public DrivetrainFaceAngle(CommandSwerveDrivetrain drivetrain, Supplier<Double> x, Supplier<Double> y,
      Supplier<Rotation2d> headingSupplier) {
    m_drivetrain = drivetrain;
    xSpeed = x;
    ySpeed = y;
    heading = headingSupplier;

    addRequirements(drivetrain);
  }


  @Override
  public void execute() {
    m_drivetrain.applyRequest(
        () -> faceDirection.withTargetDirection(heading.get())
            .withVelocityX(xSpeed.get())
            .withVelocityY(ySpeed.get())
            .withHeadingPID(1, 0, 0));
  }


  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
