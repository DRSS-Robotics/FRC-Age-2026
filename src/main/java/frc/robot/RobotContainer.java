// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.commands.AutoCommands.ExpandStorageAutoCommand;
import frc.robot.commands.AutoCommands.HangDownAutoCommand;
import frc.robot.commands.AutoCommands.HangUpAutoCommand;
import frc.robot.subsystems.HangSubsystem;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.commands.AutoCommands.IntakeAutoCommand;
import frc.robot.commands.AutoCommands.pathfindingCommand;
import frc.robot.generated.TunerConstants;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;

import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  private pathfindingCommand pathfinding;
  private String adaptivePathPath;
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final SendableChooser<Command> autoChooser;
  public Object m_outtakeSubsystem;

    private double MaxSpeed = 0.4 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1)
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController joystick = new CommandXboxController(0);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
  final Pose3d hubPose = new Pose3d(0, 0, 0, Rotation3d.kZero);
  //final OuttakeSubsystem m_outtakeSubsystem = new OuttakeSubsystem(0,0,0,0);
  final SuperstructureSubsystem m_superstructure = 
    new SuperstructureSubsystem(
      SuperstructureConstants.kIntakeMotorId, 
      SuperstructureConstants.kStorageMotorId,
      SuperstructureConstants.kAgitatorMotorId,
      SuperstructureConstants.kTransferMotorId, NetworkTableInstance.getDefault().getTable("Superstructure"));
  final HangSubsystem m_hang = new HangSubsystem(0);
  // Replace with CommandPS4Controller or CommandJoystick if needed
   //private final CommandXboxController m_driverController =
    //  new CommandXboxController(OperatorConstants.kDriverControllerPort);


      NamedCommands.registerCommand("runOuttakeMotor", new ShooterCommandAuto(m_outtakeSubsystem));
      NamedCommands.registerCommand("HangLv1", new HangUpAutoCommand(m_hang));
      NamedCommands.registerCommand("LowerHang", new HangDownAutoCommand(m_hang));
      NamedCommands.registerCommand("Intake", new IntakeAutoCommand(m_superstructure));
      NamedCommands.registerCommand("Wallout", new ExpandStorageAutoCommand(m_superstructure));
      // NamedCommands.registerCommand("Transfer", new TranslocatorAutoCommand(m_transfer));//Add when we get translocator subsystem
    
      autoChooser = AutoBuilder.buildAutoChooser("Default");
      adaptivePathPath = String.valueOf(autoChooser);
      //Fix later
      // this.autoChooser = new LoggedDashboardChooser<>("Auto Routine", AutoBuilder.buildAutoChooser()); // Loads all FRC PathPlanner auto routines
      // this.autoChooser.addOption("Left", this.autoLeftCommand());
      // this.autoChooser.addOption("Left with Push", this.autoLeftWithPushCommand());
      // this.autoChooser.addOption("Centre H", this.autoCentreHCommand());
      // this.autoChooser.addOption("Drive Forward", this.autoDriveForwardCommand());
      // this.autoChooser.addOption("Centre G", this.autoCentreGCommand());
      // this.autoChooser.addOption("Right", this.autoRightCommand());
      SmartDashboard.putData("Auto Mode", autoChooser);
      
    // Configure the trigger bindings
    configureBindings();
  }
  

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
    // m_driverController.b().onTrue(new ToggleIntakeCommand(m_superstructure));
  //m_driverController.a().onTrue(new ToggleWallCommand());
  //m_driverController.x().onTrue(m_drivetrain.pathfindingCommand());
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
          return autoChooser.getSelected();
  }
}