package frc.robot.subsystems;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Utils;
import frc.robot.Constants.SuperstructureConstants;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;


public class SuperstructureSubsystem extends SubsystemBase {
    
    private TalonFX m_intakeMotor;
    private SlotConfigs intakeMotorConfigs;
    private VelocityVoltage intakeMotorRequest;
    private AngularVelocity intakeMotorSetSpeed = DegreesPerSecond.of(0);

    private TalonFX m_storageMotor;
    private StorageWallState storageState;
    private SlotConfigs storageMotorConfigs;
    private PositionVoltage storageMotorRequest;
    private Angle storageWallSetpoint = Degrees.of(0);

    private TalonFX m_agitatorMotor;
    private SlotConfigs agitatorMotorConfigs;
    private VelocityVoltage agitatorMotorRequest;
    private AngularVelocity agitatorMotorSetSpeed = DegreesPerSecond.of(0);

    private TalonFX m_transferMotor;
    private SlotConfigs transferMotorConfigs;
    private VelocityVoltage transferMotorRequest;
    private AngularVelocity transferMotorSetSpeed = DegreesPerSecond.of(0);

    public static Angle storageClosedAngle = Degrees.of(SuperstructureConstants.kStorageClosedRotations);
    public static Angle storageOpenAngle = Degrees.of(SuperstructureConstants.kStorageOpenRotations);
    public static Angle storageAngleTolerance = Degrees.of(SuperstructureConstants.kStorageStateTolerance);



    /**
     * Subsystem that encompasses both the over-the-bumper intake as well as Fuel storage.
     */
    public SuperstructureSubsystem(int intakeMotorId, int wallMotorId, int agitatorMotorId, int transferMotorId) {
        m_intakeMotor = new TalonFX(intakeMotorId);
        m_storageMotor = new TalonFX(wallMotorId);
        m_agitatorMotor = new TalonFX(agitatorMotorId);
        m_transferMotor = new TalonFX(transferMotorId);
        
        // TODO: use actual PID values instead of placeholder
        intakeMotorConfigs = 
            Utils.configureTalonGains(m_intakeMotor, 0, 0, 0, 0, 0);
        intakeMotorRequest = new VelocityVoltage(0).withSlot(0);

        storageMotorConfigs = 
            Utils.configureTalonGains(m_storageMotor, 0, 0, 0, 0, 0);
        storageMotorRequest = new PositionVoltage(0).withSlot(0);

        agitatorMotorConfigs = 
            Utils.configureTalonGains(m_agitatorMotor, 0, 0, 0, 0, 0);
        agitatorMotorRequest = new VelocityVoltage(0).withSlot(0);

        transferMotorConfigs = 
            Utils.configureTalonGains(m_transferMotor, 0, 0, 0, 0, 0);
        transferMotorRequest = new VelocityVoltage(0).withSlot(0);
     
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



    public void runAgitatorMotor(double speed) {
        runAgitatorMotor(DegreesPerSecond.of(speed));
    }



    public void runAgitatorMotor(AngularVelocity speed) {
        agitatorMotorSetSpeed = speed;
        m_agitatorMotor.setControl(agitatorMotorRequest.withVelocity(speed));
    }



    public void runTransferMotor(double speed) {
        runTransferMotor(DegreesPerSecond.of(speed));
    }



    public void runTransferMotor(AngularVelocity speed) {
        transferMotorSetSpeed = speed;
        m_transferMotor.setControl(transferMotorRequest.withVelocity(speed));
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