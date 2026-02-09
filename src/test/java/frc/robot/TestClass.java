package frc.robot;

import static edu.wpi.first.units.Units.Degrees;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import frc.robot.subsystems.SuperstructureSubsystem;

public class TestClass {

    @Test
    void testguh() {
        assertEquals(3 + -1, 2);
    }

    @Test
    void storageWallTest() {
        
        // checking if storage wall is trying to open
        assertEquals(SuperstructureSubsystem.getStorageState(SuperstructureSubsystem.storageClosedAngle,
                SuperstructureSubsystem.storageOpenAngle), SuperstructureSubsystem.StorageWallState.kIsOpening);

        // checking if storage wall is trying to close
        assertEquals(
                SuperstructureSubsystem.getStorageState(SuperstructureSubsystem.storageOpenAngle,
                        SuperstructureSubsystem.storageClosedAngle),
                SuperstructureSubsystem.StorageWallState.kIsClosing);

        // checking if storage wall is fully open
        assertEquals(SuperstructureSubsystem.getStorageState(SuperstructureSubsystem.storageOpenAngle,
                SuperstructureSubsystem.storageOpenAngle), SuperstructureSubsystem.StorageWallState.kIsOpen);

        // checking if storage wall is fully closed
        assertEquals(
                SuperstructureSubsystem.getStorageState(SuperstructureSubsystem.storageClosedAngle,
                        SuperstructureSubsystem.storageClosedAngle),
                SuperstructureSubsystem.StorageWallState.kIsClosed);
    }

}