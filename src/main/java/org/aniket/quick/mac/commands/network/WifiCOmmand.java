package org.aniket.quick.mac.commands.network;

import lombok.extern.slf4j.Slf4j;
import org.aniket.quick.mac.commands.Command;
import org.aniket.quick.mac.helper.*;
import org.aniket.quick.mac.model.network.WifiAttribute;
import org.aniket.quick.mac.model.network.WifiModel;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.*;
import java.util.List;
import org.springframework.shell.style.TemplateExecutor;

import static org.aniket.quick.mac.helper.TablePrinter.getColouredString;

@ShellComponent
@Slf4j
@ShellCommandGroup("Network:")
public class WifiCommand extends AbstractShellComponent implements Command {
    private static final String WDUTIL_COMMAND = "sudo wdutil info | awk '/WIFI/,/BLUETOOTH/ {if ($0 !~ /WIFI|BLUETOOTH/) print $0}' | sed '1d;$d'";

    private String table;

    @Override
    public void run() throws Exception {
        wifi();
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

    @ShellMethod(key = "wifi", value = "Get connected WiFi data, signal strength, noise and more.")
    public void wifi() throws Exception {
        final String wdutilOutput = CommandHelper.executeCommand(WDUTIL_COMMAND);
        final List<List<String>> wdUtilData = parseWdutilInfo(wdutilOutput);
        table = TablePrinter.printTable(getWifiCommandHeaderData(), TablePrinter.convertListTo2DArray(wdUtilData));
        print();
        postRun(getDefaultContext().getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private String[] getWifiCommandHeaderData() {
        final String[] header = new String[]{
                getColouredString("Property", Ansi.Color.CYAN),
                getColouredString("Value", Ansi.Color.CYAN)
        };
        return header;
    }

    private List<List<String>> parseWdutilInfo(String input) {
        final String[] lines = input.split("\n");
        final List<List<String>> data = new ArrayList<>();
        for (final WifiAttribute attr : WifiAttribute.values()) {
            for (final String line : lines) {
                final String[] parts = line.split(":");
                final String attribute = parts[0].trim();
                final String value = (parts.length > 1) ? parts[1].trim() : "";
                final Optional<WifiAttribute> wifiAttribute = WifiModel.getWifiAttribute(attribute);
                if (wifiAttribute.isEmpty()) {
                    continue;
                }
                if (wifiAttribute.get().equals(attr)) {
                    final List<String> row = new ArrayList<>();
                    row.add(WifiAttribute.getTextValue(attr));
                    row.add(WifiModel.getValue(attr, value));
                    data.add(row);
                }
            }
        }
        return data;
    }
}
