package com.github.rasika.bpmn2solidity.tree;

import com.github.rasika.bpmn2solidity.antlr4.BPMN2;
import com.github.rasika.bpmn2solidity.tree.model.CallActivity;
import com.github.rasika.bpmn2solidity.tree.model.Collaboration;
import com.github.rasika.bpmn2solidity.tree.model.DataInput;
import com.github.rasika.bpmn2solidity.tree.model.DataObject;
import com.github.rasika.bpmn2solidity.tree.model.DataOutput;
import com.github.rasika.bpmn2solidity.tree.model.DataStoreReference;
import com.github.rasika.bpmn2solidity.tree.model.DocumentNode;
import com.github.rasika.bpmn2solidity.tree.model.EndEvent;
import com.github.rasika.bpmn2solidity.tree.model.IntermediateCatchEvent;
import com.github.rasika.bpmn2solidity.tree.model.MessageFlow;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.Operation;
import com.github.rasika.bpmn2solidity.tree.model.Process;
import com.github.rasika.bpmn2solidity.tree.model.SequenceFlow;
import com.github.rasika.bpmn2solidity.tree.model.StartEvent;
import com.github.rasika.bpmn2solidity.tree.model.gateway.EventBasedGateway;
import com.github.rasika.bpmn2solidity.tree.model.gateway.ExclusiveGateway;
import com.github.rasika.bpmn2solidity.tree.model.gateway.InclusiveGateway;
import com.github.rasika.bpmn2solidity.tree.model.gateway.ParallelGateway;
import com.github.rasika.bpmn2solidity.tree.model.task.ReceiveTask;
import com.github.rasika.bpmn2solidity.tree.model.task.ScriptTask;
import com.github.rasika.bpmn2solidity.tree.model.task.SendTask;
import com.github.rasika.bpmn2solidity.tree.model.task.Task;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.DataStore;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.Message;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.VariableTypeDefinition;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Pair;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class DocumnetBuilder {
    private final DocumentNode documentNode;
    private final CommonTokenStream tokenStream;
    private Stack<Node> nodes = new Stack<>();
    private Map<String, Collaboration> idToCollaboration = new HashMap<>();
    private Map<String, Node> idToNode = new HashMap<>();

    public DocumnetBuilder(DocumentNode documentNode, CommonTokenStream tokenStream) {
        this.documentNode = documentNode;
        this.tokenStream = tokenStream;
    }

    public void startBlockElement(BPMN2.BlockElementContext ctx) {
        Node parent = (nodes.isEmpty()) ? null : nodes.peek();
        Node blockNode = TreeBuilder.createBlockNode(parent);
        if (parent != null) {
            parent.addChild(blockNode);
        }
        nodes.push(blockNode);
    }

    public void endBlockElement(BPMN2.BlockElementContext ctx) {
        String name = extractName(ctx.Name(0).toString());
        Node blockNode = nodes.pop();
        String text = "";
        if (ctx.content() != null) {
            text = ctx.content().getText();
        }
        blockNode.text = text;
        blockNode.type = name;
        blockNode.position = getPosition(documentNode.position.getSrc(), ctx);
        resolveElement(name, blockNode, ctx);
    }

    public void startSelfClosingElement(BPMN2.SelfClosingElementContext ctx) {
        Node parent = (nodes.isEmpty()) ? null : nodes.peek();
        Node selfClosingNode = TreeBuilder.createSelfClosingNode(parent);
        if (parent != null) {
            parent.addChild(selfClosingNode);
        }
        nodes.push(selfClosingNode);
    }

    public void endSelfClosingElement(BPMN2.SelfClosingElementContext ctx) {
        String name = extractName(ctx.Name().toString());
        Node selfClosingNode = nodes.pop();
        selfClosingNode.type = name;
        selfClosingNode.position = getPosition(documentNode.position.getSrc(), ctx);
        resolveElement(name, selfClosingNode, null);
    }

    private void resolveElement(String name, Node currentNode, BPMN2.BlockElementContext ctx) {
        try {
            switch (name) {
                case "itemDefinition":
                    VariableTypeDefinition typeDefinition = TreeBuilder.createVariableTypeDefinition(currentNode);
                    idToNode.put(typeDefinition.id, typeDefinition);
                    break;
                case "message":
                    Message message = TreeBuilder.createMessage(currentNode);
                    idToNode.put(message.id, message);
                    break;
                case "dataStore":
                    DataStore dataStore = TreeBuilder.createDataStore(currentNode);
                    idToNode.put(dataStore.id, dataStore);
                    break;
                case "dataObject":
                    DataObject dataObject = TreeBuilder.createDataObject(currentNode);
                    idToNode.put(dataObject.id, dataObject);
                    break;
                case "collaboration":
                    Collaboration collaboration = TreeBuilder.createCollaboration(currentNode);
                    documentNode.addTopLevelNode(collaboration);
                    idToCollaboration.put(collaboration.id, collaboration);
                    break;
                case "process":
                case "subProcess":
                    Process process = TreeBuilder.createProcess(currentNode);
                    idToNode.put(process.id, process);
                    break;
                case "startEvent":
                    StartEvent startEvent = TreeBuilder.createStartEvent(currentNode);
                    idToNode.put(startEvent.id, startEvent);
                    break;
                case "endEvent":
                    EndEvent endEvent = TreeBuilder.createEndEvent(currentNode);
                    idToNode.put(endEvent.id, endEvent);
                    break;
                case "task":
                    Task task = TreeBuilder.createTask(currentNode);
                    idToNode.put(task.id, task);
                    break;
                case "sequenceFlow":
                    SequenceFlow sequenceFlow = TreeBuilder.createSequenceFlow(currentNode);
                    idToNode.put(sequenceFlow.id, sequenceFlow);
                    break;
                case "messageFlow":
                    MessageFlow messageFlow = TreeBuilder.createMessageFlow(currentNode);
                    idToNode.put(messageFlow.id, messageFlow);
                    break;
                case "dataInput":
                    DataInput dataInput = TreeBuilder.createDataInput(currentNode);
                    idToNode.put(dataInput.id, dataInput);
                    break;
                case "dataOutput":
                    DataOutput dataOutput = TreeBuilder.createDataOutput(currentNode);
                    idToNode.put(dataOutput.id, dataOutput);
                    break;
                case "eventBasedGateway":
                    EventBasedGateway eventBasedGateway = TreeBuilder.createEventBasedGateway(currentNode);
                    idToNode.put(eventBasedGateway.id, eventBasedGateway);
                    break;
                case "callActivity":
                    CallActivity callActivity = TreeBuilder.createCallActivity(currentNode);
                    idToNode.put(callActivity.id, callActivity);
                    break;
                case "scriptTask":
                    ScriptTask scriptTask = TreeBuilder.createScriptTask(currentNode);
                    idToNode.put(scriptTask.id, scriptTask);
                    break;
                case "sendTask":
                    SendTask sendTask = TreeBuilder.createSendTask(currentNode);
                    idToNode.put(sendTask.id, sendTask);
                    break;
                case "receiveTask":
                    ReceiveTask receiveTask = TreeBuilder.createReceiveTask(currentNode);
                    idToNode.put(receiveTask.id, receiveTask);
                    break;
                case "exclusiveGateway":
                    ExclusiveGateway exclusiveGateway = TreeBuilder.createExclusiveGateway(currentNode);
                    idToNode.put(exclusiveGateway.id, exclusiveGateway);
                    break;
                case "inclusiveGateway":
                    InclusiveGateway inclusiveGateway = TreeBuilder.createInclusiveGateway(currentNode);
                    idToNode.put(inclusiveGateway.id, inclusiveGateway);
                    break;
                case "parallelGateway":
                    ParallelGateway parallelGateway = TreeBuilder.createParallelGateway(currentNode);
                    idToNode.put(parallelGateway.id, parallelGateway);
                    break;
                case "dataStoreReference":
                    DataStoreReference storeReference = TreeBuilder.createDataStoreReference(currentNode);
                    idToNode.put(storeReference.id, storeReference);
                    break;
                case "operation":
                    Operation operation = TreeBuilder.createOperation(currentNode);
                    idToNode.put(operation.id, operation);
                    break;
                case "intermediateCatchEvent":
                    IntermediateCatchEvent catchEvent = TreeBuilder.createIntermediateCatchEvent(currentNode);
                    idToNode.put(catchEvent.id, catchEvent);
                    break;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAttribute(BPMN2.AttributeContext ctx) {
        String key = ctx.start.getText().replaceAll("\"", "");
        String value = ctx.stop.getText().replaceAll("\"", "");
        nodes.peek().addAttribute(new Pair<>(key, value));
    }

    private String extractName(String nameWithPrefix) {
        String[] split = nameWithPrefix.split(":");
        if (split.length == 2) {
            return split[1];
        }
        return nameWithPrefix;
    }


    private SourcePosition getPosition(Path src, ParserRuleContext ctx) {
        return new SourcePosition(src, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                ctx.stop.getLine(), ctx.stop.getCharPositionInLine());
    }

    public void relink() {
        for (Collaboration collaboration : idToCollaboration.values()) {
            collaboration.relink(idToNode);
        }
    }

    public Map<String, Node> getIdToNode() {
        return idToNode;
    }
}
