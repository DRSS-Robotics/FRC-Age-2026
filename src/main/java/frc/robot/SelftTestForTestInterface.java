package frc.robot;

import java.util.Optional;

public class SelftTestForTestInterface implements TestableSubsystem {

    private double guh;
    private double guhTheSecond;
    private boolean guhBoolean;

    @Override
    public TestableCommand getTestCommand() {
        return new SequencedTest(this,

                new TestBase(this) {
                    private String output = "";


                    @Override
                    public Optional<String> getLoggableResult(TestResult testResult) {
                        return Optional.of(output);
                    };

                    @Override
                    public void onInitialize() {
                        guh = 2;
                        guhTheSecond = 6;
                    }

                    @Override
                    public TestResult getCurrentResult() {
                        if (guhTheSecond - guh == 12) {
                            output += "6-2 is not 12. ";
                            return TestResult.KNOWN_FAILURE;
                        } else if (guhTheSecond * guh == 12) {
                            output += "6*2 does equal 12. ";
                            return TestResult.SUCCESS;
                        } else {
                            output += "Unknown failure... ";
                            return TestResult.UNKNOWN_FAILURE;
                        }

                    }
                },
                new TestBase(this) {
                    private String output = "";


                    @Override
                    public Optional<String> getLoggableResult(TestResult testResult) {
                        return Optional.of(output);
                    };

                    @Override
                    public void onInitialize() {
                        guhBoolean = false;
                    }

                    @Override
                    public TestResult getCurrentResult() {
                        if (guhBoolean) {
                            output += " True is not false. ";
                            return TestResult.KNOWN_FAILURE;
                        } else {
                            output += " True is true. ";
                            return TestResult.SUCCESS;
                        }
                    }

                    @Override
                    public String toString() {
                        return "guh";
                    }


                }

        );

    }

}