package frc.robot.subsystems.shooter;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

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
  private Slot0Configs launchMotorConfigs;
  private Slot0Configs pitchMotorConfigs;
  private VelocityVoltage launchRequest;
  private PositionVoltage pitchRequest;
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
    
    m_turretController = new TurretControl(turretMotorID);

    // m_pitchMotor = new TalonFX(pitchMotorId);
    // pitchMotorConfigs = new Slot0Configs();
    // //Placeholder PID values
    // pitchMotorConfigs.kV = 0;
    // pitchMotorConfigs.kP = 1;
    // pitchMotorConfigs.kI = 0;
    // pitchMotorConfigs.kD = 0;
    // m_pitchMotor.getConfigurator().apply(pitchMotorConfigs);
    // pitchRequest = new PositionVoltage(0).withSlot(0);
  }

  public TurretControl getTurretControl(){
    return m_turretController;
  }

   

  public void runLaunchMotor(double speed) {
    runLaunchMotor(DegreesPerSecond.of(speed));
  }

  public void runLaunchMotor(AngularVelocity speed) {
    m_launchMotor.setControl(launchRequest.withVelocity(speed));
  }


  public void setPitchMotorPosition(double degrees) {
    setPitchMotorPosition(Degrees.of(degrees));

  }

  public void setPitchMotorPosition(Angle angle) {
    if (angle.lt(MIN_PITCH_ANGLE) || angle.gt(MAX_PITCH_ANGLE)) {
      return;
    }

    targetPosition = angle;
    m_pitchMotor.setControl(pitchRequest.withPosition(angle));
  }



  public Angle getDistanceFromTarget() {
    Angle current = m_pitchMotor.getPosition().getValue();
    return Degrees.of(targetPosition.minus(current).abs(Degrees));
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