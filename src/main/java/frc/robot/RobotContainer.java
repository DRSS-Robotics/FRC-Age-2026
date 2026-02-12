// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.commands.ToggleIntakeCommand;
import frc.robot.commands.ToggleWallCommand;
import frc.robot.subsystems.SuperstructureSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;



public class RobotContainer {
  private final SuperstructureSubsystem m_superstructure = 
    new SuperstructureSubsystem(
      SuperstructureConstants.kIntakeMotorId, 
      SuperstructureConstants.kStorageMotorId,
      SuperstructureConstants.kTransferMotorId);

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);


  public RobotContainer() {
    configureBindings();

    TestRunner.addTest(new SelftTestForTestInterface());
  }

  private void configureBindings() {

    // new Trigger(m_exampleSubsystem::exampleCondition)
    //     .onTrue(new ExampleCommand(m_exampleSubsystem));

    m_driverController.b().onTrue(new ToggleIntakeCommand(m_superstructure));
    m_driverController.a().onTrue(new ToggleWallCommand(m_superstructure));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
      return Commands.none();
  }
}
