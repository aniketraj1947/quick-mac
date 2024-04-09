package org.aniket.quick.mac.commands.network;

import lombok.extern.slf4j.Slf4j;
import org.aniket.quick.mac.commands.Command;
import org.aniket.quick.mac.helper.CommandHelper;
import org.aniket.quick.mac.helper.TablePrinter;
import org.aniket.quick.mac.model.network.DNSAttribute;
import org.aniket.quick.mac.model.network.DNSModel;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.style.TemplateExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.aniket.quick.mac.helper.TablePrinter.getColouredString;

@ShellComponent
@Slf4j
@ShellCommandGroup("Network:")
@Order(1)
public class DNSCommand extends AbstractShellComponent implements Command {
    private static final String DNS_LOCALHOST_COMMAND = "scutil --get LocalHostName";
    private static final String DNS_INFO_SCUTIL_COMMAND = "scutil --dns";
    private static final String DNS_LOGGING_COMMAND = "log show --predicate 'process == \"mDNSResponder\"' --info | head";

    private String table;

    @Override
    public void run() throws Exception {
        dns();
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

    @ShellMethod(key = "dns", value = "Get nameservers, search domains and other DNS related information.")
    public void dns() throws Exception {
        System.out.print("Loading DNS data.....");
        final String scUtilData = CommandHelper.executeCommand(DNS_INFO_SCUTIL_COMMAND);
        final List<List<String>> scUtilDataParsed = parseScUtilInfo(scUtilData);
        final String hostData = CommandHelper.executeCommand(DNS_LOCALHOST_COMMAND);
        final List<List<String>> hostnameData = parseHostnameData(hostData);
        final String logData = CommandHelper.executeCommand(DNS_LOGGING_COMMAND);
        final List<List<String>> logDataParsed = parseDnsLogData(logData);
        final List<List<String>> federatedData = new ArrayList<>(scUtilDataParsed);
        federatedData.addAll(hostnameData);
        federatedData.addAll(logDataParsed);
        table = TablePrinter.printTable(getDnsCommandHeader(), TablePrinter.convertListTo2DArray(federatedData));
        System.out.print("\r");
        print();
        postRun(getDefaultContext().getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private List<List<String>> parseDnsLogData(final String logData) {
        final String[] lines = logData.split("\n");
        final List<List<String>> data = new ArrayList<>();
        final List<String> row = new ArrayList<>();
        row.add(DNSAttribute.getTextValue(DNSAttribute.DNS_LOGGING));
        row.add(DNSModel.getValue(DNSAttribute.DNS_LOGGING,
                Collections.singletonList(lines.length > 2 ? "Enabled" : "Disabled")));
        data.add(row);
        return data;
    }

    private List<List<String>> parseScUtilInfo(final String scUtilOutput) {
        final String[] lines = scUtilOutput.split("\n");
        final List<List<String>> data = new ArrayList<>();
        for (final DNSAttribute attr : DNSAttribute.values()) {
            final List<String> values = new ArrayList<>();
            final List<String> row = new ArrayList<>();
            boolean match = false;
            for (final String line : lines) {
                final String[] parts = line.split(":");
                final String attribute = parts[0].trim();
                final String value = (parts.length > 1) ? parts[1].trim() : "";
                final Optional<DNSAttribute> dnsAttribute = DNSModel.getDNSAttribute(attribute);
                if (dnsAttribute.isEmpty()) {
                    continue;
                }
                if (dnsAttribute.get().equals(attr)) {
                    match = true;
                    values.add(value);
                }
            }
            if (match) {
                row.add(DNSAttribute.getTextValue(attr));
                row.add(DNSModel.getValue(attr, values));
                data.add(row);
            }
        }
        return data;
    }

    private List<List<String>> parseHostnameData(final String hostNameOutput) {
        final List<List<String>> data = new ArrayList<>();
        final List<String> row = new ArrayList<>();
        row.add(DNSAttribute.getTextValue(DNSAttribute.LOCAL_DOMAIN));
        row.add(DNSModel.getValue(DNSAttribute.LOCAL_DOMAIN,
                Collections.singletonList(hostNameOutput.replaceAll("\n", "").trim())));
        data.add(row);
        return data;
    }

    private String[] getDnsCommandHeader() {
        final String[] header = new String[]{
                getColouredString("Property", Ansi.Color.CYAN),
                getColouredString("Value", Ansi.Color.CYAN)
        };
        return header;
    }
}
