package com.github.rasika.bpmn2solidity.solidty;

public class SolidityInterface extends SolidityContract{
    protected SolidityInterface(String name) {
        super(name);
    }

    public String render() {
        StringBuilder rv = new StringBuilder();
        rv.append("interface ").append(contractName).append(" {\n");
        for (SolidityFunction function : nameToFunction.values()) {
            // Replaces function body
            rv.append(function.render().replaceFirst("\\s\\{\\r?\\n.*}\\n", ";"));
        }
        rv.append("}\n");
        return rv.toString();
    }
}
