// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

import edu.wpi.first.units.measure.AngularVelocity;
//import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

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

    public double calcShotPitch(double horizDist, double power, double h_launcher){
        double a = (Constants.kGravIN * Math.pow(horizDist, 2)) / (2 * Math.pow(power, 2));
        double b = -horizDist;
        double c = -h_launcher + a + 72;
        double discrim = Math.pow(b, 2) - (4 * a * c);

        // simplifying it down, if the discrimininant is below zero for this shot calc, it is NOT POSSIBLE
        double s_angle;
        if(discrim >= 0){
            s_angle = Math.atan((-b + Math.sqrt(discrim)) / (2 * a));
            return s_angle;
        }
        else{
            // return -1 for not possible
            return -1;
        }
    }

    public double calcShotPower(double horizDist, double pitch, double h_launcher){
        
        double numerator = Constants.kGravIN * Math.pow(horizDist, 2);
        double denominator = 2 * (-72 + horizDist * Math.tan(pitch) + h_launcher) * Math.pow(Math.cos(pitch), 2);
        double power = Math.sqrt(numerator / denominator);

        return power;
    }


}