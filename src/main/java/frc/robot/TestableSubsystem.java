package frc.robot;

import java.util.Optional;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface TestableSubsystem {

    public TestCommandBase getTestCommand();

    public static final byte LOG_NONE            = 0b000;
    public static final byte LOG_ALL             = 0b111;

    public static final byte LOG_SUCCESS         = 0b001;
    public static final byte LOG_KNOWN_FAILURE   = 0b010;
    public static final byte LOG_UNKNOWN_FAILURE = 0b100;

    public static enum TestResult {
        IN_PROGRESS,
        SUCCESS,
        KNOWN_FAILURE,
        UNKNOWN_FAILURE
    }

    public abstract class TestCommandBase extends Command {

        private TestableSubsystem testable;
        private Optional<Subsystem> subsystem;

        public final boolean isSubsystemValid() {
            return subsystem.isPresent();
            
        }



        /**
         * runs inital setup stuff, not supposed to be overridden or really called outside
         * of {@link TestRunner}. if the class implementing {@link TestableSubsystem}
         * also implements WPILib's {@link Subsystem} interface, add it as a requirement
         * to the command
         * @param testable
         */
        protected final void init(TestableSubsystem testable) {
            this.testable = testable;
            if (Subsystem.class.isAssignableFrom(testable.getClass())) {
                Subsystem testableAsSubsystem = (Subsystem) testable;
                addRequirements(testableAsSubsystem);
                this.subsystem = Optional.of(testableAsSubsystem);
            }
        }



        /**
         * @return the {@link TestResult} of this test. this is queried periodically,
         * and should return something other than {@link TestResult#IN_PROGRESS} when the 
         * command should end. (the command will NOT end until this returns a non-{@link TestResult#IN_PROGRESS}
         * value)
         */
        public abstract TestResult getCurrentResult();



        /**
         * returns a string, representing what happened in the test
         * (e.g. did it fail? what was the amount of PID error? etc.).
         * 
         * this is read at the end of the test, and will be printed along with
         * the test's result.
         * 
         * <p> will NOT be logged if not overridden (only test 
         * result enum will be logged).
         * 
         * <p> if {@link #getCurrentResult()} returns {@link TestResult#KNOWN_FAILURE} but
         * this doesn't method provide a valid return, the TestResult will be treated as an 
         * {@link TestResult#UNKNOWN_FAILURE}
         */
        public Optional<String> getLoggableResult(TestResult testResult) {
            return Optional.empty();
        };



        /**
         * @return a byte, representing what exactly should be logged (use 
         * the {@link TestableSubsystem#LOG_ALL LOG_ALL}, {@link TestableSubsystem#LOG_NONE 
         * LOG_NONE}, etc. constants)
         * 
         * <p> if you're confused as to why this is a byte, the value this method returns
         * will be bitmasked 
         * <p> (tl;dr since number types are just a bunch of bits, you can use them as handy storage 
         * for booleans/"flags" (which are just single bits). to combine log types, use the bitwise OR {@code |}
         * (vertical line character) operation [ex. {@code LOG_KNOWN_FAILURE | LOG_UNKNOWN_FAILURE} to only 
         * report failures at the end of the test])
         */
        public byte getLogSelection() {
            return LOG_ALL;
        };



        @Override
        public final boolean isFinished() {
            TestResult testResult = getCurrentResult();
            
            if (testResult != TestResult.IN_PROGRESS) {
                boolean verboseLog = getLoggableResult(testResult).isPresent();
                String output = "";

                String name = testable.getClass().getSimpleName();
                if (name == "") {
                    name = "Unknown Testable";
                }

            

                switch (testResult) {
                    case SUCCESS:
                        output += "SUCCESS: " + name + (verboseLog ? ", result: \"" : "");
                        break;

                    case KNOWN_FAILURE:
                        if (!verboseLog) {
                            testResult = TestResult.UNKNOWN_FAILURE;
                        } else {
                            output += "KNOWN FAILURE: " + name + (verboseLog ? ", result: \"" : "");
                            break;
                        }

                    case UNKNOWN_FAILURE:
                        output += "UNKNOWN FAILURE: " + name + "...";
                        break;

                    default:
                        break;
                }

                
                if (verboseLog) {
                    Optional<String> result = getLoggableResult(testResult);
                    output += result.get() + "\"";
                }

                System.out.println(output);
                return true;       
            }

            return false;
        }
    }
}
