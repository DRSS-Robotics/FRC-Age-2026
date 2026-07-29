// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.subsystems.shooter.Turret;

// import frc.robot.LimelightHelpers;
// import frc.robot.Constants;
// import frc.robot.Constants.ShooterConstants;
// import frc.robot.Constants.VisionConstants;
// import frc.robot.LimelightHelpers.RawFiducial;

// import static edu.wpi.first.units.Units.Degrees;

// import java.util.function.Supplier;

// import edu.wpi.first.math.estimator.PoseEstimator;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.units.measure.Angle;
// import edu.wpi.first.wpilibj2.command.Command;

// public class RotateToHub extends Command {

//   private final TurretControl m_turretControl;
//   // Thorughbore encoder specs
//   private final double totalTicksPerRev = 8192;
//   // Calculates how many degrees are in 1 tick
//   private final double degreesPerTick = 360/totalTicksPerRev;

//   private PoseEstimator poseEstimator;

//   public RotateToHub(TurretControl turret, PoseEstimator poseEstimator) {
//     m_turretControl = turret;
//     this.poseEstimator = poseEstimator;

//     addRequirements(turret);

//   }
  
//   @Override
//   public void execute() {
//     // Get rotation of turret relative to robot, must be rotated 180deg to be accurate
//     Angle relativeTurretRotation = Degrees.of(m_turretControl.getEncoderTicks() * degreesPerTick).plus(ShooterConstants.kShooterYawOffset);

//     // get field pose of turret with the rotation of the robot 
//     Pose2d robotPose = poseEstimator.getEstimatedPosition();
//     Pose2d turretPose = new Pose2d(robotPose.getTranslation().plus((m_turretControl.turretOffset).rotateBy(robotPose.getRotation())), 
//                                           new Rotation2d(relativeTurretRotation.plus(Degrees.of(robotPose.getRotation().getDegrees()))));

//     // fill this out with a reference to rad per second, whether from pigeon or pose
//     double omegaRadiansPerSecond = 0;

//     Translation2d turretLinearMomentum = new Translation2d(
//         ShooterConstants.kShooterSideOffset.times(-omegaRadiansPerSecond),
//         ShooterConstants.kShooterForwardOffset.times(omegaRadiansPerSecond)
//     );

//     Translation2d distanceFromHub = Constants.kHubPoseCenter.getTranslation().minus(turretPose.getTranslation());
//     // //Gets the secant of distToRobot/txnc to find the offset angle to the hub
//     // double targetAngle = Math.toDegrees(1/Math.cos(targetOffsetDistance/targetOffsetHorizontal));
//     // //Determine how many ticks are needed to turn to the target angle
//     // double targetTicks = targetAngle * ticksPerDegree;
//     //speed that turret rotates
//     double speed = 0.3;
//     //Sets the motor position to the target angle
//     // m_turretControl.runTurretMotor(speed, targetTicks);
//   }

//   @Override
//   public boolean isFinished() {
//     return false;
//   }

//   @Override
//   public void end(boolean interrupted) {
//   }

//   @Override
//   public InterruptionBehavior getInterruptionBehavior() {
//     return InterruptionBehavior.kCancelIncoming;
//   }
// }