package com.github.rasika.bpmn2solidity.tree.model.task;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.Property;
import com.github.rasika.bpmn2solidity.tree.model.SolidityNode;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.Message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SendTask extends Task {
    private String messageRef;
    private Message message;
    private List<Property> properties = new ArrayList<>();

    public SendTask(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(attr -> {
            if ("messageRef".equals(attr.a)) {
                messageRef = attr.b;
            }
        });
        getChildren().forEach(child -> {
            if ("property".equals(child.type)) {
                properties.add(TreeBuilder.createProperty(child));
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        super.relink(idToNode);
        message = (Message) idToNode.get(messageRef);
        message.parentProcess = getParentProcess(idToNode);
        message.parentId = id;
        message.properties = properties;
        message.relink(idToNode);

        // Add into first entry
        Map<String, SolidityNode> oldItems = nextItems;
        nextItems = new LinkedHashMap<>();
        nextItems.put(message.id, message);
        nextItems.putAll(oldItems);

        relinkNextItem(idToNode);
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// ------SendTask:" + name);
        message.setTopLevelTask(findTopLevelTask(id, message.parentProcess, idToNode));
        getNextItemsSolidity(template);
    }
}
