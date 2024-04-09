package org.aniket.quick.mac.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.aniket.quick.mac.helper.CommandHelper;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

class ProcessApplicationMappingTest {

    @Test
    @Ignore
    public void testMap() throws Exception {
        final String value = "-62 dBm";
        final Pattern pattern = Pattern.compile("(-?\\d+)");
        final Matcher matcher = pattern.matcher(value);
        String parsedValue = "";
        if (matcher.find()) {
            parsedValue = matcher.group(1);
        }
    }
}