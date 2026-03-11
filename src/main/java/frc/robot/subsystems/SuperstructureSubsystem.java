package frc.robot.subsystems;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.Topic;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.TestableSubsystem;
import frc.robot.Utils;
import frc.robot.Constants.SuperstructureConstants;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

import java.util.Optional;

import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

public class SuperstructureSubsystem extends SubsystemBase implements TestableSubsystem {

    private DoublePublisher intakeSpeedPublisher;
    private DoublePublisher soupSpeedPublisher;
    private DoublePublisher transferSpeedPublisher;
    private DoublePublisher storagePositionPublisher;
    private BooleanPublisher storageIsOpenPublisher;

    private TalonFX m_intakeMotor;
    private SlotConfigs intakeMotorConfigs;
    private VelocityVoltage intakeMotorRequest;
    private AngularVelocity intakeMotorSetSpeed = DegreesPerSecond.of(0);

    private final TrapezoidProfile intakeTrapezoidProfile = new TrapezoidProfile(
            new TrapezoidProfile.Constraints(SuperstructureConstants.kMaxIntakeDPS,
                    SuperstructureConstants.kMaxIntakeDPSPS));

    private final TrapezoidProfile storageWallTrapezoidProfile = new TrapezoidProfile(
            new TrapezoidProfile.Constraints(SuperstructureConstants.kMaxSoupDPS,
                    SuperstructureConstants.kMaxSoupDPSPS));

    private TrapezoidProfile.State intakeVelocityGoal = new TrapezoidProfile.State();
    private TrapezoidProfile.State intakeVelocitySetpoint = new TrapezoidProfile.State();

    private TrapezoidProfile.State storageWallPositionGoal = new TrapezoidProfile.State();
    private TrapezoidProfile.State storageWallTrapezoidSetpoint = new TrapezoidProfile.State();

    private TalonFX m_storageMotor;
    private StorageWallState storageState;
    private SlotConfigs storageMotorConfigs;
    private PositionVoltage storageMotorRequest;
    private Angle storageWallSetpoint = Degrees.of(0);

    private TalonFX m_soupMotor;
    private SlotConfigs soupMotorConfigs;
    private VelocityVoltage soupMotorRequest;
    private AngularVelocity soupMotorSetSpeed = DegreesPerSecond.of(0);

    private final TrapezoidProfile soupTrapezoidProfile = new TrapezoidProfile(
            new TrapezoidProfile.Constraints(SuperstructureConstants.kMaxIntakeDPS,
                    SuperstructureConstants.kMaxIntakeDPSPS));

    private TrapezoidProfile.State soupVelocityGoal = new TrapezoidProfile.State();
    private TrapezoidProfile.State soupVelocitySetpoint = new TrapezoidProfile.State();

    private final TrapezoidProfile transferTrapezoidProfile = new TrapezoidProfile(
            new TrapezoidProfile.Constraints(SuperstructureConstants.kMaxTransferDPS,
                    SuperstructureConstants.kMaxTransferDPSPS));

    private TrapezoidProfile.State transferVelocityGoal = new TrapezoidProfile.State();
    private TrapezoidProfile.State transferVelocitySetpoint = new TrapezoidProfile.State();

    private TalonFX m_transferMotor;
    private SlotConfigs transferMotorConfigs;
    private VelocityVoltage transferMotorRequest;
    private AngularVelocity transferMotorSetSpeed = DegreesPerSecond.of(0);

    public static Angle storageClosedAngle = Degrees.of(SuperstructureConstants.kStorageClosedRotations);
    public static Angle storageOpenAngle = Degrees.of(SuperstructureConstants.kStorageOpenRotations);
    public static Angle storageAngleTolerance = Degrees.of(SuperstructureConstants.kStorageStateTolerance);

    public final double kDT = 0.02;

