package frc.robot.subsystems.shooter;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.function.Supplier;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

public class ShooterSubsystem extends SubsystemBase {

  private TalonFX m_launchMotor;
  private TalonFX m_pitchMotor;
  private TalonFX m_yawMotor;
  private Slot0Configs launchMotorConfigs;
  private Slot0Configs pitchMotorConfigs;
  private Slot0Configs yawMotorConfigs;
  private VelocityVoltage launchRequest;
  private PositionVoltage pitchRequest;
  private PositionVoltage yawPositionRequest;
  private VelocityVoltage yawVelocityRequest;
  private Angle targetPosition;

  public static final Angle MIN_PITCH_ANGLE = Degrees.of(ShooterConstants.kMinPitchDegrees);
  public static final Angle MAX_PITCH_ANGLE = Degrees.of(ShooterConstants.kMaxPitchDegrees);

  private final TurretControl m_turretController;

  public ShooterSubsystem(int launchMotorID, int turretMotorID, Supplier<Pose3d> poseSupplier) {
    
    m_launchMotor = new TalonFX(launchMotorID);
    launchMotorConfigs = new Slot0Configs();
    //Placeholder PID values
    launchMotorConfigs.kV = 0;
    launchMotorConfigs.kP = 1;
    launchMotorConfigs.kI = 0;
    launchMotorConfigs.kD = 0;
    m_launchMotor.getConfigurator().apply(launchMotorConfigs);
    launchRequest = new VelocityVoltage(0).withSlot(0);

    m_yawMotor = new TalonFX(yawMotorId);
    yawMotorConfigs = new Slot0Configs();
    //Placeholder PID values
    yawMotorConfigs.kV = 0;
    yawMotorConfigs.kP = 1;
    yawMotorConfigs.kI = 0;
    yawMotorConfigs.kD = 0;
    m_yawMotor.getConfigurator().apply(yawMotorConfigs);
    yawPositionRequest = new PositionVoltage(0).withSlot(0);
    yawVelocityRequest = new VelocityVoltage(0).withSlot(0);
  
  }

  // in degrees
  public void setYawMotorPosition(double speed) {
    setYawMotorPosition(Degrees.of(speed));
  }

  public void setYawMotorPosition(Angle speed) {
    m_yawMotor.setControl(yawPositionRequest.withPosition(speed));
  }

  //in degrees per second
  public void driveYawMotor(double speed) {
    driveYawMotor(DegreesPerSecond.of(speed));
  }

  public void driveYawMotor(AngularVelocity speed) {
    m_yawMotor.setControl(yawVelocityRequest.withVelocity(speed));
  }

  public void runLaunchMotor(double speed) {
    runLaunchMotor(DegreesPerSecond.of(speed));
  }

  public void runLaunchMotor(AngularVelocity speed) {
    m_launchMotor.setControl(launchRequest.withVelocity(speed));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}