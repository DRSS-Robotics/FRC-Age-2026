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
import edu.wpi.first.wpilibj.DriverStation.Alliance;
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
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Rotations;

import java.util.function.Supplier;

public class TurretSubsystem extends SubsystemBase {
    private TalonFX m_turretMotor;
    private Slot0Configs turretMotorConfigs;

    public boolean automatedControl = true;
    private Angle desiredPosition = Degrees.of(0);

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

    Supplier<Pose2d> robotPoseSupplier;

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

    public TurretSubsystem(int turretMotorID, Supplier<Pose2d> robotPoseSupplier) {

        m_turretMotor = new TalonFX(turretMotorID);

        TalonFXConfiguration talonConfigs = new TalonFXConfiguration();

        // Change motor to CounterClockwise so positive voltage does in positive encoder reading
        talonConfigs.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        talonConfigs.Feedback.SensorToMechanismRatio = ShooterConstants.kTurretGearRatio;

        this.robotPoseSupplier = robotPoseSupplier;

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

    public void RotateToHub(){
        // get field pose of turret with the rotation of the robot 
    // TODO: actually add in realistic pose, instead of static positions
    // Pose2d robotPose = poseEstimator.getEstimatedPosition();
    Pose2d robotPose = robotPoseSupplier.get();
    
    // add the turret relative pose to the robot pose, where the turret pose is rotated by robot rotation with turret yaw offset
    // the rotation of turret pose is the relative plus robot rotation

    // fill this out with a reference to rad per second, whether from pigeon or pose
    double omegaRadiansPerSecond = 0;

    Translation2d turretLinearMomentum = new Translation2d(
        ShooterConstants.kShooterSideOffset.times(-omegaRadiansPerSecond),
        ShooterConstants.kShooterForwardOffset.times(omegaRadiansPerSecond)
    );

    Distance kHubXPos = (DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == Alliance.Red) ? 
                                    Constants.kFieldLengthMeters.minus(Inches.of(182.11)) : 
                                    Inches.of(182.11);
    Pose2d kHubPoseCenter = new Pose2d(kHubXPos.in(Meters),Inches.of(158.84).in(Meters),new Rotation2d(0));
    Translation2d distanceFromHub = kHubPoseCenter.getTranslation().minus(turretPose.getTranslation());
    // Angle hubAzimuth = Degrees.of(MathUtil.inputModulus(
    //                               Radians.of(Math.PI/2-Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX())))
    //                               .plus(hubOffsetAngle(turretPose)).in(Degrees), -180, 180));

    Angle hubAzimuth = hubOffsetAngle(turretPose);

        // The system setup to find the angle input needed for turret rotation to face the hub is a bit strange
        // It will take the turret's initial offset (typically 180 deg) and subtract the turret's absolute rotation.
        // This is the most important value, because it tells exactly what angle the turret should go to.
        Angle rotationNeededFromCenter = normalizeAngle(hubAzimuth.plus(ShooterConstants.kShooterYawOffset)
                                                        .minus(Degrees.of(robotPose.getRotation().getDegrees())));
        
        // This value is not really used in the code, but can be displayed to show the error of where
        // it should be versus where it currently is
        Angle rotationNeeded = normalizeAngle(hubAzimuth.plus(ShooterConstants.kShooterYawOffset)
                                                        .minus(Degrees.of(robotPose.getRotation().getDegrees())
                                                        .plus(getTurretAngle())));

        SmartDashboard.putNumber("Turret/Delta Angle", rotationNeeded.in(Degrees));

        boolean canRotate = Math.abs(rotationNeeded.in(Degrees)) < 88;
        SmartDashboard.putBoolean("Turret/Can Rotate to autoaim", canRotate);

        // If the robot is past the hub by 0.5 meters, turn the turret into ferrying mode.
        // Ferrying mode angle is retrieved by the negated robot angle
        // if(((distanceFromHub.getX() < -0.5 && DriverStation.getAlliance().get() == Alliance.Blue) ||
        //     (distanceFromHub.getX() > 0.5 && DriverStation.getAlliance().get() == Alliance.Red))
        //     && !DriverStation.isAutonomousEnabled()){
        //     setTurretPosition(robotPose.getRotation().unaryMinus().getDegrees());
        // }
        // else{
            setTurretPosition(rotationNeededFromCenter);
        // }
    }

    // This function gives the angle needed to rotate from the top axis (X+ axis, or forward in most cases)
  // to the hub, given the turret's pose on the field
  public Angle hubOffsetAngle(Pose2d turretPose){
    Distance kHubXPos = (DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == Alliance.Red) ? 
                                    Constants.kFieldLengthMeters.minus(Inches.of(182.11)) : 
                                    Inches.of(182.11);
    Pose2d kHubPoseCenter = new Pose2d(kHubXPos.in(Meters),Inches.of(158.84).in(Meters),new Rotation2d(0));
    Translation2d distanceFromHub = kHubPoseCenter.getTranslation().minus(turretPose.getTranslation());
    
    // getting variables for which quadrant the turret is in relative to the hub
    boolean posX = (distanceFromHub.getX() > 0);
    boolean posY = (distanceFromHub.getY() > 0);

    double angleOffset = 0;
    double angle = Math.atan(distanceFromHub.getY() / distanceFromHub.getX());

    // Coordinate angle offset jank: because atan only returns values from -pi/2 to pi/2,
    // we need to offset the angle based on which quadrant the turret is in relative to the hub
    // Also, if the angle returned from atan is not meant to add onto the corected axis, we need to subtract it from pi/2
    // which is why this is handled in a function. Janky solution, but best I could think of
    if(posX && posY){
      angleOffset = 0;
      angle = Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    } else if(posX && !posY){
      angleOffset = 3 * Math.PI / 2;
      angle = Math.PI/2-Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    } else if(!posX && !posY){
      angleOffset = Math.PI;
      angle = Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    } else if(!posX && posY){
      angleOffset = Math.PI / 2;
      angle = Math.PI/2-Math.abs(Math.atan(distanceFromHub.getY() / distanceFromHub.getX()));
    }

    // the angleModulus is to make sure the angle is between -pi and pi
    return normalizeAngle(angle + angleOffset);
  }

  public Angle normalizeAngle(double angle){
    return Radians.of(MathUtil.angleModulus(angle));
  }

  public Angle normalizeAngle(Angle angle){
    return Radians.of(MathUtil.angleModulus(angle.in(Radians)));
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
                RotateToHub();
                m_turretMotor.setControl(positionRequest.withPosition(turretSetpoint.in(Rotations)));
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
