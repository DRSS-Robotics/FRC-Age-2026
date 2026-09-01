package frc.robot.subsystems.shooter.Turret;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.SuperstructureConstants;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

public class TurretSubsystem extends SubsystemBase {
    private TalonFX m_turretMotor;
    private Slot0Configs turretMotorConfigs;
    // private VelocityVoltage turretMotorRequest;
    // private AngularVelocity turretMotorSetSpeed = DegreesPerSecond.of(0);

    // private final TrapezoidProfile turretTrapezoidProfile = new TrapezoidProfile(
    // new TrapezoidProfile.Constraints(ShooterConstants.kTurretMaxManualSpeedDPS,
    // SuperstructureConstants.kMaxIntakeDPS3));

    // private Encoder m_turretEncoder;
    private final DutyCycleEncoder m_turretEncoder = new DutyCycleEncoder(0); // through bore absolute encoder code, need to get
                                                                              // channel number
    










    private final Encoder m_turretRelativeEncoder = new Encoder(1,2);

    // Creating new Voltage that will be supplied to turret for positional control
    // Using Voltage because it supplies constant power, and does not decline in performance when battery is lower
    private final VoltageOut turretVoltage = new VoltageOut(0);

    // TODO: TUNE VALUES
    private final PIDController turretPID;
    private final SimpleMotorFeedforward turretFeedforward;

    private boolean automatedControl = true;
    private Angle desiredPosition = Degrees.of(20);


    

    private final VelocityVoltage m_velocityControl = new VelocityVoltage(0.0);

    private static final double MIN_ROTATION = ShooterConstants.kMaxReverseRotation;
    private static final double MAX_ROTATION = ShooterConstants.kMaxForwardRotation;
    private final PositionVoltage m_positionControl = new PositionVoltage(0);
    public final Translation2d turretOffset = new Translation2d(ShooterConstants.kShooterForwardOffset,ShooterConstants.kShooterSideOffset);

    private boolean positionSeeded = false;
    private final Timer m_bootTimer = new Timer();
    private double m_dynamicEncoderOffset = 0.0;

    public TurretSubsystem(int turretMotorID) {

        m_turretMotor = new TalonFX(turretMotorID);

        m_turretMotor.getConfigurator().apply(new TalonFXConfiguration());

        TalonFXConfiguration talonConfigs = new TalonFXConfiguration();

        // Change motor to CounterClockwise so positive voltage does in positive encoder reading
        talonConfigs.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        // talonConfigs.MotorOutput.Inverted =
        // com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive;
        double mechanismLimitRotations = 0.25; // Max safe travel before encoder rollover
        double motorLimitThreshold = mechanismLimitRotations * ShooterConstants.kTurretGearRatio;

        talonConfigs.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        talonConfigs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = motorLimitThreshold;

        talonConfigs.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        talonConfigs.SoftwareLimitSwitch.ReverseSoftLimitThreshold = -motorLimitThreshold;

        talonConfigs.ClosedLoopRamps.VoltageClosedLoopRampPeriod = 0.15;

        talonConfigs.CurrentLimits.StatorCurrentLimitEnable = true;
        talonConfigs.CurrentLimits.StatorCurrentLimit = 40.0; // Amps continuous limit
        talonConfigs.CurrentLimits.SupplyCurrentLimitEnable = true;
        talonConfigs.CurrentLimits.SupplyCurrentLimit = 35.0; // Amps supply limit

        talonConfigs.MotorOutput.NeutralMode = com.ctre.phoenix6.signals.NeutralModeValue.Brake;
        talonConfigs.MotorOutput.DutyCycleNeutralDeadband = 0.01;

        talonConfigs.Feedback.FeedbackSensorSource = com.ctre.phoenix6.signals.FeedbackSensorSourceValue.RotorSensor;

        talonConfigs.Slot0.kP = 0.4;
        talonConfigs.Slot0.kI = 0.0;
        talonConfigs.Slot0.kD = 0.01;
        talonConfigs.Slot0.kV = 1.0;
        talonConfigs.Slot0.kS = 0.3;

        m_turretMotor.getConfigurator().apply(talonConfigs);

        m_bootTimer.start();

        m_turretRelativeEncoder.reset();
        turretPID = new PIDController(0.25,0,0.03);
        turretFeedforward = new SimpleMotorFeedforward(0.6, 0.25);


    }

    // Angular velocity limit can be passed or factored via request's velocity
    // feedforward
    // MAX's FUNCTION

    // public void setTurretPosition(double targetColumnRotation, double columnVelocityFeedforward) {
    //     Angle turretRotation = Degrees.of((m_turretRelativeEncoder.get() / 8192.0) * 360).plus(ShooterConstants.kShooterYawOffset);
    //     // turretRotation
    //     double clampedColumnRotation = Math.max(-0.48, Math.min(0.48, targetColumnRotation));

