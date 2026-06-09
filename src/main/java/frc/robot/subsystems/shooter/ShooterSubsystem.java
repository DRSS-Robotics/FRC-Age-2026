package frc.robot.subsystems.shooter;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;
import frc.robot.TestableSubsystem;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;

import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

public class ShooterSubsystem extends SubsystemBase implements TestableSubsystem {

  private TalonFX m_launchMotorL;
  private TalonFX m_launchMotorR;
  private Slot0Configs launchMotorConfigs;
  private VelocityVoltage launchRequestL;
  private VelocityVoltage launchRequestR;
  private AngularVelocity launchMotorSetpoint = DegreesPerSecond.of(0);

  private final TrapezoidProfile launchTrapezoidProfile = new TrapezoidProfile(
      new TrapezoidProfile.Constraints(ShooterConstants.kMaxShooterDPS2,
          ShooterConstants.kMaxShooterDPS3));

  private TrapezoidProfile.State launchVelocityGoal = new TrapezoidProfile.State();
  private TrapezoidProfile.State launchVelocitySetpoint = new TrapezoidProfile.State();

  private TalonFX m_yawMotor;
  private Slot0Configs yawMotorPositionConfigs;
  private Slot1Configs yawMotorVelocityConfigs;
  private Angle yawTargetPosition;

  private DoublePublisher turretSpeedPublisher;

  public ShooterSubsystem(int launchMotorIdL, int launchMotorIdR, int yawMotorId, NetworkTable table) {

    m_launchMotorL = new TalonFX(launchMotorIdL);
    launchMotorConfigs = new Slot0Configs();
    // Placeholder PID values
    launchMotorConfigs.kS = 0.2;
    launchMotorConfigs.kV = 0.8;
    launchMotorConfigs.kP = 0.04;
    launchMotorConfigs.kI = 0;
    launchMotorConfigs.kD = 0;
    m_launchMotorL.getConfigurator().apply(launchMotorConfigs);
    launchRequestL = new VelocityVoltage(0).withSlot(0);

    m_launchMotorR = new TalonFX(launchMotorIdR);
    m_launchMotorR.getConfigurator().apply(launchMotorConfigs);
    launchRequestR = new VelocityVoltage(0).withSlot(0);

    m_yawMotor = new TalonFX(yawMotorId);
    yawMotorPositionConfigs = new Slot0Configs();
    // Placeholder PID values
    yawMotorPositionConfigs.kV = 0;
    yawMotorPositionConfigs.kP = 0;
    yawMotorPositionConfigs.kI = 0;
    yawMotorPositionConfigs.kD = 0;
    m_yawMotor.getConfigurator().apply(yawMotorPositionConfigs);

    yawMotorVelocityConfigs = new Slot1Configs();
    // Placeholder PID values
    yawMotorVelocityConfigs.kV = 0;
    yawMotorVelocityConfigs.kP = 0;
    yawMotorVelocityConfigs.kI = 0;
    yawMotorVelocityConfigs.kD = 0;
    m_yawMotor.getConfigurator().apply(yawMotorVelocityConfigs);

    new PositionVoltage(0).withSlot(0);
    new VelocityVoltage(0).withSlot(1);

    table.getDoubleTopic("turretPosition").publish();
    turretSpeedPublisher = table.getDoubleTopic("turretFlywheelSpeed").publish();
  }

  // in degrees
  public void setYawMotorPosition(double speed) {
    setYawMotorPosition(Degrees.of(speed));
  }

  public void setYawMotorPosition(Angle pos) {
  }

  // in degrees per second
  public void driveYawMotor(double degreesPerSecond) {
    // driveYawMotor(DegreesPerSecond.of(degreesPerSecond));
  }

  public void driveYawMotor(AngularVelocity speed) {
    // m_yawMotor.setControl(yawVelocityRequest.withVelocity(speed));
  }

  public Angle getYawEncoder() {
    return m_yawMotor.getPosition(true).getValue();
  }

  public Angle getYawSetpoint() {
    return yawTargetPosition;
  }

  public AngularVelocity getLaunchMotorSpeed() {
    return m_launchMotorL.getVelocity(true).getValue();
  }

  public AngularVelocity getLaunchMotorSetpoint() {
    return launchMotorSetpoint;
  }

  public void runLaunchMotors(double degreesPerSecond) {
    runLaunchMotors(DegreesPerSecond.of(degreesPerSecond));
  }

  public void runLaunchMotors(AngularVelocity speed) {
    launchMotorSetpoint = speed;
    launchVelocityGoal = new TrapezoidProfile.State(speed.in(DegreesPerSecond), 0);


  }

  @Override
  public void periodic() {
    if (getYawEncoder().isNear(Degrees.of(0), Degrees.of(5)) ||
        getYawEncoder().isNear(Degrees.of(360), Degrees.of(5))) {
      driveYawMotor(0);
    }

    launchVelocitySetpoint = launchTrapezoidProfile.calculate(0.02, launchVelocitySetpoint,
        launchVelocityGoal);



    m_launchMotorL.setControl(launchRequestL.withVelocity(DegreesPerSecond.of(launchVelocitySetpoint.position)));
    m_launchMotorR.setControl(launchRequestR.withVelocity(DegreesPerSecond.of(launchVelocitySetpoint.position)));

    // turretPositionPublisher.set(getYawEncoder().in(Degrees));
    turretSpeedPublisher.set(Math.abs(getLaunchMotorSpeed().in(DegreesPerSecond)));

  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }

  @Override
  public TestableCommand getTestCommand() {
    return new SequencedTest(this,

        new TestBase(this) {
          // testing yaw motor
          private double startTime;
          private Angle maxAllowedError;
          @Override
          public void onInitialize() {
            startTime = Timer.getFPGATimestamp();
            maxAllowedError = ShooterConstants.kTestYawMotorTargetPosition
                .times(ShooterConstants.kMaxTestYawMotorErrorPercentage / 100);
          }

          @Override
          public TestResult getCurrentResult() {
            Angle currentYawMotorAngle = m_yawMotor.getPosition().getValue();
            if (Timer.getFPGATimestamp()
                - startTime >= ShooterConstants.kMaxTestYawMotorTimeToReachPosition) {
              return TestResult.KNOWN_FAILURE;

            }
            if (currentYawMotorAngle.isNear(ShooterConstants.kTestYawMotorTargetPosition, maxAllowedError)) {
              return TestResult.SUCCESS;
            }
            return TestResult.IN_PROGRESS;
          }
        },

        new TestBase(this) {
          // testing shooter motors
          private double startTime;
          private AngularVelocity maxAllowedError;
          private AngularVelocity targetSpeed = DegreesPerSecond.of(ShooterConstants.kMaxTestLaunchMotorTargetDPS);
          @Override
          public void onInitialize() {
            startTime = Timer.getFPGATimestamp();
            maxAllowedError = targetSpeed
                .times(ShooterConstants.kMaxTestLaunchMotorSpeedErrorPercentage / 100);
          }

          @Override
          public TestResult getCurrentResult() {
            AngularVelocity currentSpeed = getLaunchMotorSpeed();
            if (Timer.getFPGATimestamp() -
                startTime >= ShooterConstants.kMaxTestLaunchMotorTimeToSpinUp) {
              return TestResult.KNOWN_FAILURE;
            }
            if (currentSpeed.isNear(targetSpeed, maxAllowedError)) {
              return TestResult.SUCCESS;
            }
            return TestResult.IN_PROGRESS;
          }
        });
  }
}