    /**
     * Subsystem that encompasses both the over-the-bumper intake as well as Fuel
     * storage.
     */
    public SuperstructureSubsystem(int intakeMotorId, int wallMotorId, int soupMotorId, int transferMotorId,
            NetworkTable table) {
        m_intakeMotor = new TalonFX(intakeMotorId);
        m_storageMotor = new TalonFX(wallMotorId);
        m_soupMotor = new TalonFX(soupMotorId);
        m_transferMotor = new TalonFX(transferMotorId);

        // TODO: use actual PID values instead of placeholder
        intakeMotorConfigs = Utils.configureTalonGains(m_intakeMotor, 0, 1.5, 0.05, 0, 0);
        intakeMotorRequest = new VelocityVoltage(0).withSlot(0);

        storageMotorConfigs = Utils.configureTalonGains(m_storageMotor, 0.5, 0.0, 0.6, 0, 0);
        storageMotorRequest = new PositionVoltage(0).withSlot(0);

        soupMotorConfigs = Utils.configureTalonGains(m_soupMotor, 0.425, 0.105, 0.03, 0, 0);
        soupMotorRequest = new VelocityVoltage(0).withSlot(0);

        transferMotorConfigs = Utils.configureTalonGains(m_soupMotor, 0.05, 0.65, 0.03, 0, 0);
        transferMotorRequest = new VelocityVoltage(0).withSlot(0);

        intakeSpeedPublisher = table.getDoubleTopic("intakeSpeed").publish();
        soupSpeedPublisher = table.getDoubleTopic("soupSpeed").publish();
        transferSpeedPublisher = table.getDoubleTopic("transferSpeed").publish();
        storagePositionPublisher = table.getDoubleTopic("storagePosition").publish();
        storageIsOpenPublisher = table.getBooleanTopic("storageIsOpen").publish();

    }

    @Override
    public void periodic() {
        intakeSpeedPublisher.set(getIntakeSpeed().in(DegreesPerSecond));
        soupSpeedPublisher.set(getSoupSpeed().in(DegreesPerSecond));
        storagePositionPublisher.set(getStoragePosition().in(Degrees));
        storageIsOpenPublisher.set(getStorageState() == StorageWallState.kIsOpen);

        transferVelocitySetpoint = transferTrapezoidProfile.calculate(kDT, transferVelocitySetpoint,
                transferVelocityGoal);



        m_transferMotor.setControl(
                transferMotorRequest.withVelocity(
                        DegreesPerSecond.of(transferVelocitySetpoint.position)));



        intakeVelocitySetpoint = intakeTrapezoidProfile.calculate(kDT, intakeVelocitySetpoint,
                intakeVelocityGoal);

        m_intakeMotor.setControl(
                intakeMotorRequest.withVelocity(
                        DegreesPerSecond.of(intakeVelocitySetpoint.position)));



        soupVelocitySetpoint = soupTrapezoidProfile.calculate(kDT, soupVelocitySetpoint,
                soupVelocityGoal);

        m_soupMotor.setControl(
                soupMotorRequest.withVelocity(
                        DegreesPerSecond.of(soupVelocitySetpoint.position)));



        storageWallTrapezoidSetpoint = storageWallTrapezoidProfile.calculate(kDT, storageWallTrapezoidSetpoint,
                storageWallPositionGoal);

        m_storageMotor.setControl(storageMotorRequest.withPosition(Degrees.of(storageWallTrapezoidSetpoint.position)));

    }

    /**
     * @param speed in degrees per second
     */
    public void runIntake(double speed) {
        runIntake(DegreesPerSecond.of(speed));
    }

    public void runIntake(AngularVelocity speed) {
        intakeMotorSetSpeed = speed;
        intakeVelocityGoal = new TrapezoidProfile.State(speed.in(DegreesPerSecond), 0);

    }

    public AngularVelocity getIntakeSpeedSetpoint() {
        return intakeMotorSetSpeed;
    }

    public AngularVelocity getIntakeSpeed() {
        return m_intakeMotor.getVelocity(true).getValue();
    }

