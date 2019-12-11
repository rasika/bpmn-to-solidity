package com.github.rasika.bpmn2solidity.tree.model;

import java.util.Map;

public class OutputSet extends SolidityNode {
	private String dataOutputRefs;
	private DataOutput dataOutput;

	public OutputSet(Node currentNode) {
		super(currentNode);
		getChildren().forEach(child -> {
			if ("dataOutputRefs".equals(child.type)) {
				dataOutputRefs = child.text;
			}
		});
	}
	
	@Override
    public void relink(Map<String, Node> idToNode) {
		dataOutput = (DataOutput)idToNode.get(dataOutputRefs);
        if (dataOutput != null) {
        	dataOutput.relink(idToNode);
        }
    }
}
