// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
//import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.FieldConstants;

import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Degrees;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;


public class TurretControl extends SubsystemBase {
   
    private PositionVoltage turretPositionRequest;
    private VelocityVoltage turretVelocityRequest;
    private Slot0Configs turretMotorConfigs;
    private TalonFX m_turretMotor;

  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
    public TurretControl(int turretMotorID) {
        m_turretMotor = new TalonFX(turretMotorID);
        turretMotorConfigs = new Slot0Configs();
        //Placeholder PID values
        turretMotorConfigs.kV = 0;
        turretMotorConfigs.kP = 1;
        turretMotorConfigs.kI = 0;
        turretMotorConfigs.kD = 0;
        m_turretMotor.getConfigurator().apply(turretMotorConfigs);
        turretPositionRequest = new PositionVoltage(0).withSlot(0);
        turretVelocityRequest = new VelocityVoltage(0).withSlot(0);
    }

    public static double calcShotPitch(double horizDist, double power, double h_launcher){
        double a = (FieldConstants.kGravIN * Math.pow(horizDist, 2)) / (2 * Math.pow(power, 2));
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

    public static double calcShotPower(double horizDist, double pitch, double h_launcher){
        
        double numerator = FieldConstants.kGravIN * Math.pow(horizDist, 2);
        double denominator = 2 * (-72 + horizDist * Math.tan(pitch) + h_launcher) * Math.pow(Math.cos(pitch), 2);
        double power = Math.sqrt(numerator / denominator);

        return power;
    }
    // in degrees
    public void setTurretPosition(double speed) {
        setTurretPosition(Degrees.of(speed));
    }

    public void setTurretPosition(Angle speed) {
        m_turretMotor.setControl(turretPositionRequest.withPosition(speed));
    }

    //in degrees per second
    public void driveTurret(double speed) {
        driveTurretMotor(DegreesPerSecond.of(speed));
    }

    public void driveTurretMotor(AngularVelocity speed) {
        m_turretMotor.setControl(turretVelocityRequest.withVelocity(speed));
    }

}