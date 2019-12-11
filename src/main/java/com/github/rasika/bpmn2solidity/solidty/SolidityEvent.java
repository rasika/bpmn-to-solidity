package com.github.rasika.bpmn2solidity.solidty;

import java.util.List;

import static com.github.rasika.bpmn2solidity.tree.model.SolidityNode.TAB;

public class SolidityEvent {

    private final String name;
    private final List<String> params;

    public SolidityEvent(String name, List<String> params) {
        this.name = name;
        this.params = params;
    }

    public String render() {
        StringBuilder rv = new StringBuilder();
        rv.append(TAB);
        rv.append("event ").append(name);
        rv.append("(");
        rv.append(String.join(", ", params));
        rv.append(");");
        rv.append(System.lineSeparator());
        return rv.toString();
    }
}
