package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.tree.model.toplevel.VariableTypeDefinition;

import java.util.Map;

public class DataInput extends SolidityNode {
    private String itemSubjectRef;
    private VariableTypeDefinition variableTypeDefinition;

    public DataInput(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(pair -> {
            if ("itemSubjectRef".equals(pair.a)) {
                itemSubjectRef = pair.b;
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        if (itemSubjectRef != null) {
            variableTypeDefinition = (VariableTypeDefinition) idToNode.get(itemSubjectRef);
        }
    }

    @Override
    public String toSolidity() {
        return variableTypeDefinition.toSolidity() + " " + name;
    }
}

