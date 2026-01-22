package frc.robot.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;

/**
 * Represents a command that, when called, will run either 
 * {@code commandA} or {@code commandB} depending on whether
 * {@code predicate} is true or false
 */
public final class SwitchCommand extends Command {
        private Command a;
        private Command b;
        private Supplier<Boolean> p;

    public SwitchCommand(Command commandA, 
            Command commandB, 
            Supplier<Boolean> predicate) {
        a = commandA;
        b = commandB;
        p = predicate;
    }

    @Override
    public void initialize() {
        if (p.get()) {
            a.schedule();
        } else {
            b.schedule();
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
