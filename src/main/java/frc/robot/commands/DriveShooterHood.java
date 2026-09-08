package frc.robot.commands;

import frc.robot.Constants.SuperstructureConstants;
import frc.robot.subsystems.SuperstructureSubsystem;
import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.function.Supplier;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants;


/** An example command that uses an example subsystem. */
public class DriveShooterHood extends Command {

  private ShooterSubsystem m_hoodMotor;
  private double speed;

  public DriveShooterHood(ShooterSubsystem hood, double speed) {
    m_hoodMotor = hood;
    this.speed = speed;
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    //m_hoodMotor.runHoodMotors(speed.get());
    //System.out.println(speed.get());
    SmartDashboard.putNumber("Hood Positon", m_hoodMotor.getHoodEncoderPosition());

    if (m_hoodMotor.getHoodEncoderPosition() < 0.9){
    m_hoodMotor.runHoodMotors(
        DegreesPerSecond.of(480));
        System.out.println(m_hoodMotor.getHoodEncoderPosition());
    } else{
        // m_hoodMotor.runHoodMotors(DegreesPerSecond.of(0));
        m_hoodMotor.runHoodMotors(
        DegreesPerSecond.of(480));
        System.out.println(m_hoodMotor.getHoodEncoderPosition());
    }
  }

  @Override
  public void end(boolean interrupted) {
    m_hoodMotor.runHoodMotors(
        DegreesPerSecond.of(0));
        
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
