package frc.robot.subsystems.shooter.Turret;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
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
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.SuperstructureConstants;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Rotations;

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
    // private final PIDController turretPID;
    // private final SimpleMotorFeedforward turretFeedforward;

    private boolean automatedControl = false;
    private Angle desiredPosition = Degrees.of(15);


    

    private final VelocityVoltage m_velocityControl = new VelocityVoltage(0.0);

    private static final double MIN_ROTATION = ShooterConstants.kMaxReverseRotation;
    private static final double MAX_ROTATION = ShooterConstants.kMaxForwardRotation;
    private final PositionVoltage m_positionControl = new PositionVoltage(0);
    public final Translation2d turretOffset = new Translation2d(ShooterConstants.kShooterForwardOffset,ShooterConstants.kShooterSideOffset);

    private boolean positionSeeded = false;
    private double m_dynamicEncoderOffset = 0.0;

    private PositionVoltage turretMotorRequest = new PositionVoltage(0).withSlot(0);;
    private Angle turretSetpoint = Degrees.of(0);
    private boolean reachedSetpoint = false;
    private final MotionMagicVoltage positionRequest = new MotionMagicVoltage(0);

    private static final DCMotor MOTOR = DCMotor.getKrakenX60(1);

    private final DCMotorSim turretSim =
            new DCMotorSim(
                LinearSystemId.createDCMotorSystem(
                    MOTOR,
                    0.02,
                    ShooterConstants.kTurretGearRatio
                ),
                MOTOR
            );

    public TurretSubsystem(int turretMotorID) {

        m_turretMotor = new TalonFX(turretMotorID);

        TalonFXConfiguration talonConfigs = new TalonFXConfiguration();

        // Change motor to CounterClockwise so positive voltage does in positive encoder reading
        talonConfigs.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        talonConfigs.Feedback.SensorToMechanismRatio = ShooterConstants.kTurretGearRatio;

        // talonConfigs.MotorOutput.Inverted =
        // com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive;
        // Setting mechanism software limit to 1/4 of a rotation, or 90 degrees in both directions
        double mechanismLimitRotations = 0.25;
        double motorLimitThreshold = mechanismLimitRotations;

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
        // Voltage control less than this gets zeroed
        talonConfigs.MotorOutput.DutyCycleNeutralDeadband = 0.0;

        talonConfigs.Feedback.FeedbackSensorSource = com.ctre.phoenix6.signals.FeedbackSensorSourceValue.RotorSensor;

        talonConfigs.MotionMagic.MotionMagicCruiseVelocity = 0.75;
        talonConfigs.MotionMagic.MotionMagicAcceleration = 2.0;

        // Measured value to 0 the turret
        talonConfigs.Feedback.FeedbackRotorOffset = ShooterConstants.kTurretEncoderOffset;

        talonConfigs.Slot0.kP = 36.0;
        talonConfigs.Slot0.kI = 0.0;
        talonConfigs.Slot0.kD = 2.25;
        talonConfigs.Slot0.kS = 0.7;
        talonConfigs.Slot0.kV = 0.0;

        m_turretMotor.getConfigurator().apply(talonConfigs);
        if (RobotBase.isSimulation()) {
            m_turretMotor.setPosition(0);
        }

        setTurretPosition(desiredPosition);

    }

    // FELIX's FUNCTION
    // Takes the desired turret position in degrees and sets the variable to that, saving it for later
    public void setTurretPosition(double position) {
        setTurretPosition(Degrees.of(position));
    }
    public void setTurretPosition(Angle position) {
        double clamped = MathUtil.clamp(position.in(Degrees), -90, 90);
        desiredPosition = Degrees.of(clamped);
        turretSetpoint = Degrees.of(clamped);
    }

    public void setTurretVelocity(double turretVelocityDegreesPerSecond) {

        double columnRotationsPerSecond = turretVelocityDegreesPerSecond / 360.0;
        double motorRotationsPerSecond = columnRotationsPerSecond * ShooterConstants.kTurretGearRatio;

        // m_turretMotor.setControl(m_velocityControl.withVelocity(motorRotationsPerSecond));

    }

    // Returns relative encoder's turret angle
    public Angle getTurretAngle() {
        return m_turretMotor.getPosition().getValue();
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
        if(DriverStation.isEnabled()){
            if(automatedControl){
                m_turretMotor.setControl(positionRequest.withPosition(turretSetpoint.in(Rotations))); 
            }
            else{
                m_turretMotor.setControl(positionRequest.withPosition(0));
            }
            // if(automatedControl) {
            //     if(!reachedSetpoint){
            //         if(Math.abs(desiredPosition.in(Degrees) - getTurretAngle().in(Degrees)) < 0.5){
            //             reachedSetpoint = true;
            //             m_turretMotor.setControl(turretVoltage.withOutput(0));
            //         }
            //         else{
            //             // Take the saved desired position and calculate the voltage needed to reach the position
            //             double calculatedVoltage = MathUtil.clamp(turretPID.calculate(getTurretAngle().in(Degrees), desiredPosition.in(Degrees)),-1,1);
                        
            //             m_turretMotor.setControl(turretVoltage.withOutput(turretFeedforward.calculate(calculatedVoltage)));
            //             SmartDashboard.putNumber("Turret PID Voltage", calculatedVoltage);
            //             SmartDashboard.putNumber("Turret feedforward", turretFeedforward.calculate(calculatedVoltage));
            //         }
            //     }
            //     else{
            //         m_turretMotor.setControl(turretVoltage.withOutput(0));
            //         if(Math.abs(getTurretAngle().in(Degrees) - desiredPosition.in(Degrees)) > 2){
            //             reachedSetpoint = false;
            //         }
            //     }


            // }
            // else{

            //     m_dynamicEncoderOffset = m_turretEncoder.get();

            //     double absolutePosition = getAbsoluteTurretAngle();

            //     double motorRotations = absolutePosition * ShooterConstants.kTurretGearRatio;
            //     // m_turretMotor.setPosition(motorRotations);

                
                
            //     // positionSeeded = true;
                
            //     System.out.println("Turret calibrated! Captured center offset at: " + m_dynamicEncoderOffset);
            // }
            
        }


        // Stream data to shuffleboard
        SmartDashboard.putNumber("Turret Relative Angle", getTurretAngle().in(Degrees));
        SmartDashboard.putNumber("Turret Motor Position", m_turretMotor.getPosition().getValueAsDouble());
        SmartDashboard.putNumber("Turret Motor Velocity (RPS)", m_turretMotor.getVelocity().getValueAsDouble());

        if (!SmartDashboard.containsKey("Turret Dashboard Target (Rotations)")) {
            SmartDashboard.putNumber("Turret Dashboard Target (Rotations)", 0.0);
        }

        SmartDashboard.putNumber(
            "DEBUG/Requested Degrees",
            turretSetpoint.in(Degrees)
        );

        SmartDashboard.putNumber(
            "DEBUG/Requested Rotations",
            turretSetpoint.in(Rotations)
        );

        SmartDashboard.putNumber(
            "DEBUG/Phoenix Position Degrees",
            m_turretMotor.getPosition().getValue().in(Degrees)
        );

        SmartDashboard.putNumber(
            "DEBUG/Phoenix Position Rotations",
            m_turretMotor.getPosition().getValue().in(Rotations)
        );

        SmartDashboard.putNumber(
            "DEBUG/Phoenix Closed Loop Reference",
            m_turretMotor.getClosedLoopReference().getValueAsDouble()
        );

        SmartDashboard.putNumber(
            "DEBUG/Physics Sim Degrees",
            turretSim.getAngularPositionRotations() * 360.0
        );

        SmartDashboard.putNumber(
            "DEBUG/Raw Rotor Rotations",
            m_turretMotor.getRotorPosition().getValueAsDouble()
        );

    }

    @Override
    public void simulationPeriodic() {
        var talonSim = m_turretMotor.getSimState();

        // Simulate normal robot battery voltage
        talonSim.setSupplyVoltage(
            RobotController.getBatteryVoltage()
        );

        // Phoenix tells us what voltage its PID controller wants
        double motorVoltage = talonSim.getMotorVoltage();

        // Feed that voltage into the physical turret simulation
        turretSim.setInputVoltage(motorVoltage);

        // Simulate the next 20 ms
        turretSim.update(0.020);

        // WPILib simulation is giving us mechanism-side motion
        double turretRotations =
            turretSim.getAngularPositionRotations();

        double turretRps =
            turretSim.getAngularVelocityRadPerSec()
            / (2.0 * Math.PI);

        // Phoenix simulation needs raw MOTOR rotor motion
        double motorRotations =
            turretRotations * ShooterConstants.kTurretGearRatio;

        double motorRps =
            turretRps * ShooterConstants.kTurretGearRatio;

        talonSim.setRawRotorPosition(motorRotations);
        talonSim.setRotorVelocity(motorRps);
    }

    

}
