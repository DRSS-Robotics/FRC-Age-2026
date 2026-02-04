// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

import edu.wpi.first.units.measure.AngularVelocity;
//import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.DegreesPerSecond;
//import static edu.wpi.first.units.Units.Degrees;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;


public class TurretControl extends SubsystemBase {
  private TalonFX m_turretMotor;
  private Slot0Configs turretMotorConfigs;
  private VelocityVoltage turretMotorRequest;
  private CANcoder m_turretCANcoder;

  public TurretControl(int turretMotorID) {
    m_turretMotor = new TalonFX(turretMotorID);

    turretMotorConfigs = new Slot0Configs();
    //Placeholder PID values
    turretMotorConfigs.kV = 0;
    turretMotorConfigs.kP = 1;
    turretMotorConfigs.kI = 0;
    turretMotorConfigs.kD = 0;
    m_turretMotor.getConfigurator().apply(turretMotorConfigs);
    turretMotorRequest = new VelocityVoltage(0).withSlot(0);
  }

   
  /**
   * Example command factory method.
   *
   * @return a command
   */

  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public void runOuttakeMotor(double speed) {
    
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}