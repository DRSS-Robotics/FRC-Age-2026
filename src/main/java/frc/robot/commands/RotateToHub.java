// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.FieldConstants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.shooter.*;

import static edu.wpi.first.units.Units.Inches;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator3d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class RotateToHub extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final CommandSwerveDrivetrain m_drivetrain;
  private final SwerveDrivePoseEstimator3d poseEstimator;
  private final SwerveRequest.FieldCentricFacingAngle faceDirection = new SwerveRequest.FieldCentricFacingAngle();

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public RotateToHub(CommandSwerveDrivetrain drivetrain, SwerveDrivePoseEstimator3d poseEstimator) {
    m_drivetrain = drivetrain;
    this.poseEstimator = poseEstimator;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(drivetrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    Pose3d hubPose = DriverStation.getAlliance().get() == DriverStation.Alliance.Blue ? FieldConstants.kHubPoseBlue : FieldConstants.kHubPoseRed;

    //Rotation3d rotationNeeded = hubPose

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // return (Math.atan(
    //     abs(
    //         toHub.getMeasureY().in(Inches)/toHub.getMeasureX().in(Inches)
    //     )) * 180 / Math.PI);

    //faceDirection.withTargetDirection();
  }


  private int getQuadrantOffset(Transform3d toHub) {
    // int binaryID = 0;
    // if (huboffset.getMeasureX().in(Inches)>0) {
    //     binaryID+=2;
    // }
    // if (huboffset.getMeasureY().in(Inches)>0) {
    //     binaryID+=1;
    // }
    // switch (binaryID) {
    //     case 0:
    //         return 0;
    //     case 1:
    //         return 270;
    //     case 2:
    //         return 90;
    //     case 3:
    //         return 180;
    // }

    return 0;
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