    //     double motorTargetRotation = clampedColumnRotation * ShooterConstants.kTurretGearRatio;
    //     double motorVelocityFeedforward = columnVelocityFeedforward * ShooterConstants.kTurretGearRatio;

    //     m_turretMotor.setControl(m_positionControl
    //             .withPosition(motorTargetRotation)
    //             .withVelocity(motorVelocityFeedforward));

    // }

    // FELIX's FUNCTION
    // Takes the desired turret position in degrees and sets the variable to that, saving it for later
    public void setTurretPosition(double desiredPosition) {
        this.desiredPosition = Degrees.of(desiredPosition);
    }
    public void setTurretPosition(Angle desiredPosition) {
        this.desiredPosition = desiredPosition;
    }

    public void setTurretVelocity(double turretVelocityDegreesPerSecond) {

        double columnRotationsPerSecond = turretVelocityDegreesPerSecond / 360.0;
        double motorRotationsPerSecond = columnRotationsPerSecond * ShooterConstants.kTurretGearRatio;

        // m_turretMotor.setControl(m_velocityControl.withVelocity(motorRotationsPerSecond));

    }

    // Returns relative encoder's turret angle
    public Angle getTurretAngle() {
        // Take the encoder ticks and divide by 2048, which is the amount of ticks in a full rotation
        // Then multiply by the inverse of the gear ratio to get the rotation stage of the turret
        // Multiply by 360 to convert to degrees
        Angle turretRotation = Degrees.of(((m_turretRelativeEncoder.get() / 2048.0) * 1.0 / ShooterConstants.kTurretGearRatio) * 360);

        SmartDashboard.putNumber("Turret Relative Ticks", m_turretRelativeEncoder.get());

        return turretRotation;
    }

    public double getAbsoluteTurretAngle() {
        double rawAbsolute = m_turretEncoder.get();
        double unmappedAngle = rawAbsolute - m_dynamicEncoderOffset;

        double boundedAngle = MathUtil.inputModulus(unmappedAngle, -0.5, 0.5);

        return MathUtil.inputModulus(unmappedAngle, -0.5, 0.5);
        
    }

    public void enableAutomatedControl() {
        automatedControl = true;
    }
    public void disableAutomatedControl() {
        automatedControl = false;
    }
    

    @Override
    public void periodic() {

        // i dont even know anymore bro, mind kaboom bro

        if (!positionSeeded && m_bootTimer.hasElapsed(1.0) && m_turretEncoder.isConnected()) {
            if(automatedControl) {
                // Take the saved desired position and calculate the voltage needed to reach the position
                double calculatedVoltage = MathUtil.clamp(turretPID.calculate(getTurretAngle().in(Degrees), desiredPosition.in(Degrees)),-1,1);
                SmartDashboard.putNumber("Turret PID Voltage", calculatedVoltage);
                // turretFeedforward.calculate(calculatedVoltage)
                SmartDashboard.putNumber("Turret feedforward", turretFeedforward.calculate(calculatedVoltage));
                m_turretMotor.setControl(turretVoltage.withOutput(turretFeedforward.calculate(calculatedVoltage)));
                
            }
            else{

                m_dynamicEncoderOffset = m_turretEncoder.get();

                double absolutePosition = getAbsoluteTurretAngle();

                double motorRotations = absolutePosition * ShooterConstants.kTurretGearRatio;
                // m_turretMotor.setPosition(motorRotations);

                positionSeeded = true;
                m_bootTimer.stop();

                System.out.println("Turret calibrated! Captured center offset at: " + m_dynamicEncoderOffset);
            }
        }

        // Stream data to shuffleboard
        SmartDashboard.putNumber("Turret Absolute Angle", getAbsoluteTurretAngle());
        SmartDashboard.putNumber("Turret Relative Angle", getTurretAngle().in(Degrees));
        SmartDashboard.putNumber("Turret Motor Position", m_turretMotor.getPosition().getValueAsDouble());
        SmartDashboard.putNumber("Turret Motor Velocity (RPS)", m_turretMotor.getVelocity().getValueAsDouble());

        if (!SmartDashboard.containsKey("Turret Dashboard Target (Rotations)")) {
            SmartDashboard.putNumber("Turret Dashboard Target (Rotations)", 0.0);
        }

        // System.out.println(mapUnclamped(getTurretAngle(), -0.48, 0.48, 0, 180));

    }

    public double getDashboardTargetRotations() {
        return SmartDashboard.getNumber("Turret Dashboard Target (Rotations)", 0.0);
    }
    public static double mapUnclamped(double val, double inMin, double inMax, double outMin, double outMax){
      return outMin + (val - inMin) * (outMax - outMin) / (inMax - inMin);
    }

}
