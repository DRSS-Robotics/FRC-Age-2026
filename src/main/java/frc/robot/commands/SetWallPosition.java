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

public class SetWallPosition extends Command {

  private SuperstructureSubsystem m_wall;
  private Supplier<Angle> angle;



  /**
   * When called, this Command will open and close the 
   * {@link SuperstructureSubsystem}'s wall based on the supplied {@code boolean} argument. 
   * Uses the {@link Constants.OperatorConstants OperatorConstants} encoder positions.
   *
   * @param wallSubsystem The {@link SuperstructureSubsystem Superstructure} subsystem
   */
  public SetWallPosition(SuperstructureSubsystem wallSubsystem, Supplier<Angle> angleSupplier) {
    m_wall = wallSubsystem;
    angle = angleSupplier;
  }
  
  
  
  @Override
  public void initialize() {
    m_wall.setWallMotorPosition(angle.get());
  }



  @Override
  public boolean isFinished() {
    return true;
  }

}
