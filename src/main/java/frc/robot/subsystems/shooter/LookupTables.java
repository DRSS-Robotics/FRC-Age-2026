package frc.robot.subsystems.shooter;

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;
import frc.robot.Constants.ShooterConstants;

public class LookupTables {
    private final InterpolatingDoubleTreeMap shooterMap = new InterpolatingDoubleTreeMap();
    public LookupTables(){
        double[] keys = ShooterConstants.kShooterMapKeys;
        double[] values = ShooterConstants.kShooterMapValues;
        for(int i = 0; i < ShooterConstants.kShooterMapKeys.length; i++){
            shooterMap.put(keys[i], values[i]);
        }
    }
    public double getPowerbyDistance(double distance){
        return shooterMap.get(distance);
    }
    public double getPowerbyDistance(Distance distance){
        return getPowerbyDistance(distance.in(Units.Feet));
    }

}
