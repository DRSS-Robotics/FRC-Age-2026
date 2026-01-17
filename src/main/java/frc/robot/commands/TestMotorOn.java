package frc.robot.commands;

import frc.robot.subsystems.TestMotor;
import edu.wpi.first.wpilibj2.command.Command;

public class TestMotorOn extends Command{
    
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.


  private final TestMotor m_testMotor;


  /**
   * Powers the arm up, when finished passively holds the arm up.
   * 
   * We recommend that you use this to only move the arm into the hardstop
   * and let the passive portion hold the arm up.
   *
   * @param arm The subsystem used by this command.
   */
  public TestMotorOn(TestMotor testMotor) {
    m_testMotor = testMotor;
    addRequirements(m_testMotor);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_testMotor.run(0.5);
  }

  @Override
  public void end(boolean interrupted) {
    m_testMotor.run(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}


