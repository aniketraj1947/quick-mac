package org.aniket.quick.mac.model.network;

import lombok.Getter;
import lombok.Setter;
import org.aniket.quick.mac.model.Attribute;
import org.aniket.quick.mac.model.network.WifiAttribute;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
public class WifiModel {

    private final Attribute ssid;
    private final Attribute signalStrength;
    private final Attribute noise;
    private final Attribute linkSpeedInMbps;
    private final Attribute securityType;
    private final Attribute channel;
    private final Attribute ipAddress;
    private static final Map<String, WifiAttribute> rawTextMapping = new HashMap<>();
    static {
        rawTextMapping.put("SSID", WifiAttribute.SSID);
        rawTextMapping.put("RSSI", WifiAttribute.SIGNAL_STRENGTH);
        rawTextMapping.put("Noise", WifiAttribute.NOISE);
        rawTextMapping.put("Tx Rate", WifiAttribute.LINK_SPEED);
        rawTextMapping.put("Security", WifiAttribute.SECURITY_TYPE);
        rawTextMapping.put("Channel", WifiAttribute.CHANNEL);
        rawTextMapping.put("IPv4 Address", WifiAttribute.IP_ADDRESS);
        rawTextMapping.put("IPv4 Router", WifiAttribute.IP_ADDRESS_ROUTER);
    }

    public WifiModel(Attribute ssid, Attribute signalStrength, Attribute noise, Attribute linkSpeedInMbps, Attribute securityType, Attribute channel, Attribute ipAddress) {
        this.ssid = ssid;
        this.signalStrength = signalStrength;
        this.noise = noise;
        this.linkSpeedInMbps = linkSpeedInMbps;
        this.securityType = securityType;
        this.channel = channel;
        this.ipAddress = ipAddress;
    }

    public static Optional<WifiAttribute> getWifiAttribute(final String rawText) {
        final WifiAttribute attr = rawTextMapping.get(rawText);
        return attr != null ? Optional.of(attr) : Optional.empty();
    }

    public static String getValue(final WifiAttribute wifiAttribute, final String value) {
        switch (wifiAttribute) {
            case SSID, LINK_SPEED, SECURITY_TYPE, CHANNEL, IP_ADDRESS, IP_ADDRESS_ROUTER -> {
                return value;
            }
            case SIGNAL_STRENGTH -> {
                return getSignalStrengthPercentage(value);
            }
            case NOISE -> {
                return getNoisePercentage(value);
            }
        }
        return null;
    }

    private static String getNoisePercentage(final String value) {
        final Pattern pattern = Pattern.compile("(-?\\d+)");
        final Matcher matcher = pattern.matcher(value);
        String parsedValue = "";
        if (matcher.find()) {
            parsedValue = matcher.group(1);
        }
        int rawValue = Integer.parseInt(parsedValue.trim());
        int minNoise = -100;
        int maxNoise = -30;
        int noisePercentage = (rawValue - minNoise) * 100 / (maxNoise - minNoise);
        return noisePercentage + "%";
    }

    private static String getSignalStrengthPercentage(String value) {
        final Pattern pattern = Pattern.compile("(-?\\d+)");
        final Matcher matcher = pattern.matcher(value);
        String parsedValue = "";
        if (matcher.find()) {
            parsedValue = matcher.group(1);
        }
        int rawValue = Integer.parseInt(parsedValue.trim());
        int minRawValue = -100;
        int maxRawValue = -30;
        int boundedRawValue = Math.max(minRawValue, Math.min(maxRawValue, rawValue));
        double signalStrengthPercentage = ((double) (boundedRawValue - minRawValue) / (maxRawValue - minRawValue)) * 100;
        int roundedPercentage = (int) Math.round(signalStrengthPercentage);
        return Math.max(0, Math.min(100, roundedPercentage)) + "%";
    }
}
