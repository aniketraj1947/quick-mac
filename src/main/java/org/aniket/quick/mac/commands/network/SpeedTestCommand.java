package org.aniket.quick.mac.commands.network;

import static org.aniket.quick.mac.helper.TablePrinter.getColouredString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.aniket.quick.mac.commands.Command;
import org.aniket.quick.mac.helper.PingTest;
import org.aniket.quick.mac.helper.TablePrinter;
import org.aniket.quick.mac.model.network.SpeedTestAttribute;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import fr.bmartel.speedtest.*;
import fr.bmartel.speedtest.utils.*;

import org.springframework.shell.style.TemplateExecutor;

@ShellComponent
@Slf4j
@ShellCommandGroup("Network:")
public class SpeedTestCommand extends AbstractShellComponent implements Command {

    private static final String DOWNLOAD_SERVER = "http://ash-speed.hetzner.com/100MB.bin";
    private static final String UPLOAD_SERVER = String.format("ftp://speedtest.tele2.net/upload/" + SpeedTestUtils.generateFileName() + ".txt");
    private static final int FILE_SIZE_OCTET = 1000000;
    private static final int WAIT_MS = 100;
    private static final float DONE = 100.0f;

    private String table;

    @Override
    public void run() throws Exception {
        speedTest();
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public Terminal getAbstractTerminal() {
        return getTerminal();
    }

    @Override
    public ResourceLoader getAbstractResourceLoader() {
        return getResourceLoader();
    }

    @Override
    public TemplateExecutor getAbstractTemplateExecutor() {
        return getTemplateExecutor();
    }

    @ShellMethod(key = "speed-test", value = "Get download/upload speed, latency and jitter.")
    public void speedTest() throws Exception {
        final SpeedTestSocket speedTestSocket = new SpeedTestSocket();
        final SpeedTestListener listener = new SpeedTestListener();
        speedTestSocket.addSpeedTestListener(listener);
        speedTestSocket.startDownload(DOWNLOAD_SERVER);
        while (speedTestSocket.getLiveDownloadReport().getProgressPercent() < DONE) {
            final int completedPercent = Double.valueOf(speedTestSocket.getLiveDownloadReport().getProgressPercent()).intValue();
            final String progressBar = "Download Speed Test in Progress ..... " + completedPercent + "%";
            common(progressBar);
        }
        commonSpace();
        final Map<SpeedTestAttribute, String> values = new HashMap<>();
        values.put(SpeedTestAttribute.DOWNLOAD_SPEED, displayVal(speedTestSocket.getLiveDownloadReport().getTransferRateBit().toString()));
//        final SpeedTestSocket uploadSpeedTestSocket = new SpeedTestSocket();
//        final SpeedTestListener upListener = new SpeedTestListener();
//        uploadSpeedTestSocket.addSpeedTestListener(upListener);
//        uploadSpeedTestSocket.startUpload(UPLOAD_SERVER, FILE_SIZE_OCTET, 5000);
//        while (uploadSpeedTestSocket.getLiveUploadReport().getProgressPercent() < DONE) {
//            final int completedPercent = Double.valueOf(uploadSpeedTestSocket.getLiveUploadReport().getProgressPercent()).intValue();
//            final String progressBar = "Upload Speed Test in Progress ..... " + completedPercent + "%";
//            common(progressBar);
//        }
//        commonSpace();
        commonBackSpace();
        values.put(SpeedTestAttribute.UPLOAD_SPEED, "Upload Server unreachable");
        final PingTest.PingResponse response = PingTest.getPingResponse();
        values.put(SpeedTestAttribute.MIN_LATENCY, String.valueOf(response.getMinLatency()));
        values.put(SpeedTestAttribute.AVG_LATENCY, String.valueOf(response.getAvgLatency()));
        values.put(SpeedTestAttribute.MAX_LATENCY, String.valueOf(response.getMaxLatency()));
        values.put(SpeedTestAttribute.JITTER, String.valueOf(response.getJitter()));
        final List<List<String>> data = generateTableForSpeedTest(values);
        table = TablePrinter.printTable(getSpeedTestHeaderData(), TablePrinter.convertListTo2DArray(data));
        print();
        postRun(getDefaultContext().getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private String displayVal(final String bytes) {
        return String.format("%.2f ", Double.parseDouble(bytes) / 1000_000.0);
    }

    private void common(String progressBar) {
        System.out.print("\r" + progressBar);
        try {
            Thread.sleep(WAIT_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int j = 0; j < progressBar.length() + 4; j++) {
            System.out.print("\b");
        }
    }

    private void commonSpace() {
        for (int j = 0; j < 100; j++) {
            System.out.print(" ");
        }
    }

    private void commonBackSpace() {
        for (int j = 0; j < 100; j++) {
            System.out.print("\b");
        }
    }

    public static List<List<String>> generateTableForSpeedTest(final Map<SpeedTestAttribute, String> input) {
        final List<List<String>> data = new ArrayList<>();
        for (final SpeedTestAttribute attribute : SpeedTestAttribute.values()) {
            for (final Map.Entry<SpeedTestAttribute, String> entry : input.entrySet()) {
                final SpeedTestAttribute attr = entry.getKey();
                final String value = entry.getValue();
                final List<String> row = new ArrayList<>();
                if (attribute.equals(attr)) {
                    row.add(SpeedTestAttribute.getTextValue(attr));
                    row.add(value);
                    data.add(row);
                }
            }
        }
        return data;
    }

    private String[] getSpeedTestHeaderData() {
        final String[] header = new String[]{
                getColouredString("Property", Ansi.Color.CYAN),
                getColouredString("Value", Ansi.Color.CYAN)
        };
        return header;
    }

    private static class SpeedTestListener implements fr.bmartel.speedtest.inter.ISpeedTestListener {

        @Override
        public void onCompletion(SpeedTestReport report) {
        }

        @Override
        public void onProgress(float v, SpeedTestReport speedTestReport) {
        }

        @Override
        public void onError(fr.bmartel.speedtest.model.SpeedTestError error, String errorMessage) {
            System.out.println("[ERROR] " + errorMessage);
        }

        @Override
        public void onInterruption() {
            System.out.println("[Interrupted] ");
        }
    }
}


