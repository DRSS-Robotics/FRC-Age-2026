package frc.robot.subsystems.shooter.Turret;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class TurretSubsystem extends SubsystemBase {
    private TalonFX m_turretMotor;
    // private Encoder m_turretEncoder;
    private final DutyCycleEncoder m_turretEncoder = new DutyCycleEncoder(1); // through bore encoder code, need to get
                                                                              // channel number
    private final DutyCycleOut m_motorControl = new DutyCycleOut(0);

    private Slot0Configs turretMotorConfigs;
    // Limits to stop the turret from damging itself, in 90 to make the overall
    // rotation to 180
    private static final double MIN_ROTATION = -0.25;
    private static final double MAX_ROTATION = 0.25;
    private final PositionVoltage m_positionControl = new PositionVoltage(0);

    public TurretSubsystem(int turretMotorID) {
        m_turretMotor = new TalonFX(turretMotorID);

        turretMotorConfigs = new Slot0Configs();

        // need to tune these
        turretMotorConfigs.kV = 0;
        turretMotorConfigs.kP = 0.1;
        turretMotorConfigs.kI = 0;
        turretMotorConfigs.kD = 0;

        // SoftwareLimitSwitchConfigs limits = new SoftwareLimitSwitchConfigs()
        // .SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        // .SoftwareLimitSwitch.ForwardSoftLimitThreshold = MAX_ROTATION;
        // .SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        // .SoftwareLimitSwitch.ReverseSoftLimitThreshold = MIN_ROTATION;

        m_turretMotor.getConfigurator().apply(turretMotorConfigs);

        // sets initial mechanism/motor position to match the roboRIO duty cycle
        // absolute encoder
        m_turretMotor.setPosition(0.0);
    }

    public void setTurretPosition(double desiredRotation) {
        // Clamp the incoming rotation request to enforce the 180-degree total soft
        double clampedRotation = Math.max(MIN_ROTATION, Math.min(MAX_ROTATION, desiredRotation));

        // Command Talon FX to run closed loop position control
        m_turretMotor.setControl(m_positionControl.withPosition(clampedRotation));
    }
    
    public void setTurretPower(double power) {
        m_turretMotor.setControl(m_motorControl.withOutput(power));
    }

    public double getTurretAngle() {
        // Returns rotation measured by the encoder
        return m_turretEncoder.get();
        // System.out.println(m_turretEncoder.get()); //printing the rotation measured
        // by the encoder
    }


    public void periodic() {
        // Stream data to shuffleboard/smartdashboard for debugging and zeroing
        SmartDashboard.putNumber("Turret Absolute Angle", getTurretAngle());
    }
}
