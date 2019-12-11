package com.github.rasika.bpmn2solidity.solidty;

import com.github.rasika.bpmn2solidity.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SolidityStruct {

    private final List<String> modifiers;
    private final String name;
    private final List<String> subElements;

    public SolidityStruct(String name, List<String> subElements, List<String> modifiers) {
        this.name = name;
        this.subElements = subElements;
        this.modifiers = modifiers;
    }

    public static String getStructName(String name) {
        int index = name.indexOf(":");
        String rv;
        if (index > -1) {
            rv = Character.toUpperCase(name.charAt(0)) + name.substring(1, index);
        } else {
            rv = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        rv = rv.replaceAll(" ", "");
        rv = Parser.unescapeXml(rv);
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
        StringBuilder rv = new StringBuilder(padding);
        rv.append("struct ").append(name);
        if (!modifiers.isEmpty()) {
            rv.append(" ").append(String.join(" ", modifiers));
        }
        rv.append(" {").append(System.lineSeparator());
        if (!subElements.isEmpty()) {
            for(String subElement : subElements) {
                rv.append(padding).append(padding).append(subElement).append(";").append(System.lineSeparator());
            }
        }
        rv.append(padding).append("}").append(System.lineSeparator());
        return rv.toString();
    }
}
