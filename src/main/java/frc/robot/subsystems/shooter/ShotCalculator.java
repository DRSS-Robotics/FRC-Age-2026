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


public class ShotCalculator {
    double l_pitch;
    double power; 
    public ShotCalculator() {
        int l_pitch;
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
        runOuttakeMotor(DegreesPerSecond.of(speed));
    }

    public void runOuttakeMotor(AngularVelocity speed) {
        m_outtakeMotor.setControl(outtakeMotorRequest.withVelocity(speed));
    }


}