    public void runSoupMotor(double speed) {
        runSoupMotor(DegreesPerSecond.of(speed));
    }

    public void runSoupMotor(AngularVelocity speed) {
        soupMotorSetSpeed = speed;
        soupVelocityGoal = new TrapezoidProfile.State(speed.in(DegreesPerSecond), 0);
    }

    public AngularVelocity getSoupSpeed() {
        return m_soupMotor.getVelocity(true).getValue();
    }

    public void runTransferMotor(double speed) {
        runTransferMotor(DegreesPerSecond.of(speed));
    }

    public void runTransferMotor(AngularVelocity speed) {
        transferMotorSetSpeed = speed;
        transferVelocityGoal = new TrapezoidProfile.State(speed.in(DegreesPerSecond), 0);

    }

    public AngularVelocity getTransferSpeed() {
        return m_transferMotor.getVelocity(true).getValue();
    }

    /**
     * Sets the PID setpoint (in DEGREES) of the wall motor. Values exceeding the
     * bounds in {@link SuperstructureConstants} will automatically be ignored
     * 
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
     * 
     * @param newPosition rotational setpoint value.
     */
    public void setWallMotorPosition(Angle newPosition) {
        System.out.println(newPosition);
        System.out.println(storageOpenAngle);
        if (newPosition.lt(storageClosedAngle) ||
                newPosition.gt(storageOpenAngle)) {
            System.out.println(
                    "Superstructure storage wall setpoint is outside bounds, ignoring request");
            // return;
        }

        storageWallSetpoint = newPosition;
        storageWallPositionGoal = new TrapezoidProfile.State(newPosition.in(Degrees), 0);
    }

    public Angle getStoragePosition() {
        return m_storageMotor.getPosition(true).getValue();
    }

