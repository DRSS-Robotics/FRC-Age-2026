package frc.robot.subsystems.shooter;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;
import frc.robot.TestableSubsystem;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

public class ShooterSubsystem extends SubsystemBase implements TestableSubsystem {

  private TalonFX m_launchMotorL;
  private TalonFX m_launchMotorR;
  private Slot0Configs launchMotorConfigs;
  private VelocityVoltage launchRequest;
  private AngularVelocity launchMotorSetpoint = DegreesPerSecond.of(0);

  private TalonFX m_yawMotor;
  private Slot0Configs yawMotorConfigs;
  private PositionVoltage yawPositionRequest;
  private VelocityVoltage yawVelocityRequest;
  private Angle yawTargetPosition;

  public ShooterSubsystem(int launchMotorIdL, int launchMotorIdR, int pitchMotorId, int yawMotorId) {

    m_launchMotorL = new TalonFX(launchMotorIdL);
    launchMotorConfigs = new Slot0Configs();
    // Placeholder PID values
    launchMotorConfigs.kV = 0;
    launchMotorConfigs.kP = 1;
    launchMotorConfigs.kI = 0;
    launchMotorConfigs.kD = 0;
    m_launchMotorL.getConfigurator().apply(launchMotorConfigs);
    launchRequest = new VelocityVoltage(0).withSlot(0);

    m_launchMotorR = new TalonFX(launchMotorIdR);
    launchMotorConfigs = new Slot0Configs();
    // Placeholder PID values
    launchMotorConfigs.kV = 0;
    launchMotorConfigs.kP = 1;
    launchMotorConfigs.kI = 0;
    launchMotorConfigs.kD = 0;
    m_launchMotorL.getConfigurator().apply(launchMotorConfigs);
    launchRequest = new VelocityVoltage(0).withSlot(0);

    m_yawMotor = new TalonFX(yawMotorId);
    yawMotorConfigs = new Slot0Configs();
    // Placeholder PID values
    yawMotorConfigs.kV = 0;
    yawMotorConfigs.kP = 1;
    yawMotorConfigs.kI = 0;
    yawMotorConfigs.kD = 0;
    m_yawMotor.getConfigurator().apply(yawMotorConfigs);
    yawPositionRequest = new PositionVoltage(0).withSlot(0);
    yawVelocityRequest = new VelocityVoltage(0).withSlot(1);

  }

  // in degrees
  public void setYawMotorPosition(double speed) {
    setYawMotorPosition(Degrees.of(speed));
  }

  public void setYawMotorPosition(Angle pos) {
    double correctedAngle = pos.in(Degrees) % 360;
    m_yawMotor.setControl(yawPositionRequest.withPosition(Degrees.of(correctedAngle)));
  }

  // in degrees per second
  public void driveYawMotor(double degreesPerSecond) {
    driveYawMotor(DegreesPerSecond.of(degreesPerSecond));
  }

  public void driveYawMotor(AngularVelocity speed) {
    m_yawMotor.setControl(yawVelocityRequest.withVelocity(speed));
  }

  public Angle getYawEncoder() {
    return m_yawMotor.getPosition(true).getValue();
  }

  public Angle getYawSetpoint() {
    return yawTargetPosition;
  }

  public void runLaunchMotors(double speed) {
    runLaunchMotors(DegreesPerSecond.of(speed));
  }

  public AngularVelocity getLaunchMotorSpeed() {
    return m_launchMotorL.getVelocity(true).getValue();
  }

  public AngularVelocity getLaunchMotorSetpoint() {
    return launchMotorSetpoint;
  }

  public void runLaunchMotors(AngularVelocity speed) {
    launchMotorSetpoint = speed;
    m_launchMotorL.setControl(launchRequest.withVelocity(speed));
    m_launchMotorR.setControl(launchRequest.withVelocity(speed));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

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
          private String output;

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
              output = "The yaw motor took too long to get to the desired position.";
              return TestResult.KNOWN_FAILURE;

            }
            if (currentYawMotorAngle.isNear(ShooterConstants.kTestYawMotorTargetPosition, maxAllowedError)) {
              output = "The yaw motor succesfully moved to the desired position in the specified amount of time. ";
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
          private String output = "";

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
              output = "The shooter motors took too long to spin up";
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