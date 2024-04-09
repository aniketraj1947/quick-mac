package org.aniket.quick.mac.commands.system;

import lombok.extern.slf4j.Slf4j;
import org.aniket.quick.mac.commands.Command;
import org.aniket.quick.mac.helper.CommandHelper;
import org.aniket.quick.mac.helper.TablePrinter;
import org.aniket.quick.mac.model.system.SysInfoAttribute;
import org.aniket.quick.mac.model.system.SysInfoModel;
import org.fusesource.jansi.Ansi;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.style.TemplateExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.aniket.quick.mac.helper.TablePrinter.getColouredString;

@ShellComponent
@Slf4j
@ShellCommandGroup("OS & System:")
public class SystemInfoCommand extends AbstractShellComponent implements Command {

    private static final String DISK_COMMAND = "df / | sed '1d' |\n" +
            "    awk '\n" +
            "        /^\\/dev\\/disk1s1s1/ {\n" +
            "            size_byte = $2 * 512            # df uses 512 byte blocks\n" +
            "            avail_byte = $4 * 512\n" +
            "            total_size_gb = size_byte / 1000000000\n" +
            "            total_avail_gb = avail_byte / 1000000000\n" +
            "\n" +
            "            printf \"%.1f GB,%.1f GB\\n\", total_size_gb, total_avail_gb\n" +
            "        }\n" +
            "    '";

    private static final String HARDWARE_COMMAND = "system_profiler SPHardwareDataType";
    private static final String MAC_OS_VERSION_COMMAND = "sw_vers -productVersion";

    private String table;

    @Override
    public void run() throws Exception {
        sysInfo();
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

    @ShellMethod(key = "sys-info", value = "Get system hardware related information including CPU, Memory & Disk Usage.")
    public void sysInfo() throws Exception {
        final String hardwareData = CommandHelper.executeCommand(HARDWARE_COMMAND);
        final List<List<String>> hardwareDataParsed = parseHardwareData(hardwareData);
        final String osVersionData = CommandHelper.executeCommand(MAC_OS_VERSION_COMMAND);
        final List<List<String>> osVersionParsedData = parseOsVersionData(osVersionData);
        final String diskData = CommandHelper.executeCommand(DISK_COMMAND);
        final List<List<String>> diskDataParsed = parseDiskData(diskData);
        final List<List<String>> federatedData = new ArrayList<>();
        federatedData.addAll(hardwareDataParsed);
        federatedData.addAll(osVersionParsedData);
        federatedData.addAll(diskDataParsed);
        table = TablePrinter.printTable(getSysInfoHeaderData(), TablePrinter.convertListTo2DArray(federatedData));
        print();
        postRun(getDefaultContext().getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private List<List<String>> parseDiskData(final String diskData) {
        final List<List<String>> data = new ArrayList<>();
        final List<String> totalDisk = new ArrayList<>();
        final String[] diskOutput = diskData.trim().split(",");
        totalDisk.add(SysInfoAttribute.TOTAL_DISK.getText());
        totalDisk.add(diskOutput[0]);
        data.add(totalDisk);
        final List<String> freeDisk = new ArrayList<>();
        freeDisk.add(SysInfoAttribute.AVAILABLE_DISK.getText());
        freeDisk.add(diskOutput[1]);
        data.add(freeDisk);
        return data;
    }

    private List<List<String>> parseOsVersionData(final String osVersionData) {
        final List<List<String>> data = new ArrayList<>();
        final List<String> row = new ArrayList<>();
        row.add(SysInfoAttribute.OS_VERSION.getText());
        row.add(osVersionData.split("\n")[0].trim());
        data.add(row);
        return data;
    }

    private List<List<String>> parseHardwareData(final String hardwareData) {
        final String[] lines = hardwareData.split("\n");
        final List<List<String>> data = new ArrayList<>();
        for (final String line : lines) {
            final String rawLine = line.trim();
            if (!rawLine.contains(":")) {
                continue;
            }
            final String[] parts = rawLine.split(":");
            if (parts.length < 2) {
                continue;
            }
            final String attribute = parts[0].trim();
            final String value = parts[1].trim();
            final Optional<SysInfoAttribute> sysAttribute = SysInfoModel.getSysInfoAttribute(attribute);
            if (sysAttribute.isEmpty()) {
                continue;
            }
            final List<String> row = new ArrayList<>();
            row.add(sysAttribute.get().getText());
            row.add(value);
            data.add(row);
        }
        return data;
    }

    private String[] getSysInfoHeaderData() {
        final String[] header = new String[]{
                getColouredString("Property", Ansi.Color.CYAN),
                getColouredString("Value", Ansi.Color.CYAN)
        };
        return header;
    }
}
