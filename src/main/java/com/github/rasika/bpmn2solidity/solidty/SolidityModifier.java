package com.github.rasika.bpmn2solidity.solidty;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import static com.github.rasika.bpmn2solidity.tree.model.SolidityNode.TAB;

public class SolidityModifier {

    public final String name;
    private final List<String> params;
    private final Function<String, String> body;
    private final String documentation;

    public SolidityModifier(String name, List<String> params, Function<String, String> body, String documentation) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.documentation = documentation;
    }

    public String render() {
        StringBuilder rv = new StringBuilder();
        if (!documentation.isEmpty()) {
            SolidityContract.renderDocsInline(documentation, rv);
        }
        rv.append(TAB);
        rv.append("modifier ").append(name);
        if (!params.isEmpty()) {
            rv.append("(");
            rv.append(String.join(", ", params));
            rv.append(")");
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
        Scanner scanner = new Scanner(stringBuilder.toString());
        List<String> pendingLines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().startsWith("return")) {
                pendingLines.add(line);
            } else {
                result.append(line).append(System.lineSeparator());
            }
        }
        for (String line : pendingLines) {
            result.append(line).append(System.lineSeparator());
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SolidityModifier)) {
            return false;
        }
        SolidityModifier function = (SolidityModifier) o;
        return function.name.equals(name) &&
                function.params.equals(params) &&
                function.body.apply("").equals(body.apply(""));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + params.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
