package frc.robot.subsystems;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.SuperstructureConstants;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;


public class SuperstructureSubsystem extends SubsystemBase {
    
    private TalonFX m_intakeMotor;
    private Slot0Configs intakeMotorConfigs;
    private VelocityVoltage intakeMotorRequest;
    private AngularVelocity intakeMotorSetSpeed = DegreesPerSecond.of(0);

    private TalonFX m_storageMotor;
    private StorageWallState storageState;
    private Slot0Configs storageMotorConfigs;
    private PositionVoltage storageMotorRequest;
    private Angle storageWallSetpoint = Degrees.of(0);

    public static Angle storageClosedAngle = Degrees.of(SuperstructureConstants.kStorageClosedRotations);
    public static Angle storageOpenAngle = Degrees.of(SuperstructureConstants.kStorageOpenRotations);
    public static Angle storageAngleTolerance = Degrees.of(SuperstructureConstants.kStorageStateTolerance);



    /**
     * Subsystem that encompasses both the over-the-bumper intake as well as Fuel storage.
     */
    public SuperstructureSubsystem(int intakeMotorId, int wallMotorId) {
        m_intakeMotor = new TalonFX(intakeMotorId);
        m_storageMotor = new TalonFX(wallMotorId);
        
        // TODO: use actual PID values instead of placeholder
        intakeMotorConfigs = new Slot0Configs();
        intakeMotorConfigs.kV = 0;
        intakeMotorConfigs.kP = 1;
        intakeMotorConfigs.kI = 0;
        intakeMotorConfigs.kD = 0;
        m_intakeMotor.getConfigurator().apply(intakeMotorConfigs);
        intakeMotorRequest = new VelocityVoltage(0).withSlot(0);
        
        // TODO: use actual PID values instead of placeholder
        storageMotorConfigs = new Slot0Configs();
        storageMotorConfigs.kV = 0.02;
        storageMotorConfigs.kP = 6;
        storageMotorConfigs.kI = 0;
        storageMotorConfigs.kD = 0;
        m_storageMotor.getConfigurator().apply(storageMotorConfigs);
        storageMotorRequest = new PositionVoltage(0).withSlot(0);
    }



    /**
     * @param speed in degrees per second
     */
    public void runIntake(double speed) {
        runIntake(DegreesPerSecond.of(speed));
    }



    public void runIntake(AngularVelocity speed) {
        intakeMotorSetSpeed = speed;
        m_intakeMotor.setControl(intakeMotorRequest.withVelocity(speed));
    }



    public AngularVelocity getIntakeSpeed() {
        return intakeMotorSetSpeed;
    }
    
    

    /**
     * Sets the PID setpoint (in DEGREES) of the wall motor. Values exceeding the 
     * bounds in {@link SuperstructureConstants} will automatically be ignored 
     * @param newPosition rotational setpoint value.
     * 
     * @see #setWallMotorPosition(Angle)
     */
    public void setWallMotorPosition(double newPosition) {
        setWallMotorPosition(Degrees.of(newPosition));
    }



    /**
     * Sets the PID setpoint of the wall motor. Values exceeding the 
     * bounds in {@link SuperstructureConstants} will automatically be ignored 
     * @param newPosition rotational setpoint value.
     */
    public void setWallMotorPosition(Angle newPosition) {
        if (newPosition.lt(storageClosedAngle) || 
            newPosition.gt(storageOpenAngle)) {
            System.out.println(
                "Superstructure storage wall setpoint is outside bounds, ignoring request");
            return;
        }

        storageWallSetpoint = newPosition;
        m_storageMotor.setControl(storageMotorRequest.withPosition(newPosition));
    }



    /**
     * @return a {@link StorageWallState} enum representing the storage wall's state
     */
    public StorageWallState getStorageState() {
        Angle storageWallCurrentPosition = m_storageMotor.getPosition(true).getValue();

        if (storageWallCurrentPosition.isNear(storageClosedAngle, storageAngleTolerance) && 
            storageWallCurrentPosition.isNear(storageWallSetpoint, storageAngleTolerance)) {
            storageState = StorageWallState.kIsClosed; 
        } else if (storageWallCurrentPosition.isNear(storageOpenAngle, storageAngleTolerance) && 
            storageWallCurrentPosition.isNear(storageWallSetpoint, storageAngleTolerance)) {
            storageState = StorageWallState.kIsOpen; 
        } else if (storageWallSetpoint.isNear(storageClosedAngle, storageAngleTolerance)) {
            storageState = StorageWallState.kIsClosing; 
        } else if (storageWallSetpoint.isNear(storageOpenAngle, storageAngleTolerance)) {
            storageState = StorageWallState.kIsOpening; 
        } else {
            storageState = StorageWallState.kCustom;
        }

        return storageState;
    } 

    
    
    /**
     * Describes the current state of the Fuel storage wall. {@code kIsClosed} means 
     * that the wall has reached the {@link SuperstructureConstants#kStorageClosedRotations 
     * closed position} and is currently idling. {@code kIsClosing} is similar, with the 
     * distinction that it instead means the wall is currently on the way to that setpoint.
     * 
     * <p> The {@code kIsOpen} and {@code kIsOpening} states represent the same, in reverse 
     * (using the {@link SuperstructureConstants#kStorageOpenRotations open position})
     * 
     * <p> Possible values: {@link #kIsClosed}, {@link #kIsClosing}, {@link #kIsOpen}, 
     * {@link #kIsOpening}, {@link #kCustom}
     */
    public static enum StorageWallState {
        kIsClosed,
        kIsClosing,
        kIsOpen,
        kIsOpening,
    
        // special case for manual control
        kCustom
    }
}