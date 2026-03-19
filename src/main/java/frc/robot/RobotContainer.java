package frc.robot;

import frc.robot.Constants.*;
import frc.robot.commands.DriveYawMotor;
import frc.robot.commands.RotateYawMotor;
import frc.robot.commands.SetWallPosition;
import frc.robot.commands.SoupKickback;
import frc.robot.commands.SetWallPosition;
import frc.robot.commands.SoupKickback;
import frc.robot.commands.ToggleIntakeCommand;
import frc.robot.commands.ToggleIntakeCommandReverse;
import frc.robot.commands.ToggleLaunchMotor;
import frc.robot.commands.ToggleLaunchMotor;
import frc.robot.commands.ToggleWallCommand;
import frc.robot.commands.AutoCommands.ExpandStorageAutoCommand;
import frc.robot.commands.AutoCommands.IntakeAutoCommand;
import frc.robot.commands.AutoCommands.AutoShootMidDistance;
import frc.robot.commands.AutoCommands.TranslocatorAutoCommand;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CommandSwerveDrivetrain;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.*;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.generated.TunerConstants;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.generated.TunerConstants;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator3d;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.ctre.phoenix6.hardware.core.CorePigeon2;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

    // TODO: actually initialize a SwerveDrivePoseEstimator
    // public SwerveDrivePoseEstimator m_poseEstimator = new
    // SwerveDrivePoseEstimator();

    public final Pose3d hubPose = new Pose3d(0, 0, 0, Rotation3d.kZero);
    private final ShooterSubsystem m_shooter = new ShooterSubsystem(17, 19, 2,
            NetworkTableInstance.getDefault().getTable("Turret"));
    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

        private double MaxSpeed = 0.5 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired
                                                                                            // top
        // speed
        private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per
                                                                                          // second
        // max
        // angular velocity

        Supplier<Angle> turretAngleSupplier = () -> {
                return Degrees.of(0);
        };
        SwerveDrivePoseEstimator3d poseEstimator;
        CorePigeon2 pigeon;
        public final Vision m_vision = new Vision(turretAngleSupplier, poseEstimator, pigeon);

        private double speedMultiplier = 1;
    private final double minSpeedMulti = 0.175;

        private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
                        .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1)
                        .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive
                                                                                 //
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
        
        Field2d field = new Field2d();

        private final SendableChooser<Command> autoChooser;

    public RobotContainer() {
                poseEstimator = new SwerveDrivePoseEstimator3d(
                                drivetrain.getKinematics(),
                                drivetrain.getRotation3d(),
                                getModulePositions(),
                                new Pose3d(0, 0, 0, new Rotation3d()));

                SmartDashboard.putData("Field", field);

        
        NamedCommands.registerCommand("Shoot", new AutoShootMidDistance(m_shooter));
        // NamedCommands.registerCommand("HangLv1", new HangUpAutoCommand(m_hang));
        // NamedCommands.registerCommand("LowerHang", new HangDownAutoCommand(m_hang));
        // //we have no hang for buckeye
        
        NamedCommands.registerCommand("Intake", new IntakeAutoCommand(m_superstructure));
        NamedCommands.registerCommand("OutIntake", new ExpandStorageAutoCommand(m_superstructure));
        NamedCommands.registerCommand("Transfer", new TranslocatorAutoCommand(m_superstructure));

        // Changed from default auto name- Micah plp
        autoChooser = AutoBuilder.buildAutoChooser("testAutoCommands");


        // Recently added- Micah plp
        SmartDashboard.putData("Auto Mode", autoChooser);

        // THIS IS ALL CODE FOR LIMELIGHT FEED- from PID tuning branch- Micah plp
        HttpCamera limelight = new HttpCamera("limelight", "http://limelight.local:5800");
        limelight.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
        MjpegServer outputStream = CameraServer.addSwitchedCamera("Output Stream");
        outputStream.setSource(limelight);

        configureBindings();
                // 77% max power from corner works well
    
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
                () -> DegreesPerSecond.of(ShooterConstants.kShooterMaxManualSpeedDPS * 0.3),
                () -> false));
        m_operatorController.b().onTrue(new ToggleIntakeCommand(m_superstructure));
        m_operatorController.rightBumper().onTrue(new ToggleIntakeCommandReverse(m_superstructure));
        // m_operatorController.x().onTrue(new ToggleIntakeCommand(m_superstructure));
        m_operatorController.a().onTrue(new ToggleWallCommand(m_superstructure));
        m_operatorController.leftTrigger(0.05)
                .whileTrue(new DriveTransferCommand(m_superstructure,
                        m_operatorController::getLeftTriggerAxis));
        // Untested but we need to make it so that transfer cannot run without shooter
        // running
        // m_operatorController.leftTrigger(0.15)
        // .whileTrue(new ToggleLaunchMotor(m_shooter, null, null));

                m_operatorController.leftBumper()
                                .whileTrue(new SoupKickback(m_superstructure).withTimeout(0.5));

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
        m_driverController.b().whileTrue(drivetrain.applyRequest(() -> point
                .withModuleDirection(new Rotation2d(-m_driverController.getLeftY(),
                        -m_driverController.getLeftX()))));

        // Note that each routine should be run exactly once in a single log.
        m_driverController.back().and(m_driverController.y())
                .whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        m_driverController.back().and(m_driverController.x())
                .whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        m_driverController.start().and(m_driverController.y())
                .whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        m_driverController.start().and(m_driverController.x())
                .whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));



                m_driverController.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        m_driverController.rightTrigger().whileTrue(Commands.run(() -> {
            speedMultiplier = minSpeedMulti
                    + (1 - m_driverController.getRightTriggerAxis()) * (1 - minSpeedMulti);
            drive
                    .withDeadband(MaxSpeed * 0.1 * speedMultiplier)
                    .withRotationalDeadband(MaxAngularRate * 0.1 * speedMultiplier);
        })).whileFalse(Commands.run(() -> {
            speedMultiplier = 1;
            drive.withDeadband(MaxSpeed * 0.1)
                    .withRotationalDeadband(MaxAngularRate * 0.1);
        }));

                m_driverController.y().toggleOnTrue(new SwapCamera(m_vision));
                m_driverController.a().onTrue(Commands.runOnce(() -> {
                        System.out.println("CAN DETECT: " + CameraServer.getServer(VisionConstants.kOutputStreamName));
                }));
                // This one switches the camera while the button is held
                m_driverController.x().whileTrue(new SwapCamera(m_vision));
                // drivetrain.registerTelemetry(logger::telemeterize);
                /*
                 * m_driverController.rightStick().whileFalse(
                 * new DriveYawMotor(m_shooter, () -> DegreesPerSecond.of(
                 * ShooterConstants.kTurretMaxManualSpeedDPS
                 * powPreserveSign(-m_driverController.getRightX(), 2.))));
                 * 
                 * m_driverController.rightStick().whileTrue(
                 * new RotateYawMotor(m_shooter, () -> Degrees
                 * .of(convertPositionToTurretAngle(
                 * m_driverController.getRightX(), m_driverController.getRightY()))));
                 */
        }

    private static double binDouble(double in, double bins) {
        return Math.round(in * bins) / bins;
    }

        // these should be moved to utils once we have utils class from superstrcuture
        // !!
        private static double powPreserveSign(double a, double b) {
                return Math.pow(Math.abs(a), b) * Math.signum(a);
        }

    private static int signInclusive(double a) {
        return (a >= 0.0) ? 1 : -1;
        // new Trigger(m_exampleSubsystem::exampleCondition)
        // .onTrue(new ExampleCommand(m_exampleSubsystem));
    }

        /**
         * Use this to pass the autonomous command to the main {@link Robot} class.
         *
         * @return the command to run in autonomous
         */
        public Command getAutonomousCommand() {
                return autoChooser.getSelected();
        }

        public Pose3d getRobotPose3d() {
                return poseEstimator.getEstimatedPosition();
        }

    // converts a m_driverController position into an angle that can be used by
    // turret set
    // position commands (straight forward on the joytick is 180 deg)
    private static double convertPositionToTurretAngle(double x, double y) {
        return (180 / Math.PI) * Math.atan(
                y / x) + (90.0 * (signInclusive(x) + 2));
    }

        public void updateOdometry(){
                poseEstimator.updateWithTime(Timer.getFPGATimestamp(), drivetrain.getRotation3d(), getModulePositions());
                field.setRobotPose(poseEstimator.getEstimatedPosition().toPose2d());
        }

        public SwerveModulePosition[] getModulePositions() {
                var modules = drivetrain.getModules();
                SwerveModulePosition[] modpos = new SwerveModulePosition[4];
                for (int x = 0; x < 4; x++) {
                        modpos[x] = modules[x].getPosition(true);
                }
                return modpos;
        }

        public void updateOdometry(){
                poseEstimator.updateWithTime(Timer.getFPGATimestamp(), drivetrain.getRotation3d(), getModulePositions());
                field.setRobotPose(poseEstimator.getEstimatedPosition().toPose2d());
        }

        public SwerveModulePosition[] getModulePositions() {
                var modules = drivetrain.getModules();
                SwerveModulePosition[] modpos = new SwerveModulePosition[4];
                for (int x = 0; x < 4; x++) {
                        modpos[x] = modules[x].getPosition(true);
                }
                return modpos;
        }

}
