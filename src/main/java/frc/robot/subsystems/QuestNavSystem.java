// package frc.robot.subsystems;

// import edu.wpi.first.apriltag.AprilTagFieldLayout;
// import edu.wpi.first.apriltag.AprilTagFields;
// import edu.wpi.first.math.Matrix;
// import edu.wpi.first.math.VecBuilder;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Pose3d;
// import edu.wpi.first.math.geometry.Rotation3d;
// import edu.wpi.first.math.geometry.Transform3d;
// import edu.wpi.first.math.geometry.Translation3d;
// import edu.wpi.first.math.numbers.N1;
// import edu.wpi.first.math.numbers.N3;
// import edu.wpi.first.networktables.NetworkTableInstance;
// import edu.wpi.first.networktables.StructArrayPublisher;
// import edu.wpi.first.networktables.StructPublisher;
// import edu.wpi.first.wpilibj.DriverStation;
// import edu.wpi.first.wpilibj.Timer;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import gg.questnav.questnav.PoseFrame;
// import gg.questnav.questnav.QuestNav;
// import java.util.ArrayList;
// import java.util.List;

// public class QuestNavSystem extends SubsystemBase {
//   private final QuestNav questNav = new QuestNav();
//   private final CommandSwerveDrivetrain m_drivetrain;

//   private static final Transform3d questOffset =
//       new Transform3d(
//           new Translation3d(0.0, 0.0, 0.5),
//           new Rotation3d(0.0, 0.0, 0.0));

//   private static final Matrix<N3, N1> QUESTNAV_STD_DEVS =
//       VecBuilder.fill(
//           0.02,       // X position trust (20mm)
//           0.02,       // Y position trust (20mm)
//           0.0872665); // Rotation trust (5 degrees)

//   private static final AprilTagFieldLayout FIELD_LAYOUT =
//       AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

//   private static final double BATTERY_LOW_PERCENT = 20;
//   private static final double BATTERY_CRITICAL_PERCENT = 10;

//   // NT struct publishers for AdvantageScope 3D visualization
//   private final StructArrayPublisher<Pose3d> m_allPosesPub;
//   private final StructArrayPublisher<Pose3d> m_acceptedPosesPub;
//   private final StructArrayPublisher<Pose3d> m_rejectedPosesPub;
//   private final StructPublisher<Pose3d> m_latestPosePub;

//   private double m_lastPoseTimestamp = -1;

//   public QuestNavSystem(CommandSwerveDrivetrain drivetrain) {
//     m_drivetrain = drivetrain;

//     var nt = NetworkTableInstance.getDefault();
//     m_allPosesPub =
//         nt.getStructArrayTopic("QuestNav/RobotPoses", Pose3d.struct).publish();
//     m_acceptedPosesPub =
//         nt.getStructArrayTopic("QuestNav/RobotPosesAccepted", Pose3d.struct).publish();
//     m_rejectedPosesPub =
//         nt.getStructArrayTopic("QuestNav/RobotPosesRejected", Pose3d.struct).publish();
//     m_latestPosePub =
//         nt.getStructTopic("QuestNav/LatestRobotPose", Pose3d.struct).publish();

//     questNav.setVersionCheckEnabled(false);

//     questNav.onConnected(() ->
//         System.out.println("Quest connected!"));
//     questNav.onDisconnected(() ->
//         DriverStation.reportWarning("Quest disconnected!", false));
//     questNav.onTrackingAcquired(() ->
//         System.out.println("Quest tracking acquired!"));
//     questNav.onTrackingLost(() ->
//         DriverStation.reportWarning("Quest tracking lost!", false));
//     questNav.onLowBattery((int) BATTERY_LOW_PERCENT, level ->
//         DriverStation.reportWarning("Quest battery low: " + level + "%", false));
//     questNav.onCommandSuccess(response ->
//         System.out.println("Pose reset succeeded: " + response.getCommandId()));
//     questNav.onCommandFailure(response ->
//         DriverStation.reportError(
//             "Pose reset failed: " + response.getErrorMessage(), false));
//   }

//   public void periodic() {
//     questNav.commandPeriodic();

//     // Publish device diagnostics
//     boolean connected = questNav.isConnected();
//     boolean tracking = questNav.isTracking();
//     SmartDashboard.putBoolean("QuestNav/Connected", connected);
//     SmartDashboard.putBoolean("QuestNav/Tracking", tracking);
//     SmartDashboard.putNumber("QuestNav/Latency", questNav.getLatency());
//     questNav.getBatteryPercent().ifPresent(b -> {
//       SmartDashboard.putNumber("QuestNav/Battery%", b);
//       if (b < BATTERY_CRITICAL_PERCENT) {
//         DriverStation.reportWarning("Quest battery CRITICAL: " + b + "%", false);
//       }
//     });
//     questNav.getTrackingLostCounter().ifPresent(
//         c -> SmartDashboard.putNumber("QuestNav/TrackingLostCount", c));

//     // Process all unread pose frames
//     PoseFrame[] frames = questNav.getAllUnreadPoseFrames();
//     SmartDashboard.putNumber("QuestNav/UnreadFrames", frames.length);

//     List<Pose3d> allPoses = new ArrayList<>();
//     List<Pose3d> acceptedPoses = new ArrayList<>();
//     List<Pose3d> rejectedPoses = new ArrayList<>();

//     for (PoseFrame frame : frames) {
//       Pose3d questPose = frame.questPose3d();
//       Pose3d robotPose = questPose.plus(ROBOT_TO_QUEST.inverse());

//       allPoses.add(robotPose);

//       m_drivetrain.setQuestNavPose(robotPose.toPose2d());

//       if (shouldReject(robotPose)) {
//         rejectedPoses.add(robotPose);
//         continue;
//       }

//       acceptedPoses.add(robotPose);

//       if (frame.isTracking()) {
//         m_drivetrain.addVisionMeasurement(
//             robotPose.toPose2d(), frame.dataTimestamp(), QUESTNAV_STD_DEVS);
//       }

//       m_lastPoseTimestamp = frame.dataTimestamp();
//     }

//     // Publish pose arrays for AdvantageScope 3D field visualization
//     m_allPosesPub.set(allPoses.toArray(Pose3d[]::new));
//     m_acceptedPosesPub.set(acceptedPoses.toArray(Pose3d[]::new));
//     m_rejectedPosesPub.set(rejectedPoses.toArray(Pose3d[]::new));

//     if (!allPoses.isEmpty()) {
//       m_latestPosePub.set(allPoses.get(allPoses.size() - 1));
//     }

//     if (m_lastPoseTimestamp > 0) {
//       SmartDashboard.putNumber(
//           "QuestNav/TimeSinceLastPose", Timer.getTimestamp() - m_lastPoseTimestamp);
//     }
//   }

//   private boolean shouldReject(Pose3d pose) {
//     return pose.getX() < 0.0
//         || pose.getX() > FIELD_LAYOUT.getFieldLength()
//         || pose.getY() < 0.0
//         || pose.getY() > FIELD_LAYOUT.getFieldWidth();
//   }

//   public void resetPose(Pose3d robotPose) {
//     Pose3d questPose = robotPose.plus(ROBOT_TO_QUEST);
//     questNav.setPose(questPose);
//   }

//   public void resetPose(Pose2d robotPose) {
//     resetPose(new Pose3d(robotPose));
//   }
// }