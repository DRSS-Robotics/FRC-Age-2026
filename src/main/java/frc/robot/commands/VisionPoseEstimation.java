// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.LimelightHelpers;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Vision;
import frc.robot.Constants.VisionConstants;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator3d;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static edu.wpi.first.units.Units.*;
import com.ctre.phoenix6.hardware.core.CorePigeon2;

/** An example command that uses an example subsystem. */
public class VisionPoseEstimation extends Command {
  @SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.SingularField" })
  private final Vision m_vision;
  private final SwerveDrivePoseEstimator3d m_poseEstimator;
  private final CorePigeon2 m_pigeon;
  private final CommandSwerveDrivetrain drivetrain;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public VisionPoseEstimation(Vision vision, SwerveDrivePoseEstimator3d poseEstimator, CorePigeon2 pigeon, CommandSwerveDrivetrain drivetrain) {
    m_vision = vision;
    m_poseEstimator = poseEstimator;
    m_pigeon = pigeon;
    this.drivetrain = drivetrain;

    // Use addRequirements() here to declare subsystem dependencies.
    // addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // m_vision.updateLimelightPosition();

    // Use April tag data to update swerve drive pose estimate (MegaTag2)
    LimelightHelpers.SetRobotOrientation(VisionConstants.kLimelightName,
        m_poseEstimator.getEstimatedPosition().getRotation().getZ(), 0, 0, 0, 0, 0);
    LimelightHelpers.PoseEstimate mt2 = LimelightHelpers
        .getBotPoseEstimate_wpiBlue_MegaTag2(VisionConstants.kLimelightName);
    // only update if angular velocity is less than 360 degrees per second and at
    // least 1 tag is detected
    if (Math.abs(m_pigeon.getAngularVelocityZWorld().getValue().in(DegreesPerSecond)) < 360 && mt2.tagCount > 0) {
      System.out.println("viewed");
      m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.7, .7, .7, 9999999));
      m_poseEstimator.addVisionMeasurement(new Pose3d(mt2.pose), mt2.timestampSeconds);
      drivetrain.addVisionMeasurement(mt2.pose, mt2.timestampSeconds);
    }

    // The following code is for testing purposes and should be commented out unless
    // testing
    // note that measurements are assumed to be in meters
    
    Pose2d estimatedPosition = m_poseEstimator.getEstimatedPosition().toPose2d();
    System.out.print("pose estimate: (");
    System.out.print(estimatedPosition.getX());
    System.out.print(", ");
    System.out.print(estimatedPosition.getY());
    System.out.println(")");
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
