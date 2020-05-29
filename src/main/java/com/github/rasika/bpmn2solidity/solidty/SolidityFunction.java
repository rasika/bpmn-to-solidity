package com.github.rasika.bpmn2solidity.solidty;

import com.github.rasika.bpmn2solidity.BPMN2SolidityParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.github.rasika.bpmn2solidity.tree.model.SolidityNode.TAB;

public class SolidityFunction extends AbstractFunction {
    private final String name;
    private final List<String> modifiers;
    private final List<String> params;
    private final String returnType;
    private final Function<String, String> body;
    private final String documentation;

    public SolidityFunction(String name, List<String> modifiers, List<String> params, String returnType,
                            Function<String, String> body, String documentation) {
        this.name = name;
        this.modifiers = modifiers;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
        this.documentation = documentation;
    }

    public String render() {
        StringBuilder rv = new StringBuilder();
        if (!documentation.isEmpty()) {
            SolidityContract.renderDocs(documentation, rv);
        }
        rv.append(TAB);
        rv.append("function ").append(name);
        rv.append("(");
        rv.append(String.join(", ", params));
        rv.append(")");
        if (!modifiers.isEmpty()) {
            rv.append(" ").append(String.join(" ", modifiers));
        }
        if (returnType != null && !returnType.isEmpty()) {
            rv.append(" returns(").append(returnType).append(")");
        }
        rv.append(" {\n");

        // Re-order return statement into bottom
        rv.append(renderFunctionBody());

        rv.append(TAB);
        rv.append("}\n\n");
        return rv.toString();
    }

    private String renderFunctionBody() {
        StringBuilder result = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        if (body != null) {
            stringBuilder.append(body.apply(TAB + TAB));
        } else {
            stringBuilder.append("\n");
        }
        if (!instructions.isEmpty()) {
            for (SolidityInstruction instruction : instructions) {
                stringBuilder.append(instruction.render(TAB + TAB));
            }
            stringBuilder.setLength(stringBuilder.length() - 1); // remove last line feed
        }
        Scanner scanner = new Scanner(stringBuilder.toString());
        List<String> pendingLines = new ArrayList<>();
        List<String> postLines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().startsWith("return")) {
                pendingLines.add(line);
            } else if (line.trim().startsWith("require(")) {
                result.append(line).append(System.lineSeparator());
            } else {
                postLines.add(line);
            }
        }
        for (String line : postLines) {
            result.append(line).append(System.lineSeparator());
        }
        for (String line : pendingLines) {
            result.append(line).append(System.lineSeparator());
        }
        return result.toString();
    }

    public static String getFunctionName(String name) {
        int index = name.indexOf(":");
        String rv;
        if (index > -1) {
            rv = Character.toLowerCase(name.charAt(0)) + name.substring(1, index);
        } else {
            rv = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        rv = rv.replaceAll(" ", "");
        rv = BPMN2SolidityParser.unescapeXml(rv);
        return ("()".equals(rv)) ? "" : rv;
    }

    public static List<String> getModifiers(String str) {
        String[] parts = str.split(":");
        List<String> modifiers = new ArrayList<>();
        if (parts.length >= 2) {
            IntStream.range(1, parts.length).forEach(i -> modifiers.add(parts[i].trim()));
        }
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SolidityFunction)) {
            return false;
        }
        SolidityFunction function = (SolidityFunction) o;
        return function.name.equals(name) &&
                function.modifiers.equals(modifiers) &&
                function.params.equals(params) &&
                function.returnType.equals(returnType) &&
                function.body.apply("").equals(body.apply(""));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + params.hashCode();
        result = 31 * result + returnType.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
