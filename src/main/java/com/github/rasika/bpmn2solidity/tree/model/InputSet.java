package com.github.rasika.bpmn2solidity.tree.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InputSet extends SolidityNode {
    public List<String> dataInputRefs = new ArrayList<>();
    public List<DataInput> dataInputs = new ArrayList<>();

    public InputSet(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("dataInputRefs".equals(child.type)) {
                dataInputRefs.add(child.text);
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        for (String dataInputRef : dataInputRefs) {
            DataInput dataInput = (DataInput) idToNode.get(dataInputRef);
            if (dataInput != null) {
                dataInput.relink(idToNode);
                dataInputs.add(dataInput);
            }
        }
    }
}
