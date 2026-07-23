package frc.robot;

import frc.robot.Constants.*;
import frc.robot.commands.WallInterpCommand;
import frc.robot.commands.SoupKickback;
import frc.robot.commands.ToggleIntakeCommand;
import frc.robot.commands.ToggleLaunchMotor;
import frc.robot.commands.ToggleWallCommand;
import frc.robot.commands.AutoCommands.ExpandStorageAutoCommand;
import frc.robot.commands.AutoCommands.IntakeAutoCommand;
import frc.robot.commands.AutoCommands.AutoShootMidDistance;
import frc.robot.commands.AutoCommands.TranslocatorAutoCommand;
import frc.robot.generated.TunerConstants;
import frc.robot.commands.DriveLaunchMotor;
import frc.robot.commands.DriveTransferCommand;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.QuestNavSystem;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;

public class RobotContainer {

  public final Pose3d hubPose = new Pose3d(0, 0, 0, Rotation3d.kZero);
  private final Pose2d initialPose = new Pose2d(2, 5, new Rotation2d(0));
  private final ShooterSubsystem m_shooter = new ShooterSubsystem(17, 19, 2,
      NetworkTableInstance.getDefault().getTable("Turret"));
  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

  private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per
  // second
  // max
  // angular velocity

  private double speedMultiplier = 1;
  private final double speedModifier = 0.35;
  private final double minSpeedMulti = 0.175;
  private final double slowSpeedMulti = 0.25;


