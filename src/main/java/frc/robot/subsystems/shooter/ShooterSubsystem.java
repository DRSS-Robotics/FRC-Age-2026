package frc.robot.subsystems.shooter;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.DegreesPerSecond;

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
  public ShooterSubsystem(int launchMotorId, int pitchMotorId) {
    
    m_launchMotor = new TalonFX(launchMotorId);
    launchMotorConfigs = new Slot0Configs();
    //Placeholder PID values
    launchMotorConfigs.kV = 0;
    launchMotorConfigs.kP = 1;
    launchMotorConfigs.kI = 0;
    launchMotorConfigs.kD = 0;
    m_launchMotor.getConfigurator().apply(launchMotorConfigs);
    launchRequest = new VelocityVoltage(0).withSlot(0);
    
    m_pitchMotor = new TalonFX(pitchMotorId);
    pitchMotorConfigs = new Slot0Configs();
    //Placeholder PID values
    pitchMotorConfigs.kV = 0;
    pitchMotorConfigs.kP = 1;
    pitchMotorConfigs.kI = 0;
    pitchMotorConfigs.kD = 0;
    m_pitchMotor.getConfigurator().apply(pitchMotorConfigs);
    pitchRequest = new PositionVoltage(0).withSlot(0);
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