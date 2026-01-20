// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.SuperstructureSubsystem.StorageWallState;
import edu.wpi.first.wpilibj2.command.Command;

public class ToggleWallCommand extends Command {

  private SuperstructureSubsystem m_wall;



  /**
   * When called, this Command will open and close the 
   * {@link SuperstructureSubsystem}'s wall based on the supplied {@code boolean} argument. 
   * Uses the {@link Constants.OperatorConstants OperatorConstants} encoder positions.
   *
   * @param wallSubsystem The {@link SuperstructureSubsystem Superstructure} subsystem
   */
  public ToggleWallCommand(SuperstructureSubsystem wallSubsystem) {
    m_wall = wallSubsystem;
    addRequirements(wallSubsystem);
  }
  
  
  
  @Override
  public void initialize() {
    // if the storage wall is either closing or on its way to closing,
    // open it (defaults to closing otherwise)
    StorageWallState state = m_wall.getStorageState();
    boolean willOpen = (state == StorageWallState.kIsClosed || 
                state == StorageWallState.kIsClosing);

    m_wall.setWallMotorPosition(willOpen ? 
      SuperstructureConstants.kStorageOpenRotations : 
      SuperstructureConstants.kStorageClosedRotations);
  }



  @Override
  public boolean isFinished() {
    return true;
  }

}
