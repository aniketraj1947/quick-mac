package org.aniket.quick.mac.model.system;

import org.aniket.quick.mac.model.system.SysInfoAttribute;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SysInfoModel {
    private static final Map<String, SysInfoAttribute> rawTextMapping = new HashMap<>();
    static {
        rawTextMapping.put("Model Name", SysInfoAttribute.MODEL);
        rawTextMapping.put("Processor Name", SysInfoAttribute.PROCESSOR);
        rawTextMapping.put("Processor Speed", SysInfoAttribute.PROCESSOR_SPEED);
        rawTextMapping.put("Total Number of Cores", SysInfoAttribute.NO_OF_CORES);
        rawTextMapping.put("Memory", SysInfoAttribute.MEMORY);
    }

    public static Optional<SysInfoAttribute> getSysInfoAttribute(final String rawText) {
        final SysInfoAttribute attr = rawTextMapping.get(rawText);
        return attr != null ? Optional.of(attr) : Optional.empty();
    }
}
