package org.aniket.quick.mac.commands.security;

import lombok.extern.slf4j.Slf4j;
import org.aniket.quick.mac.commands.Command;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import org.springframework.shell.style.TemplateExecutor;

@ShellComponent
@Slf4j
@ShellCommandGroup("Security - Coming Soon!")
public class SecurityCommands extends AbstractShellComponent implements Command {

    private String table;

    @Override
    public void run() throws Exception {
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

    @ShellMethod(key = "security-commands", value = "firewall-status, vulnerability-scan, security-policies and other security commands under construction!")
    public String security() throws Exception {
        return "Under construction!";
    }
}
