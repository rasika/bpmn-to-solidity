package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Collaboration extends SolidityNode {
    public List<Participant> participants = new ArrayList<>();

    public Collaboration(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("participant".equals(child.type)) {
                participants.add(TreeBuilder.createParticipant(child));
            }
        });
    }

    @Override
    public String toString() {
        return "{id:" + id + ", name:" + name + "}";
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        for (Participant participant : participants) {
            participant.relink(idToNode);
        }
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// Collaboration: " + name);
        for (Participant participant : participants) {
            participant.toSolidity(template);
        }
    }
}
