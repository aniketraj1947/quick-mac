package org.aniket.quick.mac.helper;

import java.net.InetAddress;
import lombok.Getter;
import org.aniket.quick.mac.helper.CommandHelper;

public class PingTest {
    private static final String PING_HOST = "8.8.8.8";
    private static final int JITTER_COUNT = 3;

    public static PingResponse getPingResponse() {
        try {
            final String command = String.format("ping %s -c %d", PING_HOST, JITTER_COUNT);
            final String output = CommandHelper.executeCommand(command);
            final int startIdx = output.lastIndexOf("stddev =");
            final String[] pingResponse = output.substring(startIdx+9, output.length()-4).split("/");
            return new PingResponse(Double.parseDouble(pingResponse[0]), Double.parseDouble(pingResponse[1]), Double.parseDouble(pingResponse[2]), Double.parseDouble(pingResponse[3]), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PingResponse(false);
    }

    @Getter
    public static class PingResponse {
        private final double minLatency;
        private final double avgLatency;
        private final double maxLatency;
        private final double jitter;
        private final boolean isSuccessful;

        public PingResponse(final double minLatency, final double avgLatency, final double maxLatency, final double jitter, final boolean isSuccessful) {
            this.minLatency = minLatency;
            this.avgLatency = avgLatency;
            this.maxLatency = maxLatency;
            this.jitter = jitter;
            this.isSuccessful = isSuccessful;
        }

        public PingResponse(final boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
            this.minLatency = 0.0f;
            this.avgLatency = 0.0f;
            this.maxLatency = 0.0f;
            this.jitter = 0.0f;
        }
    }
}
