package frc.robot.subsystems.shooter;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
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
  private final InterpolatingDoubleTreeMap distanceToTime;
  private final Supplier<ChassisSpeeds> speedsSupplier;
  private final Supplier<Pose3d> poseSupplier;

  private final TurretControl m_turretController;

  public ShooterSubsystem(int launchMotorID, int turretMotorID, Supplier<Pose3d> poseSupplier, Supplier<ChassisSpeeds> velocitySupplier, TurretControl turret) {
    
    m_launchMotor = new TalonFX(launchMotorID);
    launchMotorConfigs = new Slot0Configs();
    //Placeholder PID values
    launchMotorConfigs.kV = 0;
    launchMotorConfigs.kP = 1;
    launchMotorConfigs.kI = 0;
    launchMotorConfigs.kD = 0;
    m_launchMotor.getConfigurator().apply(launchMotorConfigs);
    launchRequest = new VelocityVoltage(0).withSlot(0);
    
    distanceToTime = new InterpolatingDoubleTreeMap();

    // TODO: get these to be real numbers
    distanceToTime.put(1.0,2.0);
    distanceToTime.put(2.0,4.0);
    distanceToTime.put(3.0,8.0);
    distanceToTime.put(4.0,16.0);
    distanceToTime.put(5.0,32.0);
    distanceToTime.put(6.0,64.0);

    this.poseSupplier = poseSupplier;
    this.speedsSupplier = velocitySupplier;
    m_turretController = turret;
  }

  public void runLaunchMotor(double speed) {
    runLaunchMotor(DegreesPerSecond.of(speed));
  }

  public void runLaunchMotor(AngularVelocity speed) {
    m_launchMotor.setControl(launchRequest.withVelocity(speed));
  }

  public TurretControl getTurretControl() {
    return m_turretController;
  }

  public double getDistanceToTime(double distance) {
    return distanceToTime.get(distance);
  }
  public Pose3d getPose3d() {
    return poseSupplier.get();
  }
  public ChassisSpeeds getChassisSpeeds() {
    return speedsSupplier.get();
  }
}