    /**
     * @return a {@link StorageWallState} enum representing the storage wall's state
     */
    public StorageWallState getStorageState() {
        Angle storageWallCurrentPosition = getStoragePosition();

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
     * @return a {@link StorageWallState} enum representing the storage wall's state
     */
    public static StorageWallState getStorageState(Angle storageWallCurrentPosition, Angle storageWallSetpoint) {

        StorageWallState storageState;

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
     * that the wall has reached the
     * {@link SuperstructureConstants#kStorageClosedRotations
     * closed position} and is currently idling. {@code kIsClosing} is similar, with
     * the
     * distinction that it instead means the wall is currently on the way to that
     * setpoint.
     * 
     * <p>
     * The {@code kIsOpen} and {@code kIsOpening} states represent the same, in
     * reverse
     * (using the {@link SuperstructureConstants#kStorageOpenRotations open
     * position})
     * 
     * <p>
     * Possible values: {@link #kIsClosed}, {@link #kIsClosing}, {@link #kIsOpen},
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

    @Override
    public TestableCommand getTestCommand() {
        return new SequencedTest(this,

                new TestBase(this) {
                    // here begins the wall test
                    private double startTime;
                    private Angle maxAllowedError;
                    private String output;

                    @Override
                    public void onInitialize() {
                        startTime = Timer.getFPGATimestamp();
                        maxAllowedError = SuperstructureConstants.kTestWallTargetAngle
                                .times(SuperstructureConstants.kMaxTestWallErrorPercentage / 100);
                    }

                    @Override
                    public Optional<String> getLoggableResult(TestResult result) {
                        return Optional.of(output);
                    }

                    @Override
                    public TestResult getCurrentResult() {
                        Angle currentWallAngle = m_storageMotor.getPosition().getValue();
                        if (Timer.getFPGATimestamp()
                                - startTime >= SuperstructureConstants.kMaxTestWallTimeToReachHeight) {
                            output = "The wall took too long to reach the desired position. ";
                            return TestResult.KNOWN_FAILURE;
                        }
                        if (currentWallAngle.isNear(SuperstructureConstants.kTestWallTargetAngle, maxAllowedError)) {
                            return TestResult.SUCCESS;
                        }
                        return TestResult.IN_PROGRESS;

                    }
                },

                // transfer test
                new TestBase(this) {

                    private double startTime;
                    private double maxAllowedError;
                    private boolean transferMotorSpeedReached;
                    private double timeAtStartOfVelocityTest;
                    private String output = "";
                    private AngularVelocity targetSoupSpeed = DegreesPerSecond
                            .of(SuperstructureConstants.kTestSoupTargetDPS);

                    @Override
                    public void onInitialize() {
                        runSoupMotor(targetSoupSpeed);
                        startTime = Timer.getFPGATimestamp();
                        maxAllowedError = SuperstructureConstants.kMaxTestSoupSpeedErrorPercentage / 100;
                    }

                    @Override
                    public Optional<String> getLoggableResult(TestResult result) {
                        return Optional.of(output);
                    }

                    @Override
                    public TestResult getCurrentResult() {
                        if (!transferMotorSpeedReached) {
                            if (Timer.getFPGATimestamp()
                                    - startTime >= SuperstructureConstants.kMaxTestSoupTimeToSpinUp) {
                                output = "The transfer took too long to spin up.";
                                return TestResult.KNOWN_FAILURE;
                            }

                            timeAtStartOfVelocityTest = Timer.getFPGATimestamp();
                            transferMotorSpeedReached = getIntakeSpeed().isNear(soupMotorSetSpeed, maxAllowedError);
                        } else {
                            if (!getIntakeSpeed().isNear(soupMotorSetSpeed, maxAllowedError)) {
                                output = "Soup did not maintain speed.";
                                return TestResult.KNOWN_FAILURE;
                            }
                            if (Timer.getFPGATimestamp()
                                    - timeAtStartOfVelocityTest >= SuperstructureConstants.kMinTestSoupTimeToMaintainSpeed) {

                                return TestResult.SUCCESS;
                            }
                        }

                        return TestResult.IN_PROGRESS;
                    }
                },

                // intake test
                new TestBase(this) {

                    private boolean intakeMotorSpeedReached;
                    private double startTime;
                    private double timeAtStartOfVelocityTest;
                    private String output = "";
                    private double maxAllowedError;
                    private AngularVelocity targetSpeed = DegreesPerSecond
                            .of(SuperstructureConstants.kTestIntakeTargetDPS);

                    @Override
                    public void onInitialize() {
                        runIntake(targetSpeed);
                        startTime = Timer.getFPGATimestamp();
                        maxAllowedError = SuperstructureConstants.kMaxTestIntakeSpeedErrorPercentage / 100;

                    }

                    @Override
                    public Optional<String> getLoggableResult(TestResult result) {
                        return Optional.of(output);
                    }

                    @Override
                    public TestResult getCurrentResult() {

                        if (!intakeMotorSpeedReached) {
                            if (Timer.getFPGATimestamp()
                                    - startTime >= SuperstructureConstants.kMaxTestIntakeTimeToSpinUp) {
                                output = "Intake took too long to spin up";
                                return TestResult.KNOWN_FAILURE;
                            }

                            timeAtStartOfVelocityTest = Timer.getFPGATimestamp();
                            intakeMotorSpeedReached = getIntakeSpeed().isNear(targetSpeed, maxAllowedError);
                        } else {
                            if (!getIntakeSpeed().isNear(targetSpeed, maxAllowedError)) {
                                output = "Intake did not maintain speed";
                                return TestResult.KNOWN_FAILURE;
                            }
                            if (Timer.getFPGATimestamp()
                                    - timeAtStartOfVelocityTest >= SuperstructureConstants.kMinTestIntakeTimeToMaintainSpeed) {

                                return TestResult.SUCCESS;
                            }
                        }

                        return TestResult.IN_PROGRESS;
                    }

                });
    }

}