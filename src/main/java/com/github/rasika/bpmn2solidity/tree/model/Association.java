package com.github.rasika.bpmn2solidity.tree.model;

import java.util.Map;

public class Association extends SolidityNode {
    public String targetRef;
    public String sourceRef;

    public Association(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(pair -> {
                                    if ("sourceRef".equals(pair.a)) {
                                        this.sourceRef = pair.b;
                                    } else if ("targetRef".equals(pair.a)) {
                                        this.targetRef = pair.b;
                                    }
                                }

        );
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        System.out.println("");
    }
}
