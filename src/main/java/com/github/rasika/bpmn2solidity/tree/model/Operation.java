package com.github.rasika.bpmn2solidity.tree.model;

public class Operation extends SolidityNode {
    public String implementationRef;
    public String interfaceImplementationRef;

    public Operation(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(pair -> {
            if ("implementationRef".equals(pair.a)) {
                implementationRef = pair.b;
            }
        });
        Node parent = getParent();
        if("block".equals(parent.type)){
            parent.getAttributes().forEach(pair->{
                if("implementationRef".equals(pair.a)) {
                    interfaceImplementationRef = pair.b;
                }
            });
        }
    }
}
