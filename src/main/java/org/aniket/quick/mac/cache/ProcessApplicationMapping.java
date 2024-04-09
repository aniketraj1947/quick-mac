package org.aniket.quick.mac.cache;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aniket.quick.mac.helper.CommandHelper;
import org.springframework.stereotype.Component;

/**
 * This is a singleton instance which serves <Process PID ->  Application Name> mapping
 * And updates the mapping after a fixed frequency.
 * This is available throughout the JVM for any command to make use of in their use-case!
 */
@Component
public class ProcessApplicationMapping {
    private static ProcessApplicationMapping instance;

    private static final String PID_COMMAND = "ps -p %s -o comm=";
    private static final String APP_PREFIX = "/Applications/";
    private static final String APP_SUFFIX = ".app";
    private static final String SYSTEM_APP = "System";
    private static final Pattern PATTERN = Pattern.compile(Pattern.quote(APP_PREFIX) + "(.*?)" + Pattern.quote(APP_SUFFIX));

    private ProcessApplicationMapping() {
    }

    private final LoadingCache<Integer, String> appsCache =
            CacheBuilder.newBuilder()
                    .maximumSize(10000)
                    .expireAfterAccess(30, TimeUnit.SECONDS)
                    .build(new CacheLoader<>() {
                        @Override
                        public String load(final Integer pid) throws Exception {
                            return getAppForPidThroughCLI(pid);
                        }
                    });

    public static ProcessApplicationMapping getInstance() {
        if (instance == null) {
            synchronized (ProcessApplicationMapping.class) {
                if (instance == null) {
                    instance = new ProcessApplicationMapping();
                }
            }
        }
        return instance;
    }

    private String getAppForPidThroughCLI(final int pid) {
        final String command = String.format(PID_COMMAND, pid);
        final String appString = CommandHelper.executeCommand(command);
        final Matcher matcher = PATTERN.matcher(appString);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return SYSTEM_APP;
        }
    }

    public String getAppName(final int pid) throws ExecutionException {
        return appsCache.get(pid);
    }
}
