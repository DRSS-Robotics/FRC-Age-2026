// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.*;
import frc.robot.commands.AutoShoot;
import frc.robot.commands.Autos;
import frc.robot.commands.RunLaunchMotor;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.RotateToHub;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.TestMotor;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.shooter.TurretControl;
import frc.robot.commands.TestMotorOff;
import frc.robot.commands.TestMotorOn;

import java.util.function.Supplier;

import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator3d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final TestMotor m_testMotor = new TestMotor();
  private final Supplier<Pose3d> poseSupplier;
  private final Supplier<ChassisSpeeds> velocitySupplier;
  private final ShooterSubsystem m_shooterSubsystem;
  private final TurretControl m_turretController;

  // TODO: actually initialize a SwerveDrivePoseEstimator
  private final SwerveDrivePoseEstimator3d poseEstimator;
  private final SwerveModulePosition emptyPos = new SwerveModulePosition();
  public final Pose3d hubPose = new Pose3d(0, 0, 0, Rotation3d.kZero);

  private final ChassisSpeeds currentSpeed = new ChassisSpeeds();
  public final Pigeon2 pigeon = new Pigeon2(Constants.kPigeonID);
    

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  private final CommandXboxController m_operatorController =
      new CommandXboxController(OperatorConstants.kOperatorControllerPort);
  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    poseEstimator = new SwerveDrivePoseEstimator3d(
                  m_drivetrain.getKinematics(), 
                  m_drivetrain.getRotation3d(), 
                  getModulePositions(),
                  new Pose3d(0,0,0, null));
    
    poseSupplier = () -> getRobotPose3d();
    velocitySupplier = () -> getChassisSpeeds();
    m_turretController = new TurretControl(ShooterConstants.kTurretID);
    m_shooterSubsystem = new ShooterSubsystem(ShooterConstants.kPowerID, ShooterConstants.kTurretID, poseSupplier, velocitySupplier, m_turretController);
    // new AutoShoot(m_shooterSubsystem, 100);
    // Configure the trigger bindings
    configureBindings();
  }

  // private final CommandXboxController m_operatorController =
  // new CommandXboxController(OperatorConstants.kOperatorControllerPort);


  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));
    m_turretController.setDefaultCommand(new RotateToHub(m_turretController, m_shooterSubsystem));
    m_operatorController.leftBumper().whileTrue(new AutoShoot(m_shooterSubsystem, poseSupplier));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
    m_driverController.y().whileTrue(new TestMotorOn(m_testMotor));
    
   // m_operatorController.b().whileTrue(new EnableOuttake(outtakeSubsystem));
  
  }

  
  
  public Pose3d getRobotPose3d(){
    return poseEstimator.getEstimatedPosition();
  }
  public ChassisSpeeds getChassisSpeeds(){
    return currentSpeed;
  }

  public SwerveDrivePoseEstimator3d getRobotPoseEstimator() {
    return poseEstimator;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }

  public void updateOdometry() {
    poseEstimator.updateWithTime(Timer.getFPGATimestamp(), m_drivetrain.getRotation3d(), getModulePositions());
  }
  public void updateOdometry(Pose3d cameraDetectedPose) {

  }

  public SwerveModulePosition[] getModulePositions() {
    var modules = m_drivetrain.getModules();
    SwerveModulePosition[] modpos = new SwerveModulePosition[4];
    for(int x = 0; x < 4; x++) {
      modpos[x] = modules[x].getPosition();
    }
    return modpos;
  }
}
