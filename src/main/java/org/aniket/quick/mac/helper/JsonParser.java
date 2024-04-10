package org.aniket.quick.mac.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aniket.quick.mac.model.network.ISPInfoAttribute;

public class JsonParser {
    public static List<List<String>> convertISPJsonToList(String json) throws JsonProcessingException {
        final List<List<String>> data = new ArrayList<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode rootNode = objectMapper.readTree(json);

        final Iterator<String> fieldNames = rootNode.fieldNames();
        while (fieldNames.hasNext()) {
            final String fieldName = fieldNames.next();
            final String value = rootNode.get(fieldName).asText();
            final List<String> row = new ArrayList<>();
            if (fieldName.contains("readme")) {
                continue;
            }
            row.add(ISPInfoAttribute.getTextValue(fieldName));
            row.add(value);
            data.add(row);
        }
        return data;
    }
}
