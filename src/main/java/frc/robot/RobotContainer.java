// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.AutoCommands.ExpandStorageAutoCommand;
import frc.robot.commands.AutoCommands.HangDownAutoCommand;
import frc.robot.commands.AutoCommands.HangUpAutoCommand;
//import frc.robot.commands.ShooterCommandAuto;
import frc.robot.Remote_Manager.RemoteUpdateManager;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.HangSubsystem;
import frc.robot.subsystems.SuperstructureSubsystem;
//import frc.robot.subsystems.shooter.OuttakeSubsystem;
import frc.robot.commands.ToggleIntakeCommand;
import frc.robot.commands.ToggleWallCommand;
import frc.robot.commands.AutoCommands.IntakeAutoCommand;
import frc.robot.commands.AutoCommands.TranslocatorAutoCommand;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.pathfinding.*;
import com.pathplanner.lib.util.FileVersionException;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.commands.ToggleIntakeCommand;
import frc.robot.commands.ToggleWallCommand;
import frc.robot.subsystems.SuperstructureSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  private String adaptivePathPath;
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final SendableChooser<Command> autoChooser;
  public Object m_outtakeSubsystem;
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
  final Pose3d hubPose = new Pose3d(0, 0, 0, Rotation3d.kZero);
  //final OuttakeSubsystem m_outtakeSubsystem = new OuttakeSubsystem(0,0,0,0);
  final SuperstructureSubsystem m_superstructure = 
    new SuperstructureSubsystem(
      SuperstructureConstants.kIntakeMotorId, 
      SuperstructureConstants.kStorageMotorId,
      SuperstructureConstants.kAgitatorMotorId,
      SuperstructureConstants.kTransferMotorId);
  final HangSubsystem m_hang = new HangSubsystem(0);
  // Replace with CommandPS4Controller or CommandJoystick if needed
   //private final CommandXboxController m_driverController =
    //  new CommandXboxController(OperatorConstants.kDriverControllerPort);


      //NamedCommands.registerCommand("runOuttakeMotor", new ShooterCommandAuto(m_outtakeSubsystem));
      NamedCommands.registerCommand("HangLv1", new HangUpAutoCommand(m_hang));
      NamedCommands.registerCommand("LowerHang", new HangDownAutoCommand(m_hang));
      NamedCommands.registerCommand("Intake", new IntakeAutoCommand(m_superstructure));
      NamedCommands.registerCommand("Wallout", new ExpandStorageAutoCommand(m_superstructure));
      //NamedCommands.registerCommand("Transfer", new TranslocatorAutoCommand(m_transfer));//Add when we get translocator subsystem
    
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
 
      try {
       PathPlannerPath path = PathPlannerPath.fromPathFile("start");
      } catch (FileVersionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
   PathConstraints constraints = new PathConstraints(
        3.0, 4.0,
        Units.degreesToRadians(540), Units.degreesToRadians(720));

      Command pathfindingCommand = AutoBuilder.pathfindThenFollowPath(
        path,
        constraints);

     // PathPlannerPath path = new PathPlannerPath(null, constraints, null, null);



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
    //    m_driverController.b().onTrue(new ToggleIntakeCommand(m_superstructure));
  //  m_driverController.a().onTrue(new ToggleWallCommand(m_superstructure));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
          return Commands.none();
  }
}