// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoCommands;

import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.BumperDectectionSubsystem;
import frc.robot.commands.AutoCommands.BumperDetectionCommand;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.SuperstructureSubsystem;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.util.FileVersionException;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class pathfindingCommand extends Command{
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
   public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

   public double MaxVelocity = 3.0;

   //public boolean maybe = true;
   public final BumperDetectionCommand distanceFromLimelightToGoalInches;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public pathfindingCommand(CommandSwerveDrivetrain m_drivetrain) { 
    m_drivetrain = drivetrain;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_drivetrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    if (BumperDetectionCommand(distanceFromLimelightToGoalInches) > 0){
        //Object detected, so speed = 0
        MaxVelocity = 0;
    } else{
        //Object not detected, so speed = 3
        MaxVelocity = 3.0;
    }
    
    try {
       PathPlannerPath path = PathPlannerPath.fromPathFile("start");

       PathConstraints constraints = new PathConstraints(
        MaxVelocity, 4.1,
        Units.degreesToRadians(540), Units.degreesToRadians(720));

       Command pathfindingCommand = AutoBuilder.pathfindThenFollowPath(
        path,
        constraints);
      } catch (FileVersionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
