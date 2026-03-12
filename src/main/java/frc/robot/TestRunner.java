package frc.robot;
import java.util.ArrayList;
import java.util.HashMap;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.TestableSubsystem.TestCommandBase;


public class TestRunner {
    
    private static TestRunner instance = null;
    private boolean hasLoggedTestsComplete = false;

    private ArrayList<TestableSubsystem> tests;

    // all scheduled commands and whether they're finished or not
    private HashMap<Command, Boolean> runningCommands;

    private TestRunner() {
        tests = new ArrayList<>();
        runningCommands = new HashMap<>();
        instance = this;
    }

    public static TestRunner getInstance() {
        if (instance == null) {
            instance = new TestRunner();
        }
        return instance;
    }

    public static void addTest(TestableSubsystem subsystem) {
        getInstance().tests.add(subsystem);
    }

    public static void runTests() {
        System.out.println("starting tests!");

        CommandScheduler.getInstance().onCommandFinish(
            (Command c) -> {
                if (getInstance().runningCommands.containsKey(c)) {
                    getInstance().runningCommands.put(c, true);
                }
            }
        );

        for (TestableSubsystem test : getInstance().tests) {
            TestCommandBase command = test.getTestCommand();
            command.init(test);
            getInstance().runningCommands.put(command, false);
            
            CommandScheduler.getInstance().schedule(command);
        }
    }

    /**
     * iterates through the commands run through the test runner and checks which
     * ones are still running.
     * @return whether or not all tests are complete
     */
    public static boolean allTestsComplete() {
        for (Command tcb : getInstance().runningCommands.keySet()) {

            // if a command is still running, exit and return false
            if (!getInstance().runningCommands.get(tcb)) {
                return false;
            }
        }

        return true;
    }

    public static void periodic() {
        if (!getInstance().hasLoggedTestsComplete && allTestsComplete()) {
            System.out.println("all tests complete!");
            getInstance().hasLoggedTestsComplete = true;
        }
    }
}
// secret pinyata!! (guh from william) -noah