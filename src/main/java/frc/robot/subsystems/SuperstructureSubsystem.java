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
import frc.robot.remote_manager.IRemoteUpdate;

public class SuperstructureSubsystem extends SubsystemBase implements IRemoteUpdate {
        private SparkMax m_intakeMotor;
        private SparkMax m_wallMotor;

        private Integer state;

        private SparkBaseConfig wallConfig;
        private ClosedLoopConfig wallPid;

        public SuperstructureSubsystem() {
            wallPid = new ClosedLoopConfig()
                .p(1)
                .i(0)
                .d(0);
            wallConfig = new SparkMaxConfig().apply(wallPid);
            m_wallMotor.configure(wallConfig, 
                ResetMode.kResetSafeParameters, 
                PersistMode.kNoPersistParameters);
        }

        @Override
        public void periodic() {
                         
        }

        public void runIntake(double speed) {
            m_intakeMotor.set(speed);
        }
        
        public void setWallMotorPosition(double position) {
            // if ()
            m_wallMotor.getClosedLoopController().setReference(position, ControlType.kPosition);
        }

        @Override
        public void updateNumericState(Integer state) {
            this.state = state;
        }

        @Override
        public Integer getNumericState() {
            return state;
        }
}