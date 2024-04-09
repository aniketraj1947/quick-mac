package org.aniket.quick.mac.commands.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aniket.quick.mac.cache.ProcessApplicationMapping;
import org.aniket.quick.mac.commands.Command;
import org.aniket.quick.mac.helper.*;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.shell.style.TemplateExecutor;

import static org.aniket.quick.mac.helper.TablePrinter.getColouredString;

@ShellComponent
@Slf4j
@ShellCommandGroup("Network:")
public class NetworkUsageCommand extends AbstractShellComponent implements Command {
    private static final String NETTOP_COMMAND = "nettop -P -k state,interface,rx_dupe,rx_ooo,re-tx,rtt_avg,rcvsize,tx_win,tc_class,tc_mgt,cc_algo,P,C,R,W,arch -n -x -L 1 | sed '1d'";

    private String table;

    @Autowired
    public ProcessApplicationMapping processApplicationMapping;

    @Override
    public void run() throws Exception {
        networkUsage();
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

    @ShellMethod(key = "network-usage", value = "Get app specific aggregated network usage (egress/ingress) in last few seconds.")
    public void networkUsage() throws Exception {
        System.out.println("Top 10 applications by egress/ingress bytes in last few seconds..");
        final String nettopCommandOutput = CommandHelper.executeCommand(NETTOP_COMMAND);
        final List<List<String>> nettopData = parseNettopInfo(nettopCommandOutput);
        table = TablePrinter.printTable(getUsageCommandHeaderData(), TablePrinter.convertListTo2DArray(nettopData));
        print();
        postRun(getDefaultContext().getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private String[] getUsageCommandHeaderData() {
        final String[] header = new String[]{
                getColouredString("Application", Ansi.Color.CYAN),
                getColouredString("Egress", Ansi.Color.CYAN),
                getColouredString("Ingress", Ansi.Color.CYAN)
        };
        return header;
    }

    private List<List<String>> parseNettopInfo(String input) throws ExecutionException {
        final String[] lines = input.split("\n");
        final List<List<String>> data = new ArrayList<>();
        final Map<String, NetworkUsageRow.BytesData> rows = new HashMap<>();
        for (final String line : lines) {
            final String[] parts = line.split(",");
            final String[] pidPair = parts[1].split("\\.");
            final int pid = Integer.parseInt(pidPair[pidPair.length -1]);
            final String appName = ProcessApplicationMapping.getInstance().getAppName(pid);
            final int bytesIn = Integer.parseInt(parts[2]);
            final int bytesOut = Integer.parseInt(parts[3]);
            if (rows.containsKey(appName)) {
                rows.get(appName).add(bytesIn, bytesOut);
            } else {
                rows.put(appName, new NetworkUsageRow.BytesData(bytesIn, bytesOut));
            }
        }
        final List<NetworkUsageRow> dataRows = getNetworkDataRowList(rows);
        dataRows.sort((r1, r2) -> {
            if (r1.getBytesData().getBytesIn() == r2.getBytesData().getBytesIn()) {
                return 0;
            }
            return r1.getBytesData().getBytesIn() > r2.getBytesData().getBytesIn() ? -1 : 1;
        });

        for (final NetworkUsageRow networkUsageRow : dataRows) {
            // skip rows with 0 network usage
            if (networkUsageRow.isZero()) {
                continue;
            }
            final List<String> row = new ArrayList<>();
            row.add(networkUsageRow.getAppName());
            row.add(displayVal(networkUsageRow.getBytesData().getBytesIn()));
            row.add((displayVal(networkUsageRow.bytesData.getBytesOut())));
            data.add(row);
            if (data.size() == 10) {
                break;
            }
        }
        return data;
    }

    private List<NetworkUsageRow> getNetworkDataRowList(Map<String, NetworkUsageRow.BytesData> rows) {
        final List<NetworkUsageRow> ret = new ArrayList<>();
        for (final Map.Entry<String, NetworkUsageRow.BytesData> entry : rows.entrySet()) {
            final String key = entry.getKey();
            final NetworkUsageRow.BytesData val = entry.getValue();
            ret.add(new NetworkUsageRow(key, val));
        }
        return ret;
    }

    private String displayVal(final int bytes) {
        if (bytes > 1000_000) {
            return String.format("%.2f MB", (double) bytes / 1000_000.0);
        }
        if (bytes > 1000) {
            return String.format("%.2f KB", (double) bytes / 1000.0);
        }
        return String.valueOf(bytes);
    }

    @AllArgsConstructor
    @Getter
    public class NetworkUsageRow {
        private final String appName;
        private final BytesData bytesData;

        @AllArgsConstructor
        @Getter
        public static class BytesData {
            private int bytesIn = 0;
            private int bytesOut = 0;

            public void add(int bytesIn, int bytesOut) {
                this.bytesIn += bytesIn;
                this.bytesOut += bytesOut;
            }
        }

        public boolean isZero() {
            return ((bytesData.getBytesIn() == 0) && (bytesData.getBytesOut()) == 0);
        }
    }
}
