package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.tree.TreeBuilder;

import java.util.Map;

public class DataOutputAssociation extends SolidityNode {
    public SolidityNode source;
    public SolidityNode target;
    private String sourceRef;
    private String targetRef;
    public Assignment assignment;

    public DataOutputAssociation(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("sourceRef".equals(child.type)) {
                sourceRef = child.text;
            } else if ("targetRef".equals(child.type)) {
                targetRef = child.text;
            } else if ("assignment".equals(child.type)) {
            	assignment = TreeBuilder.createAssignment(child);
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        source = (SolidityNode) idToNode.get(sourceRef);
        if (source != null) {
            source.relink(idToNode);
        }
        target = (SolidityNode) idToNode.get(targetRef);
        if (target != null) {
            target.relink(idToNode);
        }
        if (assignment != null) {
        	assignment.relink(idToNode);
        }
    }
}
