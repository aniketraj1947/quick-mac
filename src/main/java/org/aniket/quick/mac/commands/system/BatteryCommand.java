package org.aniket.quick.mac.commands.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aniket.quick.mac.cache.ProcessApplicationMapping;
import org.aniket.quick.mac.commands.Command;
import org.aniket.quick.mac.helper.*;
import org.aniket.quick.mac.model.system.BatteryAttribute;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.springframework.core.annotation.Order;
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
@Order(2)
public class BatteryCommand extends AbstractShellComponent implements Command {

    private static final String DRAWING = "drawing";
    private static final String DRAWING_PREFIX = "Now drawing from";
    private static final String POWER_SOURCE = "Power Source";
    private static final String REMAINING_TIME_ATTR = "Remaining Time (in HH:MM)";
    private static final String REMAINING = "remaining";
    private static final String ESTIMATE_UNAVAILABLE = "Estimate Unavailable";
    private static final String NO_TIME = "0:00";
    private static final String BATTERY_INFO_COMMAND = "system_profiler SPPowerDataType |" +
            " grep -e 'Fully Charged'" +
            " -e 'Charging: No'" +
            " -e 'Full Charge Capacity'" +
            " -e 'State of Charge (%)'" +
            " -e 'Cycle Count'" +
            " -e 'Condition'" +
            " -e 'Low Power Mode'" +
            " -e 'Connected' " +
            "| tr -d \"[:blank:]\" | sort | uniq";

    private static final String BATTERY_REMAINING_COMMAND = "pmset -g batt";

    private String table;

    @Override
    public void run() throws Exception {
        cpu();
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

    @ShellMethod(key = "battery", value = "Get live battery related data - cycle count, remaining time etc.")
    public void cpu() throws Exception {
        final String batOutput = CommandHelper.executeCommand(BATTERY_INFO_COMMAND);
        final List<List<String>> batData = parseBatInfo(batOutput);
        final String batTimeOutput = CommandHelper.executeCommand(BATTERY_REMAINING_COMMAND);
        final List<List<String>> batTimeOutputParsed = parseBatteryRemainingInfo(batTimeOutput);
        final List<List<String>> federatedData = new ArrayList<>();
        federatedData.addAll(batData);
        federatedData.addAll(batTimeOutputParsed);
        table = TablePrinter.printTable(getBatteryCommandHeaderData(), TablePrinter.convertListTo2DArray(federatedData));
        print();
        postRun(getDefaultContext().getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    /**
     * Now drawing from 'Battery Power'
     * -InternalBattery-0 (id=19005539)	100%; discharging; 4:06 remaining present: true
     * <p>
     * <p>
     * Now drawing from 'Battery Power'
     * -InternalBattery-0 (id=19005539)	100%; discharging; (no estimate) present: true
     * <p>
     * Now drawing from 'AC Power'
     * -InternalBattery-0 (id=19005539)	100%; charged; 0:00 remaining present: true
     */
    private List<List<String>> parseBatteryRemainingInfo(final String batTimeOutput) {
        final String[] lines = batTimeOutput.split("\n");
        final List<List<String>> data = new ArrayList<>();
        for (final String line : lines) {
            final List<String> row = new ArrayList<>();
            if (line.contains(DRAWING)) {
                final String val = line.replace(DRAWING_PREFIX, "");
                row.add(POWER_SOURCE);
                row.add(val.replaceAll("'", "").trim());
                data.add(row);
                continue;
            }
            row.add(REMAINING_TIME_ATTR);
            if (line.contains(REMAINING)) {
                final int idx = line.lastIndexOf(REMAINING);
                int start = idx;
                for (int i = idx; i >= 0; --i) {
                    if (line.charAt(i) ==';') {
                        start = i;
                        break;
                    }
                }
                final String val = line.substring(start+1, idx);
                row.add(val.contains(NO_TIME) ? ESTIMATE_UNAVAILABLE : val.trim());
                data.add(row);
            } else {
                row.add(ESTIMATE_UNAVAILABLE);
                data.add(row);
            }

        }
        return data;
    }

    private String[] getBatteryCommandHeaderData() {
        final String[] header = new String[]{
                getColouredString("Property", Ansi.Color.CYAN),
                getColouredString("Value", Ansi.Color.CYAN)
        };
        return header;
    }

    private List<List<String>> parseBatInfo(String input) throws ExecutionException {
        final String[] lines = input.split("\n");
        final List<List<String>> data = new ArrayList<>();
        for (final String line : lines) {
            final List<String> row = new ArrayList<>();
            final String[] parts = line.split(":");
            final String attr = parts[0];
            final String val = parts[1];
            row.add(BatteryAttribute.rawTextMapping.get(attr));
            row.add(val);
            data.add(row);
        }
        return data;
    }
}
