package org.aniket.quick.mac.model.network;

public enum SpeedTestAttribute {
    DOWNLOAD_SPEED("Download Speed (in Mbps)"),
    UPLOAD_SPEED("Upload Speed (in Mbps)"),
    LATENCY("Latency (in ms)"),
    MIN_LATENCY("Min Latency (in ms)"),
    AVG_LATENCY("Avg Latency (in ms)"),
    MAX_LATENCY("Max Latency (in ms)"),
    JITTER("Jitter (in ms)");

    private final String text;

    SpeedTestAttribute(final String text) {
        this.text = text;
    }

    public static String getTextValue(final SpeedTestAttribute attr) {
        return attr.text;
    }
    }
