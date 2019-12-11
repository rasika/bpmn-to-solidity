package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.Parser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;
import com.github.rasika.bpmn2solidity.tree.model.task.Task;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a constructor in solidity.
 *
 * <pre>
 *     constructor() {
 *         &lt;task_name>();
 *     }
 * </pre>
 */
public class StartEvent extends SolidityNode {
    private String messageId;
    private Message message;
    private Task task;
    private EventType eventType;
    private Map<String, Node> idToNode = new HashMap<>();
    private List<String> params = new ArrayList<>();
    private List<DataOutputAssociation> dataOutputAssociations = new ArrayList<>();

    public StartEvent(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("messageEventDefinition".equals(child.type)) {
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
            } else if ("dataOutputAssociation".equals(child.type)) {
                dataOutputAssociations.add(TreeBuilder.createDataOutputAssociation(child));
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.idToNode = idToNode;
        message = (Message) idToNode.get(messageId);
        if (message != null) {
            message.relink(idToNode);
        }
        if (task != null) {
            task.relink(idToNode);
        } else {
            getParentProcess(idToNode).associations.stream()
                    .filter(a -> id.equals(a.targetRef) && idToNode.get(a.sourceRef) instanceof DataInput)
                    .collect(Collectors.toList())
                    .forEach(a -> {
                        DataInput dataInput = (DataInput) idToNode.get(a.sourceRef);
                        dataInput.relink(idToNode);
                        params.add(dataInput.toSolidity());
                    });
        }
        for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
            dataOutputAssociation.relink(idToNode);
        }
        relinkNextItem(idToNode);
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// ------StartEvent: " + name);
        Function<String, String> body = (String padding) -> "";
        List<String> stmts = new ArrayList<>();
        if (eventType == null && task != null) {
            stmts.add(SolidityFunction.getFunctionName(task.name) + "("
                    + task.sourceParams.stream().map(str -> str.split(" ")[1]).collect(Collectors.joining(", "))
                    + ");");
        }
        for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
            if (dataOutputAssociation.assignment != null) {
                String name = dataOutputAssociation.source.name;
                String[] parts = name.split(":");
                String assignment = dataOutputAssociation.assignment.toSolidity();
                stmts.add(Parser.unescapeXml(assignment) + " = " + parts[0] + ";");
            } else if (dataOutputAssociation.source != null && dataOutputAssociation.target != null) {
                String name = dataOutputAssociation.target.name;
                String[] parts = name.split(":");
                stmts.add(parts[0] + " = " + Parser.unescapeXml(dataOutputAssociation.source.name) + ";");
            }
        }
        body = (padding) -> {
            StringBuilder str = new StringBuilder();
            for (String stmt : stmts) {
                str.append(padding).append(stmt).append(System.lineSeparator());
            }
            return str.toString();
        };
        SolidityContract contract = template.getContract(getParentProcess(idToNode).contractName);
        if (task != null) {
            if (!task.sourceParams.isEmpty() || !body.apply("").isEmpty()) {
                contract.addConstructor( SolidityFunction.getModifiers(name), task.sourceParams, body);
            }
            task.toSolidity(template);
        } else {
            List<String> params = new ArrayList<>();
            getParentProcess(idToNode).associations.stream()
                    .filter(a -> id.equals(a.targetRef) && idToNode.get(a.sourceRef) instanceof DataInput)
                    .collect(Collectors.toList())
                    .forEach(a -> params.add(((DataInput) idToNode.get(a.sourceRef)).toSolidity()));
            if (!params.isEmpty() || !body.apply("").isEmpty()) {
                contract.addConstructor( SolidityFunction.getModifiers(name), params, body);
            }
        }
        getNextItemsSolidity(template);
    }

    public enum EventType {
        ConditionalEventDefinition, MessageEventDefinition, SignalEventDefinition, TimerEventDefinition
    }
}
