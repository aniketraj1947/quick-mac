package org.aniket.quick.mac.commands.network;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.shell.style.TemplateExecutor;

import static org.aniket.quick.mac.helper.TablePrinter.getColouredString;

@ShellComponent
@Slf4j
@ShellCommandGroup("Network:")
public class ISPInfoCommand extends AbstractShellComponent implements Command {
    private static final String PUBLIC_IP_INFO_URL = "curl https://ipinfo.io/json";

    private String table;

    @Override
    public void run() throws Exception {
        ispInfo();
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

    @ShellMethod(key = "isp-info", value = "Get ISP related data for the public IP address in the connected network")
    public void ispInfo() throws Exception {
        final String publicIpOutput = CommandHelper.executeCommand(PUBLIC_IP_INFO_URL);
        final List<List<String>> publicIpData = JsonParser.convertISPJsonToList(publicIpOutput);
        table = TablePrinter.printTable(getISPInfoCommandHeaderData(), TablePrinter.convertListTo2DArray(publicIpData));
        print();
        postRun(getDefaultContext().getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private String[] getISPInfoCommandHeaderData() {
        final String[] header = new String[]{
                getColouredString("Property", Ansi.Color.CYAN),
                getColouredString("Value", Ansi.Color.CYAN)
        };
        return header;
    }
}
