package frc.robot.subsystems.shooter;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.TestableSubsystem;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;

import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;

public class ShooterSubsystem extends SubsystemBase implements TestableSubsystem {

  private TalonFX m_launchMotorL;
  private TalonFX m_launchMotorR;
  private Slot0Configs launchMotorConfigs;
  private MotorOutputConfigs directionalConfigs = new MotorOutputConfigs();
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
    private PositionVoltage yawPositionRequest;
    private VelocityVoltage yawVelocityRequest;
    private Angle yawTargetPosition;

    private AngularVelocity yawMotorSetpoint = DegreesPerSecond.of(0);
    private TrapezoidProfile.State yawVelocityGoal = new TrapezoidProfile.State();
    private TrapezoidProfile.State yawVelocitySetpoint = new TrapezoidProfile.State();
    
    private TalonFX m_hoodMotor;
    private Slot0Configs hoodMotorVelocityConfigs;
  private PositionVoltage hoodPositionRequest;
  private VelocityVoltage hoodVelocityRequest;
  private AngularVelocity hoodMotorSetpoint = DegreesPerSecond.of(0);
  private TrapezoidProfile.State hoodVelocityGoal = new TrapezoidProfile.State();
  private TrapezoidProfile.State hoodVelocitySetpoint = new TrapezoidProfile.State();


  private DoublePublisher turretPositionPublisher;
  private DoublePublisher turretSpeedPublisher;

  public ShooterSubsystem(int launchMotorIdL, int launchMotorIdR, int yawMotorId, int hoodMotorId, NetworkTable table) {

    m_launchMotorL = new TalonFX(launchMotorIdL);
    launchMotorConfigs = new Slot0Configs();
    // Placeholder PID values
    launchMotorConfigs.kS = 0.2;
    launchMotorConfigs.kV = 0.8;
    launchMotorConfigs.kP = 0.04;
    launchMotorConfigs.kI = 0;
    launchMotorConfigs.kD = 0;

    //invert the left motor to match the right motor's direction
    directionalConfigs.Inverted = InvertedValue.CounterClockwise_Positive;
    m_launchMotorL.getConfigurator().apply(launchMotorConfigs);
    m_launchMotorL.getConfigurator().apply(directionalConfigs);
    launchRequestL = new VelocityVoltage(0).withSlot(0);
    
    m_launchMotorR = new TalonFX(launchMotorIdR);
    directionalConfigs.Inverted = InvertedValue.Clockwise_Positive;
    m_launchMotorR.getConfigurator().apply(launchMotorConfigs);
    m_launchMotorR.getConfigurator().apply(directionalConfigs);
    launchRequestR = new VelocityVoltage(0).withSlot(0);

    m_hoodMotor = new TalonFX(hoodMotorId);
    
    hoodMotorVelocityConfigs = new Slot0Configs();
    //placeholder ids again 
    hoodMotorVelocityConfigs.kS = 0.2;
    hoodMotorVelocityConfigs.kV = 0;
    hoodMotorVelocityConfigs.kP = 1.0;
    hoodMotorVelocityConfigs.kI = 0;
    hoodMotorVelocityConfigs.kD = 0;
    m_hoodMotor.getConfigurator().apply(hoodMotorVelocityConfigs);
    directionalConfigs.Inverted = InvertedValue.Clockwise_Positive;
    m_hoodMotor.getConfigurator().apply(directionalConfigs);
    hoodVelocityRequest = new VelocityVoltage(0).withSlot(0);

    m_yawMotor = new TalonFX(yawMotorId);
    yawMotorPositionConfigs = new Slot0Configs();
    // Placeholder PID values
    yawMotorPositionConfigs.kV = 0;
    yawMotorPositionConfigs.kP = 1.0;
    yawMotorPositionConfigs.kI = 0;
    yawMotorPositionConfigs.kD = 0;
    m_yawMotor.getConfigurator().apply(yawMotorPositionConfigs);

    yawMotorVelocityConfigs = new Slot1Configs();
    // Placeholder PID values
    yawMotorVelocityConfigs.kS = 0.2;
    yawMotorVelocityConfigs.kV = 0;
    yawMotorVelocityConfigs.kP = 1.0;
    yawMotorVelocityConfigs.kI = 0;
    yawMotorVelocityConfigs.kD = 0;
    m_yawMotor.getConfigurator().apply(yawMotorVelocityConfigs);

    yawPositionRequest = new PositionVoltage(0).withSlot(0);
    yawVelocityRequest = new VelocityVoltage(0).withSlot(1);

    turretPositionPublisher = table.getDoubleTopic("turretPosition").publish();
    turretSpeedPublisher = table.getDoubleTopic("turretFlywheelSpeed").publish();
  }

  // in degrees
  public void setYawMotorPosition(double speed) {
    setYawMotorPosition(Degrees.of(speed));
  }

  public void setYawMotorPosition(Angle pos) {
    double correctedAngle = pos.in(Degrees) % 360;
    // m_yawMotor.setControl(yawPositionRequest.withPosition(Degrees.of(correctedAngle)));
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
  
  //hood control code
  public AngularVelocity getHoodMotorSpeed() {
    return m_hoodMotor.getVelocity(true).getValue();
  }

  public double getHoodEncoderPosition(){
    return m_hoodMotor.getPosition().getValueAsDouble();
  }
  
  // public AngularVelocity getHoodMotorSetpoint() {
  //   return hoodMotorSetPoint;
  // }
  
  // public void runHoodMotors(double degreesPerSecond) {
  //   runHoodMotors(DegreesPerSecond.of(degreesPerSecond));
  // }
  
  public void runHoodMotors(AngularVelocity speed) {
    hoodMotorSetpoint = speed;
    hoodVelocityGoal = new TrapezoidProfile.State(speed.in(DegreesPerSecond), 0);
  }

     public void runYawMotor(AngularVelocity speed) {
    yawMotorSetpoint = speed;
    yawVelocityGoal = new TrapezoidProfile.State(speed.in(DegreesPerSecond), 0);
  }

  @Override
  public void periodic() {
    if (getYawEncoder().isNear(Degrees.of(0), Degrees.of(5)) ||
        getYawEncoder().isNear(Degrees.of(360), Degrees.of(5))) {
      driveYawMotor(0);
    }

    launchVelocitySetpoint = launchTrapezoidProfile.calculate(0.02, launchVelocitySetpoint,
        launchVelocityGoal);
    hoodVelocitySetpoint = launchTrapezoidProfile.calculate(0.02, hoodVelocitySetpoint,
        hoodVelocityGoal);
    yawVelocitySetpoint = launchTrapezoidProfile.calculate(0.02, yawVelocitySetpoint,
        yawVelocityGoal);


    m_launchMotorL.setControl(launchRequestL.withVelocity(DegreesPerSecond.of(launchVelocitySetpoint.position)));
    m_launchMotorR.setControl(launchRequestR.withVelocity(DegreesPerSecond.of(launchVelocitySetpoint.position)));

    m_hoodMotor.setControl(hoodVelocityRequest.withVelocity(DegreesPerSecond.of(hoodVelocitySetpoint.position)));  

    m_yawMotor.setControl(yawVelocityRequest.withVelocity(DegreesPerSecond.of(yawVelocitySetpoint.position)));  

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