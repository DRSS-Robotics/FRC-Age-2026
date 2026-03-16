package frc.robot;

import frc.robot.Constants.*;
import frc.robot.commands.DriveYawMotor;
import frc.robot.commands.RotateYawMotor;
import frc.robot.commands.SetWallPosition;
import frc.robot.commands.SoupKickback;
import frc.robot.commands.ToggleIntakeCommand;
import frc.robot.commands.ToggleLaunchMotor;
import frc.robot.commands.ToggleWallCommand;
import frc.robot.generated.TunerConstants;
import frc.robot.commands.DriveIntakeCommand;
import frc.robot.commands.DriveLaunchMotor;
import frc.robot.commands.DriveTransferCommand;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;
import frc.robot.subsystems.CommandSwerveDrivetrain;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.VisionConstants;
import frc.robot.commands.*;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.ctre.phoenix6.hardware.core.CorePigeon2;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
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
    
    private double MaxSpeed = 0.5 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top
    // speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second
    // max
    // angular velocity

    Supplier<Angle> turretAngleSupplier = () -> {return Degrees.of(0);};
    SwerveDrivePoseEstimator poseEstimator;
    CorePigeon2 pigeon;
    public final Vision m_vision = new Vision(turretAngleSupplier, poseEstimator, pigeon);
    
    private double speedMultiplier = 1;

    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1)
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
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

    public RobotContainer() {
        configureBindings();
        // 77% max power from corner works well
    }

    private void configureBindings() {

        m_operatorController.rightTrigger(0.1).whileTrue(
                new DriveLaunchMotor(m_shooter, () -> DegreesPerSecond.of(
                        ShooterConstants.kShooterMaxManualSpeedDPS * m_operatorController.getRightTriggerAxis()))
                .alongWith(Commands.run(() -> System.out.println(m_operatorController.getRightTriggerAxis()))));

        m_operatorController.y().whileTrue(new ToggleLaunchMotor(m_shooter,
                () -> DegreesPerSecond.of(ShooterConstants.kShooterMaxManualSpeedDPS * 0.7), () -> false));
        m_operatorController.b().onTrue(new ToggleIntakeCommand(m_superstructure));
        m_operatorController.a().onTrue(new ToggleWallCommand(m_superstructure));
        m_operatorController.leftTrigger(0.15)
                .whileTrue(new DriveTransferCommand(m_superstructure, m_operatorController::getLeftTriggerAxis));

        m_operatorController.leftBumper()
                .whileTrue(new SoupKickback(m_superstructure).withTimeout(0.5));

        drivetrain.setDefaultCommand(
                drivetrain.applyRequest(() -> drive
                        .withVelocityX(-m_driverController.getLeftY() * MaxSpeed * speedMultiplier)
                        .withVelocityY(-m_driverController.getLeftX() * MaxSpeed * speedMultiplier)
                        .withRotationalRate(-m_driverController.getRightX() * MaxAngularRate * speedMultiplier)));

        final var idle = new SwerveRequest.Idle();
        RobotModeTriggers.disabled().whileTrue(
                drivetrain.applyRequest(() -> idle).ignoringDisable(true));

        m_driverController.a().whileTrue(drivetrain.applyRequest(() -> brake));
        m_driverController.b().whileTrue(drivetrain.applyRequest(() -> point
                .withModuleDirection(new Rotation2d(-m_driverController.getLeftY(), -m_driverController.getLeftX()))));

        // Note that each routine should be run exactly once in a single log.
        m_driverController.back().and(m_driverController.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        m_driverController.back().and(m_driverController.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        m_driverController.start().and(m_driverController.y())
                .whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        m_driverController.start().and(m_driverController.x())
        .whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));
        
        m_driverController.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));
        
        m_driverController.rightTrigger().whileTrue(Commands.run(() -> {
                speedMultiplier = 1 - m_driverController.getRightTriggerAxis();
                drive
                .withDeadband(MaxSpeed * 0.1 * speedMultiplier)
                .withRotationalDeadband(MaxAngularRate * 0.1 * speedMultiplier);
        })).whileFalse(Commands.run(() -> {
                speedMultiplier = 1;
                drive.withDeadband(MaxSpeed * 0.1)
                .withRotationalDeadband(MaxAngularRate * 0.1);
        }));
        

        m_driverController.y().toggleOnTrue(new SwapCamera(m_vision));
        m_driverController.a().onTrue(Commands.runOnce(() -> {System.out.println("CAN DETECT: " + CameraServer.getServer(VisionConstants.kOutputStreamName));}));
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
        return Commands.none();
    }

    // converts a m_driverController position into an angle that can be used by
    // turret set
    // position commands (straight forward on the joytick is 180 deg)
    private static double convertPositionToTurretAngle(double x, double y) {
        return (180 / Math.PI) * Math.atan(
                y / x) + (90.0 * (signInclusive(x) + 2));
    }

}
