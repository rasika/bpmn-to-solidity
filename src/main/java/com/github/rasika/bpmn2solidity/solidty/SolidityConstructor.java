package com.github.rasika.bpmn2solidity.solidty;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import static com.github.rasika.bpmn2solidity.tree.model.SolidityNode.TAB;

public class SolidityConstructor extends AbstractFunction{
    private final List<String> modifiers;
    private final List<String> params;
    private final Function<String, String> body;

    public SolidityConstructor(List<String> modifiers, List<String> params, Function<String, String> body) {
        this.params = params;
        this.modifiers = modifiers;
        this.body = body;
    }

    public String render() {
        StringBuilder rv = new StringBuilder();
        rv.append(TAB);
        rv.append("constructor (");
        rv.append(String.join(", ", params));
        rv.append(")");
        if (!modifiers.isEmpty()) {
            rv.append(" ").append(String.join(" ", modifiers));
        }
        rv.append("{\n");
        if (body != null) {
            rv.append(renderFunctionBody());
        }
        if (rv.charAt(rv.length() - 1) != '\n') {
            rv.append("\n");
        }
        rv.append(TAB);
        rv.append("}\n");
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
                stringBuilder.append(instruction.render(TAB + TAB)).append(System.lineSeparator());
            }
            stringBuilder.setLength(stringBuilder.length() - 1); // remove last line feed
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
        if (!(o instanceof SolidityConstructor)) {
            return false;
        }
        SolidityConstructor constructor = (SolidityConstructor) o;
        return constructor.params.equals(params) &&
                constructor.modifiers.equals(modifiers) &&
                constructor.body.apply("").equals(body.apply(""));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + params.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
