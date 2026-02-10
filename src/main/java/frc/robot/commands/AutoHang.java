// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands;

// import frc.robot.subsystems.HangSubsystem;
// import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.Constants.HangConstants;

// public class AutoHang extends Command {
//   @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
//   private final HangSubsystem m_hang;
//   public int hangTag;
//   public boolean canItHang = false;
//   public boolean isItAuto;
//   public int alignmentThreshold = 10;
//   public int idealHangPositionLeft;
//   public int idealHangPositionRight;
//   public double distanceNeededToHangX;
//   public double distanceNeededToHangY;

//   private int hangTagNumbers;
//   private final int iHP;

//   //private final VisionSubsystem m_vision;

//   /**
//    * Creates a new ExampleCommand.
//    *
//    * @param subsystem The subsystem used by this command.
//    */
//   public AutoHang(HangSubsystem hangSubsystem, int idealHangPosition, boolean isItAuto) { 
//     m_hang = hangSubsystem;
//     iHP = idealHangPosition;
//     //It doesn't exist in this branch, but add the vison subsystem too
//     // Use addRequirements() here to declare subsystem dependencies.
//     addRequirements(hangSubsystem);
//   }


//   // Called when the command is initially scheduled.
//   @Override
//   public void initialize() {
//   }

//   // Called every time the scheduler runs while the command is scheduled.
//   @Override
//   public void execute() {
//     //Detect april tag
//     //hangTag = getAprilTag(hangTagNumbers); //could potentially use the .nearest with this as well, Make sure it's closest and the hanging tag

//    //We gotta use the pose estimate from the vision subsystem
//     //double distanceX = m_vision.updatePoseEstimate();



//     double distanceX = 12; //Change these to the actual way I am getting distance
//     double distanceY = 12;


//     //I think for april tag we need pose stuff
//     //double distanceX = m_vision.updatePoseEstimate();

//     //Calculate distance to ideal hang
//     if (iHP == idealHangPositionLeft){
//       distanceNeededToHangX = HangConstants.hangLeftX - distanceX;
//       distanceNeededToHangY = HangConstants.hangLeftY - distanceY;
//     }
//     else if(iHP == idealHangPositionRight){
//         distanceNeededToHangX = HangConstants.hangRightX - distanceX;
//         distanceNeededToHangY = HangConstants.hangRightY - distanceY;
//       }
    
//       double robotMovingThisMuchX = distanceNeededToHangX - distanceX;
//       double robotMovingThisMuchY = distanceNeededToHangY - distanceY;
//     while(robotMovingThisMuchX >= 0 && robotMovingThisMuchX <= alignmentThreshold && robotMovingThisMuchY >= 0 && robotMovingThisMuchY <= alignmentThreshold && isItAuto != true){
//       System.out.println("Move X by: " + robotMovingThisMuchX);
//       System.out.println("Move Y by: " + robotMovingThisMuchY);
//       //SmartDashboard.putBoolean("Move X by:", robotMovingThisMuchX);
//       //SmartDashboard.putBoolean("Move Y by:", robotMovingThisMuchY);
//       if(robotMovingThisMuchX != 0 && robotMovingThisMuchY  != 0) { 
//         //Give it some leeway on the position maybe, depends on how accurate we can be, maybe like 0.3 meters
//         System.out.println("You are bad to hang D:");
//         //Same thing as todo
//       }
//       else if(robotMovingThisMuchX >= 0 && robotMovingThisMuchX <= alignmentThreshold && robotMovingThisMuchY == 0 && robotMovingThisMuchY <= alignmentThreshold){
//         //some sort of stop moving?
//         canItHang = true;
//         System.out.println("You are good to hang :D");
//         //SmartDashboard.putBoolean("Hang?", canitHang); I think this works
//         break;
//       }
//     }


//     //Autonomous Hang v
//     if(isItAuto){
//       while(robotMovingThisMuchX <= 0 && robotMovingThisMuchX >= alignmentThreshold && robotMovingThisMuchY >= 0 && robotMovingThisMuchY <= alignmentThreshold && isItAuto){
//         if(robotMovingThisMuchX != 0 && robotMovingThisMuchY != 0) { //TODO add threshold?
//           //Auto said that the movement is covered by path planner
//           //Potentially I could make a path
//           //Though I think I will see if there is some sort of align april tag command that I could offset to be correct
//           //Pose estimator will be a good place to start, perhaps

//           System.out.println("You are bad to hang D:");
//         }
//         else if(robotMovingThisMuchX >= 0 && robotMovingThisMuchX <= alignmentThreshold && robotMovingThisMuchY == 0 && robotMovingThisMuchY <= alignmentThreshold && isItAuto){
//           //stopMoving(please);
//           canItHang = true;
//           System.out.println("You are good to hang :D");
//           //SmartDashboard.putBoolean("Hang?", canitHang);
//           break;
//         }
//       }
//     }
//   }

//   // Called once the command ends or is interrupted.
//   @Override
//   public void end(boolean interrupted) {
//     // guh nathan was here
//     m_hang.setHangMotorSpeed(0);
//   }

//   // Returns true when the command should end.
//   @Override
//   public boolean isFinished() {
//     return true;
//   }
// }


// //TODO: Research how to use path planner for the movement, check in with teleop about other thing?
// //TODO: Stop moving code

// //TODO: Correct April tag stuff, drawing on camera feed with Luka?

// //TODO: Recheck the logic, walk through it
// //TODO: Add comments explaining code