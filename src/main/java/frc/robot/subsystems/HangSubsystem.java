// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot.subsystems;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import frc.robot.Utils;
import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;


import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.HangConstants;
public class HangSubsystem extends SubsystemBase {
  /** Creates a new ExampleSubsystem. */

  public static Angle hangGroundElevation = Degrees.of(HangConstants.kHangGroundRotations);
  public static Angle hangL1Elevation = Degrees.of(HangConstants.kHangL1Rotations);
  public static Angle hangLevelTolerance = Degrees.of(HangConstants.kHangLevelTolerance);


  private TalonFX m_hangMotor;
  private SlotConfigs hangMotorConfigs;
  private PositionVoltage hangMotorPositionRequest;
  private VelocityVoltage hangMotorSpeedRequest;
  private Angle hangSetpoint = Degrees.of(0);
  private HangState hangState;
  private AngularVelocity hangMotorSetSpeed = DegreesPerSecond.of(0);

  private boolean canMoveDown;
  private boolean canMoveUp;

 

  public HangSubsystem(int hangMotorId) {
    m_hangMotor = new TalonFX(hangMotorId);
    hangMotorConfigs = 
        Utils.configureTalonGains(m_hangMotor, 0, 0, 0, 0, 0);
    hangMotorPositionRequest = new PositionVoltage(0).withSlot(0);
    hangMotorSpeedRequest = new VelocityVoltage(0).withSlot(0);
  }
  



  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    Angle newPosition = m_hangMotor.getPosition().getValue();
    
    canMoveUp = newPosition.lt(hangL1Elevation);
    canMoveDown = newPosition.gt(hangGroundElevation);

    // checking if the motor can't move based on if it's exceeded the bounds
    if (!canMoveUp ||
        !canMoveDown) {
          System.out.println("The hang attempted to go out of bounds; preventative measures taken. ");
          setHangMotorSpeed(0); 
        }
  }



  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
    }



  /**
   * converts the speed from a double into a usable format 
   * for the motor and then runs the motor
   * @param speed in degrees per second
   */
  public void setHangMotorSpeed(double speed) {
    setHangMotorSpeed(DegreesPerSecond.of(speed));
  }



  public void setHangMotorSpeed(AngularVelocity speed) {
      hangMotorSetSpeed = speed;
      boolean isMovingUp = speed.gt(DegreesPerSecond.of(0));

      if ((isMovingUp && canMoveUp) || !isMovingUp && canMoveDown) {
        m_hangMotor.setControl(hangMotorSpeedRequest.withVelocity(speed));
      }
  }



    public void setHangMotorPosition(double newPosition) {
      setHangMotorPosition(Degrees.of(newPosition));
  }



  /**
   * guh
   * @param newPosition
   */
  public void setHangMotorPosition(Angle newPosition) {
    if (newPosition.lt(hangGroundElevation) || 
        newPosition.gt(hangL1Elevation)) {
        System.out.println(
        "Superstructure storage wall setpoint is outside bounds, ignoring request");
        return;
    }

    hangSetpoint = newPosition;
    m_hangMotor.setControl(hangMotorPositionRequest.withPosition(newPosition));
  }



  public HangState getHangState() {
      Angle hangCurrentPosition = m_hangMotor.getPosition().getValue();

      if (hangCurrentPosition.isNear(hangGroundElevation, hangLevelTolerance) && 
          hangCurrentPosition.isNear(hangSetpoint, hangLevelTolerance)) {
          hangState = HangState.kIsGrounded; 
      } else if (hangCurrentPosition.isNear(hangL1Elevation, hangLevelTolerance) && 
          hangCurrentPosition.isNear(hangSetpoint, hangLevelTolerance)) {
          hangState = HangState.kIsL1; 
      } else if (hangSetpoint.isNear(hangGroundElevation, hangLevelTolerance)) {
          hangState = HangState.kIsGrounded; 
      } else if (hangSetpoint.isNear(hangL1Elevation, hangLevelTolerance)) {
          hangState = HangState.kIsGoingToL1; 
      } else {
          hangState = HangState.kCustom;
      } 

      return hangState;
    }


    /**
     * Represents various states for the hang mechanisms. 
     * There are two states where the hang is not moving, and two for when 
     * the hang is moving towards one of the stationary states. There is 
     * also an extra state that is a default.
     * 
     * <p> {@code kIsGrounded} represents the hang being at the lowest level, nearly on the ground
     * <p> {@code kIsGrounding} represents the hang moving towards the above state
     * <p> {@code kIsL1} represents the hang moving towards the L1 position, readying for hanging
     * <p> {@code kIsGoingToL1} represents the hang mechanism moving towards the previous position
     * <p> {@code kCustom} represents guh
     */
    public static enum HangState {
      kIsGrounded,
      kIsGrounding,
      kIsL1,
      kIsGoingToL1,
  
      // special case for manual control
      kCustom
  }
  }  


