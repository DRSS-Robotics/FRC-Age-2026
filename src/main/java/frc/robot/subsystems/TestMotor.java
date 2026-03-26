package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.hardware.TalonFX;

public class TestMotor extends SubsystemBase {

    private final TalonFX testMotor;

    public TestMotor(){
        testMotor = new TalonFX(0);
    }
    

    @Override
    public void periodic() {
        
    }

    public void run(double speed){
        testMotor.set(speed);
    }
    
}