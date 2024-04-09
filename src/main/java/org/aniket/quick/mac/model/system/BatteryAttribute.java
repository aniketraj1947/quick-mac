package org.aniket.quick.mac.model.system;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class BatteryAttribute {

    public static final Map<String, String> rawTextMapping = new HashMap<>();
    static {
        rawTextMapping.put("Charging", "Charging");
        rawTextMapping.put("Condition", "Condition");
        rawTextMapping.put("Connected", "Connected");
        rawTextMapping.put("CycleCount", "Cycle Count");
        rawTextMapping.put("FullChargeCapacity(mAh)", "Capacity (in mAh)");
        rawTextMapping.put("FullyCharged", "Fully Charged");
        rawTextMapping.put("LowPowerMode", "Low Power Mode Enabled");
        rawTextMapping.put("StateofCharge(%)", "Charged %");
    }
}
