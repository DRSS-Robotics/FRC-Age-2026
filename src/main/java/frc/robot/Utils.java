package frc.robot;

import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.hardware.TalonFX;

public class Utils {
    /**
     * Helper method to make Talon configs less verbose. Config slot defaults to 0 if not set.
     * 
     * @param talon the motor to configure
     * @param kS static voltage compensation
     * @param kV velocity voltage compensation
     * @param kP proportional term
     * @param kI integral term
     * @param kD derivative term
     * @return a {@link SlotConfigs}. Can be converted into a {@code SlotXConfigs}
     * using the static methods of that class. 
     */
    public static SlotConfigs configureTalonGains(
        TalonFX talon, 
        double kS,
        double kV,
        double kP,
        double kI,
        double kD) 
    {
        return configureTalonGains(talon, kS, kV, kP, kI, kD, 0);
    }


    
    /**
     * Helper method to make Talon configs less verbose.
     * 
     * @param talon the motor to configure
     * @param kS static voltage compensation
     * @param kV velocity voltage compensation
     * @param kP proportional term
     * @param kI integral term
     * @param kD derivative term
     * @param slotIndex the config slot to apply this config to.
     * @return a {@link SlotConfigs}. Can be converted into a {@code SlotXConfigs}
     * using the static methods of that class. 
     */
    public static SlotConfigs configureTalonGains(
        TalonFX talon, 
        double kS,
        double kV,
        double kP,
        double kI,
        double kD,
        int slotIndex)
    {
        SlotConfigs configs = new SlotConfigs();
        configs.withKS(kS)
               .withKV(kV)
               .withKP(kP)
               .withKI(kI)
               .withKD(kD);
        talon.getConfigurator().apply(configs);
        configs.SlotNumber = slotIndex;

        return configs;
    }
}