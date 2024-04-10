package org.aniket.quick.mac.model;

import lombok.Getter;

import java.util.List;

@Getter
public class Attribute {
    private final String name;
    private String value;
    private List<String> valuesList;
    private final boolean isList;

    public Attribute(final String name, final String value) {
        this.name = name;
        this.value = value;
        this.isList = false;
    }

    public Attribute(final String name, final List<String> valuesList) {
        this.name = name;
        this.valuesList = valuesList;
        this.isList = true;
    }

    public static String getCLITextForList(final List<String> values) {
        final StringBuilder ret = new StringBuilder();
        for (int i = 0; i < values.size(); ++i) {
            ret.append(values.get(i));
            if (i != values.size() - 1) {
                ret.append(", ");
            }
        }
        return ret.toString();
    }
}
