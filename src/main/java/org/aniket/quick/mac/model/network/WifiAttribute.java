package org.aniket.quick.mac.model.network;

public enum WifiAttribute {
    SSID("WiFi Name (SSID)"),
    SIGNAL_STRENGTH("Signal Strength (in %)"),
    NOISE("Noise (in %)"),
    LINK_SPEED("Link Speed (in Mbps)"),
    SECURITY_TYPE("Security Type"),
    CHANNEL("Wifi Channels"),
    IP_ADDRESS("IP Address"),
    IP_ADDRESS_ROUTER("IP Address Router");

    private final String text;

    WifiAttribute(final String text) {
        this.text = text;
    }

    public static String getTextValue(final WifiAttribute attr) {
        return attr.text;
    }
}
