package frc.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import frc.robot.TestableSubsystem.TestResult;
import frc.robot.TestableSubsystem.TestableCommand;

public class TestRunner {

    private static TestRunner instance = null;
    private boolean hasLoggedTestsComplete = false;
    private List<TestableSubsystem> testSubsystems;
    private List<TestableCommand> runningCommands;

    private TestRunner() {
        testSubsystems = new ArrayList<>();
        runningCommands = new ArrayList<>();
        instance = this;
    }

    public static TestRunner getInstance() {
        if (instance == null) {
            instance = new TestRunner();
        }
        return instance;
    }

    public static void addTest(TestableSubsystem subsystem) {
        getInstance().testSubsystems.add(subsystem);
    }

    public static void runTests() {
        System.out.println("starting tests!");

        for (TestableSubsystem test : getInstance().testSubsystems) {
            TestableCommand command = test.getTestCommand();
            command.onInitialize();
            getInstance().runningCommands.add(command);
        }
    }

    public static boolean allTestsComplete() {
        return getInstance().runningCommands.isEmpty();
    }

    public static boolean isAnyTestRunning() {
        return !getInstance().runningCommands.isEmpty();

    }

    public static void periodic() {
        ArrayList<TestableCommand> commandsToClear = new ArrayList<>();
        for (TestableCommand cmd : getInstance().runningCommands) {
            cmd.onExecute();
            TestResult result = cmd.getCurrentResult();
            if (result != TestResult.IN_PROGRESS) {

                cmd.onFinished(result);
                commandsToClear.add(cmd);

                boolean shouldLog = false;
                byte logSelection = cmd.getLogSelection();

                switch (result) {
                    case SUCCESS:
                        shouldLog = (logSelection & TestableSubsystem.LOG_SUCCESS) != 0;
                    case KNOWN_FAILURE:
                        shouldLog = (logSelection & TestableSubsystem.LOG_KNOWN_FAILURE) != 0;
                    case UNKNOWN_FAILURE:
                        shouldLog = (logSelection & TestableSubsystem.LOG_UNKNOWN_FAILURE) != 0;
                    default:
                        break;
                }

                Optional<String> loggable = cmd.getLoggableResult(result);
                if (shouldLog && loggable.isPresent()) {
                    System.out.println(result.toString() + ": " + cmd.toString() + "; " + loggable.get());
                }
            }
        }

        if (!getInstance().hasLoggedTestsComplete && allTestsComplete()) {
            System.out.println("all tests complete!");
            getInstance().hasLoggedTestsComplete = true;
        }

        getInstance().runningCommands.removeAll(commandsToClear);
    }
}
// secret pinyata!! (guh from william) -noah