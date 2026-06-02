// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import static edu.wpi.first.units.Units.*;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.units.measure.Angle;
import frc.robot.Constants.VisionConstants;

import java.util.function.Supplier;
import java.lang.Math;
import com.ctre.phoenix6.hardware.core.CorePigeon2;

public class Vision extends SubsystemBase {

  private HttpCamera limelight;

  private Supplier<Angle> robotYawSupplier;
  private SwerveDrivePoseEstimator poseEstimator;
  private CorePigeon2 pigeon;
  private CommandSwerveDrivetrain drivetrain;

  /** Creates a new Vision subsystem */
  public Vision(Supplier<Angle> robotYaw, SwerveDrivePoseEstimator poseEstimator, CorePigeon2 pigeon,
      CommandSwerveDrivetrain drivetrain) {
    robotYawSupplier = robotYaw;
    this.poseEstimator = poseEstimator;
    this.pigeon = pigeon;
    this.drivetrain = drivetrain;

    // Initialize Limelight
    setLimelightPipeline(VisionConstants.kLimelightAprilTagPipeline);
    limelight = new HttpCamera("limelight", VisionConstants.kLimelightStreamURL);
    limelight.setConnectionStrategy(ConnectionStrategy.kKeepOpen);

    // Camera status logging
    System.out.println("VISION: Limelight connected: " + limelight.isConnected());

    LimelightHelpers.setCameraPose_RobotSpace(VisionConstants.kLimelightName,
        /* forward offset */ VisionConstants.kLimelightForwardOffset.in(Meters),
        /* side offset */ VisionConstants.kLimelightSideOffset.in(Meters),
        /* height offset */ VisionConstants.kLimelightHeightOffset.in(Meters),
        /* roll offset */ 0,
        /* pitch offset */ VisionConstants.kLimelightPitchOffset.in(Degrees),
        /* yaw offset */ VisionConstants.kLimelightYawOffset.in(Degrees));
  }

  /** Set the Limelight's pipeline */
  public void setLimelightPipeline(int index) {
    LimelightHelpers.setPipelineIndex(VisionConstants.kLimelightName, index);
    System.out.println("VISION: Set Limelight pipeline to " + index);
  }

  @Override
  public void periodic() {
    // Use April tag data to update swerve drive pose estimate (MegaTag2)
    LimelightHelpers.SetRobotOrientation(VisionConstants.kLimelightName,
        robotYawSupplier.get().in(Degrees), 0, 0, 0, 0, 0);
    LimelightHelpers.PoseEstimate mt2 = LimelightHelpers
        .getBotPoseEstimate_wpiBlue_MegaTag2(VisionConstants.kLimelightName);
    // only update if angular velocity is less than 360 degrees per second and at
    // least 1 tag is detected
    if (Math.abs(pigeon.getAngularVelocityZWorld().getValue().in(DegreesPerSecond)) < 360 && mt2.tagCount > 0) {
      poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.7, .7, 9999999));
      poseEstimator.addVisionMeasurement(mt2.pose, mt2.timestampSeconds);
      drivetrain.addVisionMeasurement(mt2.pose, mt2.timestampSeconds);
    }

  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
