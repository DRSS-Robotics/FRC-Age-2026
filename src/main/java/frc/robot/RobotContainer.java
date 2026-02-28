package frc.robot;

import frc.robot.Constants.*;
import frc.robot.commands.DriveYawMotor;
import frc.robot.commands.RotateYawMotor;
import frc.robot.commands.DriveLaunchMotor;
import frc.robot.subsystems.shooter.ShooterSubsystem;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class RobotContainer {

  // TODO: actually initialize a SwerveDrivePoseEstimator
  // public SwerveDrivePoseEstimator m_poseEstimator = new
  // SwerveDrivePoseEstimator();
  public final Pose3d hubPose = new Pose3d(0, 0, 0, Rotation3d.kZero);
  private final ShooterSubsystem m_shooter = new ShooterSubsystem(17, 19, 2, NetworkTableInstance.getDefault().getTable("Turret"));

  private final CommandXboxController m_driverController = new CommandXboxController(
      OperatorConstants.kDriverControllerPort);
  //private final CommandXboxController m_operatorController = new CommandXboxController(1);

  private final SuperstructureSubsystem m_superstructure = new SuperstructureSubsystem(
      SuperstructureConstants.kIntakeMotorId,
      SuperstructureConstants.kStorageMotorId,
      SuperstructureConstants.kTransferMotorId,
      NetworkTableInstance.getDefault().getTable("Superstructure"));

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {

    m_driverController.rightTrigger(0.1).whileTrue(
        new DriveLaunchMotor(m_shooter,
            () -> DegreesPerSecond
                .of(ShooterConstants.kShooterMaxManualSpeedDPS
                    * powPreserveSign(m_driverController.getRightTriggerAxis(), 2.))));
    
        m_driverController.b().onTrue(new ToggleIntakeCommand(m_superstructure));
    m_driverController.a().onTrue(new ToggleWallCommand(m_superstructure));
    m_driverController.rightTrigger(0.1)
        .whileTrue(new DriveTransferCommand(m_superstructure, m_driverController::getRightTriggerAxis));
    m_driverController.leftTrigger(0.1)
        .whileTrue(new DriveIntakeCommand(m_superstructure, m_driverController::getLeftTriggerAxis));

   /* m_driverController.rightStick().whileFalse(
        new DriveYawMotor(m_shooter, () -> DegreesPerSecond.of(
            ShooterConstants.kTurretMaxManualSpeedDPS
                * powPreserveSign(-m_driverController.getRightX(), 2.))));

    m_driverController.rightStick().whileTrue(
        new RotateYawMotor(m_shooter, () -> Degrees
          .of(convertPositionToTurretAngle(
            m_driverController.getRightX(), m_driverController.getRightY()))));*/
  }

  // these should be moved to utils once we have utils class from superstrcuture !!
  private static double powPreserveSign(double a, double b) {
    return Math.pow(Math.abs(a), b) * Math.signum(a);
  }

  private static int signInclusive(double a) {
    return (a >= 0.0) ? 1 : -1;
    // new Trigger(m_exampleSubsystem::exampleCondition)
    // .onTrue(new ExampleCommand(m_exampleSubsystem));


  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return Commands.none();
  }

  // converts a joystick position into an angle that can be used by turret set
  // position commands (straight forward on the joytick is 180 deg)
  private static double convertPositionToTurretAngle(double x, double y) {
    return (180 / Math.PI) * Math.atan(
        y / x) + (90.0 * (signInclusive(x) + 2));
  }

}
