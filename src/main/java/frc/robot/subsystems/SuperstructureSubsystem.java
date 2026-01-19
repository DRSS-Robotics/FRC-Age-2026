package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.OperatorConstants;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

public class SuperstructureSubsystem extends SubsystemBase {

        private TalonFX m_intakeMotor;
        private Slot0Configs intakeMotorConfigs;
        private VelocityVoltage intakeMotorRequests;
        

        private TalonFX m_wallMotor;
        private boolean wallIsOpen;
        private Slot0Configs wallMotorConfigs;
        private PositionVoltage wallMotorRequests;

        /**
         * Subsystem that encompasses both the over-the-bumper intake as well as Fuel storage.
         */
        public SuperstructureSubsystem(int intakeMotorId, int wallMotorId) {
            m_intakeMotor = new TalonFX(intakeMotorId);
            m_wallMotor = new TalonFX(wallMotorId);
            
            // TODO: use actual PID values instead of placeholder
            intakeMotorConfigs = new Slot0Configs();
            intakeMotorConfigs.kV = 0;
            intakeMotorConfigs.kP = 1;
            intakeMotorConfigs.kI = 0;
            intakeMotorConfigs.kD = 0;
            m_intakeMotor.getConfigurator().apply(intakeMotorConfigs);
            intakeMotorRequests = new VelocityVoltage(0).withSlot(0);
            
            // TODO: use actual PID values instead of placeholder
            wallMotorConfigs = new Slot0Configs();
            wallMotorConfigs.kV = 0;
            wallMotorConfigs.kP = 1;
            wallMotorConfigs.kI = 0;
            wallMotorConfigs.kD = 0;
            m_wallMotor.getConfigurator().apply(wallMotorConfigs);
            wallMotorRequests = new PositionVoltage(0).withSlot(0);
        }


        public void runIntake(double speed) {
            m_wallMotor.setControl(wallMotorRequests.withVelocity(speed));
        }
        
        /**
         * Sets the PID setpoint of the wall motor. Values exceeding the bounds in 
         * {@link OperatorConstants} will automagically be ignored 
         * @param position encoder setpoint value
         */
        public void setWallMotorPosition(double position) {
            if (position < OperatorConstants.kWallClosedPosition 
                || position > OperatorConstants.kWallOpenPosition)
            {
                    System.out.println("Superstructure wall setpoint is outside bounds, ignoring request");
                    return;
            }

            // wall should be considered "open" even if set to a half-open value
            wallIsOpen = position > OperatorConstants.kWallClosedPosition;
            m_wallMotor.setControl(wallMotorRequests.withPosition(position));
        }

        /**
         * @return a boolean representing the wall's state ({@code true} = wall is open, {@code false} = wall is closed).
         * Specifically, returns whether or not the LAST SETPOINT is set to the open state or closed state. (e.g. if the
         * operator accidentally closes the wall when they didn't mean to, they should be able to interrupt the wall closing
         * without having to wait for it to fully close)
         */
        public boolean getWallState() {
            return wallIsOpen;
        } 

}