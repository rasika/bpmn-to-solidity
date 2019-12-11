package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.tree.model.toplevel.VariableTypeDefinition;

import java.util.Map;

public class Property extends SolidityNode {
    private String itemSubjectRef;
    private VariableTypeDefinition typeDefinition;

    public Property(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(attr -> {
            if ("itemSubjectRef".equals(attr.a)) {
                itemSubjectRef = attr.b;
            }
        });

    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.typeDefinition = (VariableTypeDefinition) idToNode.get(this.itemSubjectRef);
    }

    @Override
    public String toSolidity() {
        return typeDefinition.toSolidity() + " " + name;
    }
}
