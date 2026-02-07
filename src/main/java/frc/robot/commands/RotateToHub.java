// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.FieldConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.shooter.*;

import java.util.function.Supplier;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Inches;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class RotateToHub extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_shooterSubsystem;
  private final TurretControl m_turretController;
  private final Pose3d hubPose = FieldConstants.kHubPose;
  private final Supplier<Pose3d> poseSupplier;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public RotateToHub(ShooterSubsystem shooterSubsystem, Supplier<Pose3d> poseSupplier) {
    m_shooterSubsystem = shooterSubsystem;
    m_turretController = shooterSubsystem.getTurretControl();
    this.poseSupplier = poseSupplier;
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // get the current position
    Pose3d currentPose = poseSupplier.get();
    // get the yaw rotation
    Angle azimuth = currentPose.getRotation().getMeasureZ();
    // apply constant of turret dist from center
    currentPose.plus(new Transform3d(
      ShooterConstants.kCenterOffset*Math.cos(azimuth.in(Radians)),
      ShooterConstants.kCenterOffset*Math.sin(azimuth.in(Radians)),
      ShooterConstants.kHeight,new Rotation3d()));

    Transform3d huboffset = currentPose.minus(hubPose);
    m_turretController.setTurretPosition(Degrees.of(getQuadrantOffset()+getAngleToHub()));
  }
  private int getQuadrantOffset(Transform3d toHub) {
    int binaryID = 0;
    if (huboffset.getMeasureX().in(Inches)>0) {
        binaryID+=2;
    }
    if (huboffset.getMeasureY().in(Inches)>0) {
        binaryID+=1;
    }
    switch (binaryID) {
        case 0:
            return 0;
        case 1:
            return 270;
        case 2:
            return 90;
        case 3:
            return 180;
    }
  }
  private double getAngleToHub(Transform3d toHub) {
    return Math.atan(toHub.getMeasureY().in(Inches)/toHub.getMeasureX().in(Inches));
  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
