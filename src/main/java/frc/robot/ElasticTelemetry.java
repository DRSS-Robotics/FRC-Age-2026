package frc.robot;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

import static edu.wpi.first.wpilibj.DriverStation.Alliance;

import java.util.Optional;

import edu.wpi.first.wpilibj.util.Color;

public final class ElasticTelemetry {

    private static ElasticTelemetry instance = null;
    private NetworkTable telemetryNetworkTable;

    public static final double matchLength = 160;

    private StringPublisher blueAllianceWonAuto;
    private StringPublisher ourHubIsActive;
    private DoublePublisher matchTime;

    private static Color redAllianceColor = new Color(255, 0, 0);
    private static Color blueAllianceColor = new Color(0, 0, 255);
    private static Color noAllianceColor = new Color(20, 20, 20);

    private static Color hubActiveColor = new Color(0, 255, 0);
    private static Color hubInactiveColor = new Color(20, 20, 20);

    private ElasticTelemetry() {
        if (instance == null) {
            System.out.println("guh");
            instance = this;
        }

        telemetryNetworkTable = NetworkTableInstance.getDefault().getTable("Telemetry");

        blueAllianceWonAuto = telemetryNetworkTable.getStringTopic("autoWinningAlliance").publish();
        ourHubIsActive = telemetryNetworkTable.getStringTopic("ourHubIsActive").publish();
        matchTime = telemetryNetworkTable.getDoubleTopic("matchTime").publish();
    }

    public static ElasticTelemetry getInstance() {
        if (instance == null) {
            instance = new ElasticTelemetry();
        }

        return instance;
    }

    public static void updateNT() {

        Color allianceWinnerColor = noAllianceColor;
        if (getAutoWinner().isPresent()) {
            switch (getAutoWinner().get()) {
                case Red:
                    allianceWinnerColor = redAllianceColor;
                    break;
                case Blue:
                    allianceWinnerColor = blueAllianceColor;
                    break;
            }
        }

        getInstance().blueAllianceWonAuto.set(allianceWinnerColor.toHexString());

        if (DriverStation.getAlliance().isPresent()) {
            getInstance().ourHubIsActive.set(
                    (isHubActive(DriverStation.getAlliance().get())
                            ? hubActiveColor
                            : hubInactiveColor).toHexString());
        }

        getInstance().matchTime.set(Timer.getMatchTime());
    }

    public static Optional<Alliance> getAutoWinner() {
        if (DriverStation.getGameSpecificMessage().length() <= 0)
            return Optional.empty();

        switch (DriverStation.getGameSpecificMessage().charAt(0)) {
            case 'R':
                return Optional.of(Alliance.Red);
            case 'B':
                return Optional.of(Alliance.Blue);
            default:
                return Optional.empty();
        }
    }

    public static boolean isHubActive(Alliance alliance) {
        double time = DriverStation.getMatchTime();
        if (time < 30 || time >= matchLength - 30) {
            return true;
        }

        // shifts from 1, 2, 3, 4
        boolean shiftIsEven = (time - 30) % 50 > 25;
        return (getAutoWinner().get() == alliance) ? !shiftIsEven : shiftIsEven;

    }

}
