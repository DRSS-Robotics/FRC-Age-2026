package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface TestableSubsystem {

    public abstract TestableCommand getTestCommand();

    public static final byte LOG_NONE = 0b000;
    public static final byte LOG_ALL = 0b111;

    public static final byte LOG_SUCCESS = 0b001;
    public static final byte LOG_KNOWN_FAILURE = 0b010;
    public static final byte LOG_UNKNOWN_FAILURE = 0b100;

    public static enum TestResult {
        IN_PROGRESS,
        SUCCESS,
        KNOWN_FAILURE,
        UNKNOWN_FAILURE
    }

    /**
     * represents a unit of a full-subsystem test. use {@link TestCommandBase} for
     * an easier-to-use implementation of this
     */
    public interface TestableCommand {

        /**
         * @return the {@link TestResult} of this test. this is queried periodically,
         *         and should return something other than {@link TestResult#IN_PROGRESS}
         *         when the
         *         command should end. (the command will NOT end until this returns a
         *         non-{@link TestResult#IN_PROGRESS}
         *         value)
         */
        public abstract TestResult getCurrentResult();

        /**
         * @return whether or not you want to use the custom framework or not. if this
         *         is {@code false},
         *         the general expectation is that you will use something else (like the
         *         command scheduler).
         */
        public default boolean useCustomTestingFramework() {
            return true;
        }

        public default void onInitialize() {
        };

        public default void onExecute() {
        };

        public default void onFinished(TestResult testResult) {
        };

        /**
         * @return a byte, representing what exactly should be logged (use
         *         the {@link TestableSubsystem#LOG_ALL LOG_ALL},
         *         {@link TestableSubsystem#LOG_NONE
         *         LOG_NONE}, etc. constants)
         * 
         *         <p>
         *         if you're confused as to why this is a byte, the value this method
         *         returns
         *         will be bitmasked
         *         <p>
         *         (tl;dr since number types are just a bunch of bits, you can use them
         *         as handy storage
         *         for booleans/"flags" (which are just single bits). to combine log
         *         types, use the bitwise OR {@code |}
         *         (vertical line character) operation [ex.
         *         {@code LOG_KNOWN_FAILURE | LOG_UNKNOWN_FAILURE} to only
         *         report failures at the end of the test])
         */
        public default byte getLogSelection() {
            return LOG_ALL;
        };

        /**
         * returns a string, representing what happened in the test
         * (e.g. did it fail? what was the amount of PID error? etc.).
         * 
         * 
         * <p>
         * will NOT be logged if not overridden (only test
         * result enum will be logged).
         * 
         * <p>
         * if {@link #getCurrentResult()} returns {@link TestResult#KNOWN_FAILURE} but
         * this doesn't method provide a valid return, the TestResult will be treated as
         * an
         * {@link TestResult#UNKNOWN_FAILURE}
         */
        public default Optional<String> getLoggableResult(TestResult testResult) {
            return Optional.empty();
        };

    }

    public abstract class TestBase implements TestableCommand {

        private TestableSubsystem testable;
        private Optional<Subsystem> subsystem;

        public TestBase(TestableSubsystem testable) {
            this.testable = testable;
            if (testable instanceof Subsystem) {
                Subsystem testableAsSubsystem = (Subsystem) testable;
                this.subsystem = Optional.of(testableAsSubsystem);
            }
        }
    }

    public final class SequencedTest extends TestBase {

        private TestableCommand[] commandBuffer;
        private int bufferIndex = 0;
        private String outputBuffer;

        public SequencedTest(TestableSubsystem testable, TestableCommand... tests) {
            super(testable);
            commandBuffer = tests;
        }

        @Override
        public void onInitialize() {
            commandBuffer[bufferIndex].onInitialize();
        }

        @Override
        public void onExecute() {
            if (commandBuffer[bufferIndex].getCurrentResult() == TestResult.SUCCESS) {
                commandBuffer[bufferIndex++].onFinished(TestResult.SUCCESS);
                commandBuffer[bufferIndex].onInitialize();
            }
            commandBuffer[bufferIndex].onExecute();
        }

        @Override
        public TestResult getCurrentResult() {
            TestableCommand currentCommand = commandBuffer[bufferIndex];
            TestResult currentResult = currentCommand.getCurrentResult();
            
            if (currentResult != TestResult.IN_PROGRESS) {
                outputBuffer += currentCommand.getLoggableResult(currentResult);
            }
            if (currentResult == TestResult.KNOWN_FAILURE) {
                return TestResult.KNOWN_FAILURE;
            }
            if (currentResult == TestResult.UNKNOWN_FAILURE) {
                return TestResult.UNKNOWN_FAILURE;
            }

            return TestResult.IN_PROGRESS;
        }

        @Override
        public Optional<String> getLoggableResult(TestResult res) {
            return Optional.of(outputBuffer);
        }
    }

}
