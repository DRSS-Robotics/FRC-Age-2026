// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HangSubsystem extends SubsystemBase {
  /** Creates a new ExampleSubsystem. */

  public static Angle hangGroundElevation = Degrees.of(HangConstants.kHangGroundRotations);
  public static Angle hangL1Elevation = Degrees.of(HangConstants.kHangL1Rotations);
  public static Angle hangLevelTolerance = Degrees.of(HangConstants.kHangLevelTolerance);

  public HangSubsystem() {}
  m_hangMotor = new TalonFX(hangMotorid);
  
  hangMotorConfigs = 
            Utils.configureTalonGains(m_hangMotor, 0, 0, 0, 0, 0);
        hangMotorRequest = new VelocityVoltage(0).withSlot(0);
  public void setHangMotorPosition(Angle newPosition) {
        if (newPosition.lt(hangGroundElevation) || 
            newPosition.gt(hangL1Elevation)) {
            System.out.println(
                "Superstructure storage wall setpoint is outside bounds, ignoring request");
            return;
        }

        hangSetpoint = newPosition;
        m_hangMotor.setControl(storageMotorRequest.withPosition(newPosition));
    }
   * Example command factory method.
   *
   * @return a command
   */
  public Command exampleMethodCommand() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }

  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
    }
  public StorageWallState getStorageState() {
      Angle storageWallCurrentPosition = m_storageMotor.getPosition(true).getValue();

      if (hangCurrentPosition.isNear(hangGroundElevation, hangLevelTolerance) && 
          hangCurrentPosition.isNear(hangSetpoint, storageAngleTolerance)) {
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
} 