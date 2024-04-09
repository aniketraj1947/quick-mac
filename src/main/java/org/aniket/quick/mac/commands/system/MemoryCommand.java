package org.aniket.quick.mac.commands.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aniket.quick.mac.cache.ProcessApplicationMapping;
import org.aniket.quick.mac.commands.Command;
import org.aniket.quick.mac.helper.*;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
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
@ShellCommandGroup("OS & System:")
public class MemoryCommand extends AbstractShellComponent implements Command {
    private static final String TOP_COMMAND = "top -stats pid,mem -o mem -n 25 -l 1 | tail -n 25";

    private String table;

    @Override
    public void run() throws Exception {
        memory();
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

    @ShellMethod(key = "memory", value = "Top applications by their RAM usage.")
    public void memory() throws Exception {
        final String topOutput = CommandHelper.executeCommand(TOP_COMMAND);
        final List<List<String>> topCpuData = parseTopInfo(topOutput);
        table = TablePrinter.printTable(getMemoryCommandHeaderData(), TablePrinter.convertListTo2DArray(topCpuData));
        print();
        postRun(getDefaultContext().getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private String[] getMemoryCommandHeaderData() {
        final String[] header = new String[]{
                getColouredString("Application", Ansi.Color.CYAN),
                getColouredString("RAM Usage", Ansi.Color.CYAN)
        };
        return header;
    }

    private List<List<String>> parseTopInfo(String input) throws ExecutionException {
        final String[] lines = input.split("\n");
        final List<List<String>> data = new ArrayList<>();
        final Map<String, Integer> rows = new HashMap<>();
        for (final String line : lines) {
            if (line == null || line.isEmpty() || !Character.isDigit(line.charAt(0))) {
                continue;
            }
            final String rawLine = line.trim();
            final StringBuilder pid = new StringBuilder();
            for (int i = 0; i < rawLine.length(); ++i) {
                if (rawLine.charAt(i) == ' ') {
                    break;
                }
                pid.append(rawLine.charAt(i));
            }
            final StringBuilder memoryUsage = new StringBuilder();
            for (int i = rawLine.length() - 2; i >= 0; --i) {
                if (rawLine.charAt(i) == ' ') {
                    break;
                }
                memoryUsage.append(rawLine.charAt(i));
            }
            final String appName = ProcessApplicationMapping.getInstance()
                    .getAppName(Integer.parseInt(pid.toString()));
            final int memData = Integer.parseInt(memoryUsage.toString());
            if (rows.containsKey(appName)) {
                final int val = rows.get(appName);
                rows.put(appName, val + memData);
            } else {
                rows.put(appName, memData);
            }
        }
        final List<MemmoryDataRow> dataRows = getCpuDataRowList(rows);
        dataRows.sort((r1, r2) -> {
            if (r1.getMemory() == r2.getMemory()) {
                return 0;
            }
            return r1.getMemory() > r2.getMemory() ? -1 : 1;
        });

        for (final MemmoryDataRow cpuDataRow : dataRows) {
            final List<String> row = new ArrayList<>();
            row.add(cpuDataRow.getAppName());
            row.add(String.format("%d %s", cpuDataRow.getMemory(), " MB"));
            data.add(row);
            if (data.size() == 10) {
                break;
            }
        }
        return data;
    }

    private List<MemmoryDataRow> getCpuDataRowList(Map<String, Integer> rows) {
        final List<MemmoryDataRow> ret = new ArrayList<>();
        for (final Map.Entry<String, Integer> entry : rows.entrySet()) {
            final String key = entry.getKey();
            final int val = entry.getValue();
            ret.add(new MemmoryDataRow(key, val));
        }
        return ret;
    }

    @AllArgsConstructor
    @Getter
    private static class MemmoryDataRow {
        private final String appName;
        private final int memory;
    }
}
