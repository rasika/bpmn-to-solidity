package com.github.rasika.bpmn2solidity.tree.model.toplevel;

import com.github.rasika.bpmn2solidity.BPMN2SolidityParser;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.SolidityNode;

public class VariableTypeDefinition extends SolidityNode {
    private boolean isCollection;
    public String structureRef;

    public VariableTypeDefinition(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(pair -> {
            if ("structureRef".equals(pair.a)) {
                this.structureRef = pair.b;
            } else if ("isCollection".equals(pair.a)) {
                this.isCollection = Boolean.parseBoolean(pair.b);
            }
            });
    }

    @Override
    public String toSolidity() {
        String result = "";
        if (structureRef == null) {
            result = "";
        } else if (structureRef.startsWith("solidity:")) {
            result = structureRef.split("solidity:")[1];
        } else if (structureRef.contains(":string")) {
            result = "string";
        } else if (structureRef.contains(":int")) {
            result = "int";
        } else if (structureRef.contains(":uint")) {
            result = "uint";
        } else if (structureRef.contains(":address")) {
            result = "address";
        } else if (structureRef.contains(":token")) {
            result = "token";
        } else if (structureRef.contains(":boolean")) {
            result = "boolean";
        } else if (structureRef.contains(":float")) {
            result = "float";
        } else if (structureRef.contains(":double")) {
            result = "double";
        } else {
            result = BPMN2SolidityParser.unescapeXml(structureRef);
        }

        if (isCollection) {
            result += "[]";
        }
        return result;
    }
}
