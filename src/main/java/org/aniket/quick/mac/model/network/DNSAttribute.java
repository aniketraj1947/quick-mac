package org.aniket.quick.mac.model.network;

public enum DNSAttribute {
    NAMESERVERS("Nameservers"),
    LOCAL_DOMAIN("Local DNS Hostname"),
    SEARCH_DOMAINS("Search Domains"),
    RESOLVER_TIMEOUT("Resolver Timeout (in sec)"),
    DNS_LOGGING("DNS Logging");

    private final String text;

    DNSAttribute(final String text) {
        this.text = text;
    }

    public static String getTextValue(final DNSAttribute attr) {
        return attr.text;
    }
}