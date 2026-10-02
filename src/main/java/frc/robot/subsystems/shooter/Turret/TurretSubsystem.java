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
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.SuperstructureConstants;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Rotations;

public class TurretSubsystem extends SubsystemBase {
    private TalonFX m_turretMotor;
    private Slot0Configs turretMotorConfigs;

    private boolean automatedControl = true;
    private Angle desiredPosition = Degrees.of(-45);

    private final VelocityVoltage m_velocityControl = new VelocityVoltage(0.0);

    private static final double MIN_ROTATION = ShooterConstants.kMaxReverseRotation;
    private static final double MAX_ROTATION = ShooterConstants.kMaxForwardRotation;
    public final Translation2d turretOffset = new Translation2d(ShooterConstants.kShooterForwardOffset,ShooterConstants.kShooterSideOffset);

    private Angle turretSetpoint = Degrees.of(0);
    private final MotionMagicVoltage positionRequest = new MotionMagicVoltage(0);

    public Pose2d turretPose = new Pose2d();

    public Pose2d virtualTurretTarget = Constants.kHubPoseCenter;

    StructPublisher<Pose2d> turretPosePublisher = NetworkTableInstance.getDefault()
    .getTable("SmartDashboard")
    .getSubTable("Turret")
    .getStructTopic("Turret Pose", Pose2d.struct)
    .publish();


    // SIM code
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
        talonConfigs.MotorOutput.DutyCycleNeutralDeadband = 0.03;

        talonConfigs.Feedback.FeedbackSensorSource = com.ctre.phoenix6.signals.FeedbackSensorSourceValue.RotorSensor;

        talonConfigs.MotionMagic.MotionMagicCruiseVelocity = 2.0;
        talonConfigs.MotionMagic.MotionMagicAcceleration = 3.0;

        // Measured value to 0 the turret
        // talonConfigs.Feedback.FeedbackRotorOffset = ShooterConstants.kTurretEncoderOffset;

        talonConfigs.Slot0.kP = 50.0;
        talonConfigs.Slot0.kI = 0.0;
        talonConfigs.Slot0.kD = 1.5;
        talonConfigs.Slot0.kS = 1.0;
        talonConfigs.Slot0.kV = 0.0;

        m_turretMotor.getConfigurator().apply(talonConfigs);
        if (RobotBase.isSimulation()) {
            m_turretMotor.setPosition(0);
        }
        m_turretMotor.setPosition(0);

        setTurretPosition(desiredPosition);


    }

    // FELIX's FUNCTION
    // Takes the desired turret position in degrees and sets the variable to that, saving it for later
    public void setTurretPosition(double position) {
        setTurretPosition(Degrees.of(position));
    }
    public void setTurretPosition(Angle position) {
        double clamped = MathUtil.clamp(position.in(Degrees), -90, 90);
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
    
    public void updateTurretPose(Pose2d robotPose){
        // Get rotation of turret relative to robot, must be rotated 180deg to be accurate
        Angle relativeTurretRotation = getTurretAngle().plus(ShooterConstants.kShooterYawOffset);
        
        // This is NOT actually the absolute rotation of the turret, it is the rotation from the rotation's pole to 
        // face the center of the turret, so calculations can be held here
        Rotation2d absoluteTurretCenterRotation = robotPose.getRotation().plus(new Rotation2d(ShooterConstants.kShooterYawOffset));
        
        // add the turret relative pose to the robot pose, where the turret pose is rotated by robot rotation with turret yaw offset
        // the rotation of turret pose is the relative plus robot rotation
        turretPose = new Pose2d(robotPose.getTranslation().plus((turretOffset)
                                    .rotateBy(absoluteTurretCenterRotation)), 
                                    new Rotation2d(relativeTurretRotation.plus(
                                    Degrees.of(robotPose.getRotation().getDegrees()))));

        turretPosePublisher.set(turretPose);
    }

    private Angle ferryAngle(){
        // It's easier to get the turretPose than the robotPose in this scope, but this is
        // essentially just using the robot's rotation then negating it
        Rotation2d botRotation = turretPose.getRotation().minus(
                                new Rotation2d(getTurretAngle())).minus(
                                new Rotation2d(ShooterConstants.kShooterYawOffset));
        return Degrees.of(botRotation.unaryMinus().getDegrees());

    }

    // For Shoot on the Move, will likely need to be iterated a few times because when changing the 
    // virtual target, the distance also changes. Therefore, the power changes and the turret angle changes.
    // public void calculateSOTM(ChassisSpeeds robotSpeed){

    //     Translation2d targetPosition = Constants.kHubPoseCenter.getTranslation();

    //     // Get distance from the turret to the target
    //     Distance currDistance = Meters.of(targetPosition.getDistance(turretPose.getTranslation()));

    //     // Example for flight time
    //     double flightTime = flightTimeMap.get(currDistance);

    //     // Translation2d compensatedTarget = target.minus(
    //     //     new Translation2d(
    //     //         Meters.of(robotSpeed.vxMetersPerSecond * flightTime),
    //     //         Meters.of(robotSpeed.vyMetersPerSecond * flightTime)
    //     //     )
    //     // );

    //     // Take the robot's velocity vector and multiply by flight time
    //     // Then the rest is for turret rotation compensation
    //     // Take the angular velocity of the robot and multiply by the turret's offsets from the center
    //     Translation2d compensatedTarget = target.minus(
    //         new Translation2d(
    //             Meters.of(robotSpeed.vxMetersPerSecond * flightTime + robotSpeed.omegaRadiansPerSecond * turretOffset.getY()),
    //             Meters.of(robotSpeed.vyMetersPerSecond * flightTime + robotSpeed.omegaRadiansPerSecond * turretOffset.getX())
    //         )
    //     );

    //     // Next, do a second iteration
    //     // We should do this because we originally got the distance between the target and the turret
    //     // After finding the position of our virtual target, we need to recalculate the distance between them,
    //     // Run time of flight estimates again with the new distance, and find the target again

    //     currDistance = Meters.of(compensatedTarget.getDistance(turretPose.getTranslation()));

    //     flightTime = flightTimeMap.get(currDistance);

    //     compensatedTarget = compensatedTarget.minus(
    //         new Translation2d(
    //             Meters.of(robotSpeed.vxMetersPerSecond * flightTime + robotSpeed.omegaRadiansPerSecond * turretOffset.getY()),
    //             Meters.of(robotSpeed.vyMetersPerSecond * flightTime + robotSpeed.omegaRadiansPerSecond * turretOffset.getX())
    //         )
    //     );
    // }

    @Override
    public void periodic() {

        if(DriverStation.isEnabled()){
            if(automatedControl){
                // If the robot is past the hub by 0.3 meters, then make the turret face our alliance wall to ferry
                if(Constants.kHubPoseCenter.getTranslation().minus(turretPose.getTranslation()).getX() < -0.3){
                    m_turretMotor.setControl(positionRequest.withPosition(ferryAngle().in(Rotations)));
                }
                else{
                    m_turretMotor.setControl(positionRequest.withPosition(turretSetpoint.in(Rotations)));
                }
            }
            else{
                m_turretMotor.setControl(positionRequest.withPosition(0));
            }
        }


        // Stream data to SmartDashboard
        SmartDashboard.putNumber("Turret/Turret Relative Angle", getTurretAngle().in(Degrees));
        SmartDashboard.putNumber("Turret/Turret Commanded Rotation", turretSetpoint.in(Degrees));

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
