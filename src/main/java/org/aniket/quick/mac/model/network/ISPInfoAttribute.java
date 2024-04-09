package org.aniket.quick.mac.model.network;

public enum ISPInfoAttribute {
    ip("Public IP Address"),
    hostname("Hostname"),
    city("City"),
    region("Region"),
    country("Country Code"),
    loc("Location Coordinates"),
    org("ISP Organization"),
    postal("Postal Code"),
    timezone("Timezone");

    private final String text;

    ISPInfoAttribute(final String text) {
        this.text = text;
    }

    public static String getTextValue(final String key) {
        for (final ISPInfoAttribute attr : ISPInfoAttribute.values()) {
            if (attr.name().equals(key)) {
                return attr.text;
            }
        }
        return "";
    }
}
