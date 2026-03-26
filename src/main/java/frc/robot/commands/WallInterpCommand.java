// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.SuperstructureSubsystem.StorageWallState;

import java.util.function.Supplier;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;

public class WallInterpCommand extends Command {

  private SuperstructureSubsystem m_wall;
  private Angle startPos;
  private Supplier<Double> alpha;
  private boolean returnToStart;



  /**
   * When called, this Command will open and close the 
   * {@link SuperstructureSubsystem}'s wall based on the supplied {@code boolean} argument. 
   * Uses the {@link Constants.OperatorConstants OperatorConstants} encoder positions.
   *
   * @param wallSubsystem The {@link SuperstructureSubsystem Superstructure} subsystem
   */
  public WallInterpCommand(SuperstructureSubsystem wallSubsystem, Supplier<Double> alpha, boolean returnToStart) {
    m_wall = wallSubsystem;
    this.alpha = alpha;
    this.returnToStart = returnToStart;
  }
  
  
  
  @Override
  public void initialize() {
    startPos = m_wall.getStorageSetpoint();
    m_wall.setWallMotorPosition(
      (SuperstructureConstants.kStorageOpenRotations * alpha.get()) + 
      SuperstructureConstants.kStorageClosedRotations * (1 - alpha.get()));
  }



  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    if (returnToStart) m_wall.setWallMotorPosition(startPos);
  }

}
