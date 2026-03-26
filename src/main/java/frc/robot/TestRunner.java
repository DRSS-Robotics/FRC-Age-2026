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
    private TestableCommand runningCommand;
    private List<TestableCommand> commandQueue;

    private TestRunner() {
        testSubsystems = new ArrayList<>();
        runningCommand = null;
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

        for (TestableSubsystem cmd : getInstance().testSubsystems) {
            getInstance().commandQueue.add(cmd.getTestCommand());
        }
        
    }

    public static boolean allTestsComplete() {
        return getInstance().commandQueue.isEmpty();
    }

    public static boolean isAnyTestRunning() {
        return getInstance().runningCommand != null;

    }

    public static void periodic() {

        if (getInstance().runningCommand == null) {
            getInstance().runningCommand = getInstance().commandQueue.remove(0);
            getInstance().runningCommand.onInitialize();
        }

        TestableCommand cmd = getInstance().runningCommand;

        cmd.onExecute();
        TestResult result = cmd.getCurrentResult();
        if (result != TestResult.IN_PROGRESS) {

            cmd.onFinished(result);

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

        if (!getInstance().hasLoggedTestsComplete && allTestsComplete()) {
            System.out.println("all tests complete!");
            getInstance().hasLoggedTestsComplete = true;
        }

        getInstance().runningCommand = null;
    }

}
// secret pinyata!! (guh from william) -noah