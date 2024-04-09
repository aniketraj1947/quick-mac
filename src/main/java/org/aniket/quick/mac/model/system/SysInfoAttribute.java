package org.aniket.quick.mac.model.system;

import lombok.Getter;

public enum SysInfoAttribute {
    MODEL("Model"),
    PROCESSOR("Processor"),
    PROCESSOR_SPEED("Processor Speed"),
    OS_VERSION("MacOS Version"),
    NO_OF_CORES("Number of Cores"),
    MEMORY("Memory"),
    TOTAL_DISK("Total Disk"),
    AVAILABLE_DISK("Available Disk");

    @Getter
    private final String text;

    SysInfoAttribute(final String text) {
        this.text = text;
    }

    public static String getTextValue(final SysInfoAttribute attr) {
        return attr.text;
    }
}
