package org.aniket.quick.mac.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

@Slf4j
public class CommandHelper {
    private static final String BASH_SHELL = "/bin/bash";
    public static String executeCommand (final String command) {
        final StringBuilder sb = new StringBuilder();
        try {
            final ProcessBuilder pb = new ProcessBuilder(BASH_SHELL);
            final Process bash = pb.start();
            final PrintStream ps = new PrintStream(bash.getOutputStream());
            ps.println(command);
            ps.close();
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(bash.getInputStream()))) {
                String line;
                while (null != (line = br.readLine())) {
                    sb.append(line).append("\n");
                }
            }
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(bash.getErrorStream()))) {
                String line;
                while (null != (line = br.readLine())) {
                    sb.append(line).append("\n");
                }
            }
            bash.waitFor();
        } catch (final Exception e) {
            log.error("Failed to exec command {}", command, e);
        }
        return sb.toString();
    }
}