  private double MaxSpeed = speedModifier * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts
                                                                                                // desired
  // top
  // speed

  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
      .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1)
      .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive
                                                               // motors
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

  // private final Telemetry logger = new Telemetry(MaxSpeed);

  private final CommandXboxController m_driverController = new CommandXboxController(
      OperatorConstants.kDriverControllerPort);
  private final CommandXboxController m_operatorController = new CommandXboxController(1);

  private final SuperstructureSubsystem m_superstructure = new SuperstructureSubsystem(
      SuperstructureConstants.kIntakeMotorId,
      SuperstructureConstants.kStorageMotorId,
      SuperstructureConstants.kSoupMotorId,
      SuperstructureConstants.kTransferMotorId,
      NetworkTableInstance.getDefault().getTable("Superstructure"));

  private final SendableChooser<Command> autoChooser;
  private final SendableChooser<Constants.Driver> driverChooser = new SendableChooser<Constants.Driver>();

  public final SwerveDrivePoseEstimator poseEstimator;
  private final Vision m_vision;
  private final QuestNavSystem m_questNav;

  private final Field2d gameField;

  public RobotContainer() {

    NamedCommands.registerCommand("Shoot", new AutoShootMidDistance(m_shooter));
    // NamedCommands.registerCommand("HangLv1", new HangUpAutoCommand(m_hang));
    // NamedCommands.registerCommand("LowerHang", new HangDownAutoCommand(m_hang));

    NamedCommands.registerCommand("Intake", new IntakeAutoCommand(m_superstructure));
    NamedCommands.registerCommand("OutIntake", new ExpandStorageAutoCommand(m_superstructure));
    NamedCommands.registerCommand("Transfer", new TranslocatorAutoCommand(m_superstructure));
    NamedCommands.registerCommand("RaiseIntakeHalfway", new WallInterpCommand(m_superstructure, () -> 0.5, true));

    autoChooser = AutoBuilder.buildAutoChooser("testAutoCommands");

    SmartDashboard.putData("Driver", driverChooser);
    SmartDashboard.putData("Auto Mode", autoChooser);
    
    // TODO: link initial pose to maybe a draggable object on a field? something like that
    // rotations affect counterclockwise, like a standard graph
    poseEstimator = new SwerveDrivePoseEstimator(
            drivetrain.getKinematics(), 
            drivetrain.getRotation3d().toRotation2d(),
            getModulePositions(),
            initialPose);
    poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.7, .7, 9999999));


    m_vision = new Vision(poseEstimator, drivetrain.getPigeon2(), drivetrain);

    m_questNav = new QuestNavSystem(poseEstimator, drivetrain, this);
    
    // making field2d and separate objects: one for each poseEst system
    gameField = new Field2d();
    SmartDashboard.putData("Field", gameField);
    // SmartDashboard.putData("PoseEstimator", poseEstimator.getEstimatedPosition());

    // FieldObject2d odometryRobot = gameField.getObject("JustOdometry");
    
    // we have this line so we trust the gyro less and trust the quest more
    drivetrain.setStateStdDevs(VecBuilder.fill(0.02, 0.02, 0.0872665*100));
    

    configureBindings();
    ElasticTelemetry.getInstance();
  }

  private void configureBindings() {

    m_operatorController.rightTrigger(0.05).whileTrue(
        new DriveLaunchMotor(m_shooter, () -> DegreesPerSecond
            .of(ShooterConstants.kShooterMaxManualSpeedDPS * 0.5 * (binDouble(
                Math.pow(m_operatorController.getRightTriggerAxis(),
                    0.75),
                12) + 0.225))));

    // back wall position
    m_operatorController.y().whileTrue(new ToggleLaunchMotor(m_shooter,
        () -> DegreesPerSecond.of(ShooterConstants.kShooterMaxManualSpeedDPS * 0.415),
        () -> false));
    // mid position
    m_operatorController.x().whileTrue(new ToggleLaunchMotor(m_shooter,
        () -> DegreesPerSecond.of(ShooterConstants.kShooterMaxManualSpeedDPS * 0.355),
        () -> false));
    // close position
    m_operatorController.a().whileTrue(new ToggleLaunchMotor(m_shooter,
        () -> DegreesPerSecond.of(ShooterConstants.kShooterMaxManualSpeedDPS * 0.315),
        () -> false));

    m_operatorController.b().onTrue(new ToggleIntakeCommand(m_superstructure));
    m_operatorController.rightBumper().whileTrue(new SoupKickback(m_superstructure));

    m_operatorController.leftBumper().onTrue(new ToggleWallCommand(m_superstructure));
    m_operatorController.leftTrigger(0.05)
        .whileTrue(new DriveTransferCommand(m_superstructure,
            m_operatorController::getLeftTriggerAxis));

    m_operatorController.povUp().whileTrue(new WallInterpCommand(m_superstructure, () -> 0., false));
    m_operatorController.povLeft().whileTrue(new WallInterpCommand(m_superstructure, () -> .5, true));
    m_operatorController.povRight().whileTrue(new WallInterpCommand(m_superstructure, () -> .5, true));
    m_operatorController.povDown().whileTrue(new WallInterpCommand(m_superstructure, () -> 1., false));

    drivetrain.setDefaultCommand(
        drivetrain.applyRequest(() -> drive
            .withVelocityX(-m_driverController.getLeftY() * MaxSpeed
                * speedMultiplier)
            .withVelocityY(-m_driverController.getLeftX() * MaxSpeed
                * speedMultiplier)
            .withRotationalRate(-m_driverController.getRightX() * MaxAngularRate
                * speedMultiplier)));

    final var idle = new SwerveRequest.Idle();
    RobotModeTriggers.disabled().whileTrue(
        drivetrain.applyRequest(() -> idle).ignoringDisable(true));

    m_driverController.a().whileTrue(drivetrain.applyRequest(() -> brake));
    m_driverController.x().onTrue(Commands.runOnce(() -> {m_questNav.resetPose(initialPose);}));
    m_driverController.b().whileTrue(drivetrain.applyRequest(() -> point
        .withModuleDirection(new Rotation2d(-m_driverController.getLeftY(),
            -m_driverController.getLeftX()))));


    // Note that each routine should be run exactly once in a single log.
//     m_driverController.back().and(m_driverController.y())
//         .whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
//     m_driverController.back().and(m_driverController.x())
//         .whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
//     m_driverController.start().and(m_driverController.y())
//         .whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
//     m_driverController.start().and(m_driverController.x())
//         .whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

    m_driverController.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

    m_driverController.leftTrigger().whileTrue(Commands.run(() ->

    {
      speedMultiplier = 1 / speedModifier;
      drive
          .withDeadband(MaxSpeed * 0.1 * speedMultiplier)
          .withRotationalDeadband(MaxAngularRate * 0.1 * speedMultiplier);
    })).onFalse(Commands.run(() -> {
      speedMultiplier = 1;
      drive.withDeadband(MaxSpeed * 0.1)
          .withRotationalDeadband(MaxAngularRate * 0.1);
    }));

    m_driverController.rightTrigger().whileTrue(Commands.run(() -> {
      speedMultiplier = minSpeedMulti
          + (1 - m_driverController.getRightTriggerAxis()) * (1 - minSpeedMulti);
      drive
          .withDeadband(MaxSpeed * 0.1 * speedMultiplier)
          .withRotationalDeadband(MaxAngularRate * 0.1 * speedMultiplier);
    })).onFalse(Commands.run(() -> {
      speedMultiplier = 1;
      drive.withDeadband(MaxSpeed * 0.1)
          .withRotationalDeadband(MaxAngularRate * 0.1);
    }));

    m_driverController.rightBumper().whileTrue(Commands.run(() ->

    {
      speedMultiplier = slowSpeedMulti;
      drive
          .withDeadband(MaxSpeed * 0.1 * speedMultiplier)
          .withRotationalDeadband(MaxAngularRate * 0.1 * speedMultiplier);
    })).onFalse(Commands.run(() -> {
      speedMultiplier = 1;
      drive.withDeadband(MaxSpeed * 0.1)
          .withRotationalDeadband(MaxAngularRate * 0.1);
    }));
  }

  private static double binDouble(double in, double bins) {
    return Math.round(in * bins) / bins;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }

  private SwerveModulePosition[] getModulePositions() {
    var modules = drivetrain.getModules();
    SwerveModulePosition[] modpos = new SwerveModulePosition[4];
    for(int x=0; x<4; x++){
        modpos[x] = modules[x].getPosition(true);
    }
    return modpos;
  }

  public void questNavUpdate(Pose2d measuredPose, double timestamp){
    poseEstimator.addVisionMeasurement(measuredPose, timestamp);
  }
  // this function should be called periodically
  public void updateOdometry(){
    if(!DriverStation.isDisabled()){
        poseEstimator.update(drivetrain.getRotation3d().toRotation2d(), getModulePositions());
    }
    m_vision.limelightPeriodic();
    m_questNav.periodicUpdate();
    
    
    gameField.setRobotPose(poseEstimator.getEstimatedPosition());
    
  }

}
