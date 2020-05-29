package com.github.rasika.bpmn2solidity.solidty;

import com.github.rasika.bpmn2solidity.BPMN2SolidityParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SolidityGlobalVariable {

    private final String type;
    private final String value;
    private final List<String> modifiers;
    private final String name;
    private final String documentation;

    public SolidityGlobalVariable(String type, String name, String value, List<String> modifiers,
                                  String documentation) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.modifiers = modifiers;
        this.documentation = documentation;
    }


    public static String getVariableName(String name) {
        int index = name.indexOf(":");
        String rv;
        if (index > -1) {
            rv = Character.toLowerCase(name.charAt(0)) + name.substring(1, index);
        } else {
            rv = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        rv = rv.replaceAll(" ", "");
        rv = BPMN2SolidityParser.unescapeXml(rv);
        return rv;
    }

    public static List<String> getModifiers(String str) {
        String[] parts = str.split(":");
        List<String> modifiers = new ArrayList<>();
        if (parts.length >= 2) {
            IntStream.range(1, parts.length).forEach(i -> modifiers.add(parts[i]));
        }
        return modifiers;
    }

    public String render(String padding) {
        StringBuilder rv = new StringBuilder();
        if (!documentation.isEmpty()) {
            SolidityContract.renderDocsInline(documentation, rv);
        }
        rv.append(padding).append(type).append(" ");
        if (!modifiers.isEmpty()) {
            rv.append(String.join(" ", modifiers)).append(" ");
        }
        rv.append(name);
        if (value != null && !value.isEmpty()) {
            rv.append(" = ").append(value);
        }
        rv.append(";").append(System.lineSeparator());
        return rv.toString();
    }
}
