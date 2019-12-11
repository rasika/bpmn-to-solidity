package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.tree.TreeBuilder;

import java.util.Map;

public class IOSpecification extends SolidityNode {
    private InputSet inputSet;
    private OutputSet outputSet;

    public IOSpecification(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("inputSet".equals(child.type)) {
                inputSet = TreeBuilder.createInputSet(child);
            } else if ("outputSet".equals(child.type)) {
                outputSet = TreeBuilder.createOutputSet(child);
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        if (inputSet != null) {
            inputSet.relink(idToNode);
        }
        if (outputSet != null) {
        	outputSet.relink(idToNode);
        }
    }
}
