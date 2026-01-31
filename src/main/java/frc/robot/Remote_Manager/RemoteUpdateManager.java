
package frc.robot.Remote_Manager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class RemoteUpdateManager {

    private static RemoteUpdateManager _instance = null;
    private HashMap<String, IRemoteUpdate> subsystems;
    private UdpUpdateListener listener;
    private Thread udpThread;

    private RemoteUpdateManager() {
        _instance = this;
        subsystems = new HashMap<>();
    }

    /**
     * start the UDP listener thread
     */
    public static void startThread(String localIp, Integer port) {
            getInstance().listener = new UdpUpdateListener(localIp, port,
                        RemoteUpdateManager::onUpdateFromClient,
                        RemoteUpdateManager::serializeStates);
            getInstance().udpThread = new Thread(getInstance().listener);
            getInstance().udpThread.start();
    }

    private static RemoteUpdateManager getInstance() {
        if (_instance == null) {
            System.out.println("[Remote Mechanism Manager] remote update manager singleton is null, setting static var");
            return new RemoteUpdateManager();
        }
        return _instance;
    }

    /**
     * register a subsystem to be managed by the client. when the client sends a packet dictating that
     * the {@code subsystem} named by {@code name} is to be updated, it calls the {@link IRemoteUpdate}
     * interface on that subsystem
     * @param subsystem the subsystem to be remotely managed
     * @param name the string by which it is identified (make sure it's clear, like "Drivetrain" or
     * "Hang" or whatever)
     */
    public static void registerSubsystem(IRemoteUpdate subsystem, String name) {
        System.out.println("[Remote Mechanism Manager] registered subsystem: " + subsystem.toString() + " with name: " + name);
        getInstance().subsystems.put(name, subsystem);

    }

    /**
     * method called whenever a UDP packet requests an update on the subsystem named by
     * {@code subsystemName}. generally not for manual use but fine for testing and
     * debugging without the Python client
     * @param subsystemName name of the subsystem to update
     * @param state the new state of the subsystem, this number is what is propagated
     * back to the original {@code Object} that implements the {@link IRemoteUpdate}
     * interface
     */
    public static void onUpdateFromClient(String subsystemName, Integer state) {
        if (getInstance().subsystems.containsKey(subsystemName)) {
            getInstance().subsystems.get(subsystemName).updateNumericState(state);
        }

    }

    /**
     * method to convert the subsystem state {@code HashMap} into a string to be sent over
     * UDP
     * @return {@code String}
     */
    public static String serializeStates() {
        String serialized = "";
        
        for (String key : getInstance().subsystems.keySet()) {
            serialized += key + ":" + getInstance().subsystems.get(key).getNumericState().toString() + ";";
        }

        if (serialized != "") return serialized.substring(0,
                                     serialized.lastIndexOf(";"));
        return "";
    }
}
