package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndEvent extends SolidityNode {
    private String messageId;
    private Message message;
    private EventType eventType;
    private Map<String, Node> idToNode = new HashMap<>();
    private List<String> params = new ArrayList<>();
    private List<Property> properties = new ArrayList<>();

    public EndEvent(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("property".equals(child.type)) {
                properties.add(TreeBuilder.createProperty(child));
            } else if ("messageEventDefinition".equals(child.type)) {
                child.getAttributes().forEach(pair -> {
                    if ("messageRef".equals(pair.a)) {
                        this.messageId = pair.b;
                    }
                });
                eventType = EventType.MessageEventDefinition;
            } else if ("conditionalEventDefinition".equals(child.type)) {
                eventType = EventType.ConditionalEventDefinition;
            } else if ("signalEventDefinition".equals(child.type)) {
                eventType = EventType.SignalEventDefinition;
            } else if ("timerEventDefinition".equals(child.type)) {
                eventType = EventType.TimerEventDefinition;
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.idToNode = idToNode;
        message = (Message) idToNode.get(messageId);
        if (message != null) {
            message.parentProcess = getParentProcess(idToNode);
            message.parentId = id;
            message.properties = properties;
            message.setTopLevelTask(getTopLevelTask());
            message.relink(idToNode);
            nextItems.put(message.id, message);
            message.relink(idToNode);
        }
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// ------EndEvent: " + name);
        if (message != null) {
            message.toSolidity(template);
        }
    }

    public enum EventType {
        ConditionalEventDefinition, MessageEventDefinition, SignalEventDefinition, TimerEventDefinition
    }
}
