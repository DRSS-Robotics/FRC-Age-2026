// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.subsystems.SuperstructureSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

public class ToggleWallCommand extends Command {

  private SuperstructureSubsystem m_wall;
  private boolean willOpen;
  
  /**
   * When called, this Command will open and close the 
   * {@link SuperstructureSubsystem}'s wall based on the supplied {@code boolean} argument. 
   * Uses the {@link Constants.OperatorConstants OperatorConstants} encoder positions.
   *
   * @param wallSubsystem The {@link SuperstructureSubsystem Superstructure} subsystem
   * @param shouldOpen Whether or not the wall should be open ({@code true} = wall is open,
   * {@code false} = wall is closed)
   */

  public ToggleWallCommand(SuperstructureSubsystem wallSubsystem, boolean shouldOpen) {
    m_wall = wallSubsystem;
    willOpen = shouldOpen;
    
    addRequirements(wallSubsystem);
  }


  @Override
  public void initialize() {
    m_wall.setWallMotorPosition(willOpen ? 
      Constants.OperatorConstants.kWallOpenPosition : 
      Constants.OperatorConstants.kWallClosedPosition);
  }

  @Override
  public boolean isFinished() {
    return true;
  }

}
