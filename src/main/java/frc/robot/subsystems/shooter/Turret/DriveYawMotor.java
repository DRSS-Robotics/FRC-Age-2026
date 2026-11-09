// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter.Turret;

import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import java.util.function.DoubleSupplier;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.Command;

public class DriveYawMotor extends Command {

  private static final double MAX_MANUAL_SPEED_DPS = 90.0;
  private static final double JOYSTICK_DEADBAND = 0.1;
  private final TurretSubsystem m_turretSubsystem;
  private final DoubleSupplier speed;

  public DriveYawMotor(TurretSubsystem turret, DoubleSupplier speedSupplier) {
    m_turretSubsystem = turret;
    speed = speedSupplier;
    addRequirements(turret);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    //debug print
    // double rawJoystickValue = speed.getAsDouble();
    
    // // Temporary debug print to your RioLog / Visual Studio terminal
    // System.out.println("DEBUG: Operator Stick Value is: " + rawJoystickValue);

   // double input = MathUtil.applyDeadband(rawJoystickValue, JOYSTICK_DEADBAND);


   double input = MathUtil.applyDeadband(speed.getAsDouble(), JOYSTICK_DEADBAND);

    // 2. Square the input for finer control at low speeds, preserving the sign
    // (+/-)
    double seasonedInput = Math.copySign(input * input, input);


    // 3. Scale the input to the DPS
    double targetVelocityDPS = seasonedInput * MAX_MANUAL_SPEED_DPS;

    // 4. Send the velocity command to the subsystem
    m_turretSubsystem.setTurretVelocity(targetVelocityDPS);
  }

  @Override
  public void end(boolean interrupted) {
    m_turretSubsystem.setTurretVelocity(0.0);

  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
