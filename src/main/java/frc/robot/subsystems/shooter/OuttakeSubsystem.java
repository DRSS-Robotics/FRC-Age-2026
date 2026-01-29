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
import com.ctre.phoenix6.hardware.TalonFX;


public class OuttakeSubsystem extends SubsystemBase {
  private TalonFX m_outtakeMotor;
  private Slot0Configs outtakeMotorConfigs;
  private VelocityVoltage outtakeMotorRequest;
  private TalonFX m_pitchMotor;
  public TurretControl m_turretController;
  public OuttakeSubsystem(int outtakeMotorID, int turretMotorID, int turretCANcoderID) {
    m_outtakeMotor = new TalonFX(outtakeMotorID);

    outtakeMotorConfigs = new Slot0Configs();
    //Placeholder PID values
    outtakeMotorConfigs.kV = 0;
    outtakeMotorConfigs.kP = 1;
    outtakeMotorConfigs.kI = 0;
    outtakeMotorConfigs.kD = 0;
    m_outtakeMotor.getConfigurator().apply(outtakeMotorConfigs);
    outtakeMotorRequest = new VelocityVoltage(0).withSlot(0);

    m_turretController = new TurretControl(turretMotorID, turretCANcoderID);
  }

   
  public void runOuttakeMotor(double speed) {
    runOuttakeMotor(DegreesPerSecond.of(speed));
  }



  public void runOuttakeMotor(AngularVelocity speed) {
    m_outtakeMotor.setControl(outtakeMotorRequest.withVelocity(speed));
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