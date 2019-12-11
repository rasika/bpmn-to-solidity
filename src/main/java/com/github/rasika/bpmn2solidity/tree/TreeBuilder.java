package com.github.rasika.bpmn2solidity.tree;

import com.github.rasika.bpmn2solidity.tree.model.Assignment;
import com.github.rasika.bpmn2solidity.tree.model.Association;
import com.github.rasika.bpmn2solidity.tree.model.CallActivity;
import com.github.rasika.bpmn2solidity.tree.model.Collaboration;
import com.github.rasika.bpmn2solidity.tree.model.DataInput;
import com.github.rasika.bpmn2solidity.tree.model.DataInputAssociation;
import com.github.rasika.bpmn2solidity.tree.model.DataObject;
import com.github.rasika.bpmn2solidity.tree.model.DataOutput;
import com.github.rasika.bpmn2solidity.tree.model.DataOutputAssociation;
import com.github.rasika.bpmn2solidity.tree.model.DataStoreReference;
import com.github.rasika.bpmn2solidity.tree.model.DocumentNode;
import com.github.rasika.bpmn2solidity.tree.model.EndEvent;
import com.github.rasika.bpmn2solidity.tree.model.IOSpecification;
import com.github.rasika.bpmn2solidity.tree.model.InputSet;
import com.github.rasika.bpmn2solidity.tree.model.IntermediateCatchEvent;
import com.github.rasika.bpmn2solidity.tree.model.MessageFlow;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.Operation;
import com.github.rasika.bpmn2solidity.tree.model.OutputSet;
import com.github.rasika.bpmn2solidity.tree.model.Participant;
import com.github.rasika.bpmn2solidity.tree.model.Process;
import com.github.rasika.bpmn2solidity.tree.model.Property;
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

import java.nio.file.Path;

public class TreeBuilder {
    public static DocumentNode createDocumentNode(Path fileName, SourcePosition sourcePosition) {
        return new DocumentNode(fileName, sourcePosition);
    }

    public static Node createBlockNode(Node parent) {
        return new Node("block", parent);
    }

    public static Node createSelfClosingNode(Node parent) {
        return new Node("self-closing", parent);
    }

    public static VariableTypeDefinition createVariableTypeDefinition(Node currentNode) {
        return new VariableTypeDefinition(currentNode);
    }

    public static Collaboration createCollaboration(Node currentNode) {
        return new Collaboration(currentNode);
    }

    public static Participant createParticipant(Node currentNode) {
        return new Participant(currentNode);
    }

    public static Process createProcess(Node currentNode) {
        return new Process(currentNode);
    }

    public static StartEvent createStartEvent(Node currentNode) {
        return new StartEvent(currentNode);
    }

    public static EndEvent createEndEvent(Node currentNode) {
        return new EndEvent(currentNode);
    }

    public static Message createMessage(Node currentNode) {
        return new Message(currentNode);
    }

    public static Task createTask(Node currentNode) {
        return new Task(currentNode);
    }

    public static SequenceFlow createSequenceFlow(Node currentNode) {
        return new SequenceFlow(currentNode);
    }

    public static DataInput createDataInput(Node currentNode) {
        return new DataInput(currentNode);
    }

    public static InputSet createInputSet(Node currentNode) {
        return new InputSet(currentNode);
    }

    public static OutputSet createOutputSet(Node currentNode) {
        return new OutputSet(currentNode);
    }

    public static IOSpecification createIOSpecification(Node currentNode) {
        return new IOSpecification(currentNode);
    }

    public static DataInputAssociation createDataInputAssociation(Node currentNode) {
        return new DataInputAssociation(currentNode);
    }

    public static DataOutputAssociation createDataOutputAssociation(Node currentNode) {
        return new DataOutputAssociation(currentNode);
    }

    public static EventBasedGateway createEventBasedGateway(Node currentNode) {
        return new EventBasedGateway(currentNode);
    }

    public static CallActivity createCallActivity(Node currentNode) {
        return new CallActivity(currentNode);
    }

    public static MessageFlow createMessageFlow(Node currentNode) {
        return new MessageFlow(currentNode);
    }

    public static Assignment createAssignment(Node currentNode) {
        return new Assignment(currentNode);
    }

    public static ScriptTask createScriptTask(Node currentNode) {
        return new ScriptTask(currentNode);
    }

    public static ExclusiveGateway createExclusiveGateway(Node currentNode) {
        return new ExclusiveGateway(currentNode);
    }

    public static Association createAssociation(Node currentNode) {
        return new Association(currentNode);
    }

    public static SendTask createSendTask(Node currentNode) {
        return new SendTask(currentNode);
    }

    public static Property createProperty(Node currentNode) {
        return new Property(currentNode);
    }

    public static DataStoreReference createDataStoreReference(Node currentNode) {
        return new DataStoreReference(currentNode);
    }

    public static DataStore createDataStore(Node currentNode) {
        return new DataStore(currentNode);
    }

    public static DataOutput createDataOutput(Node currentNode) {
        return new DataOutput(currentNode);
    }

    public static InclusiveGateway createInclusiveGateway(Node currentNode) {
        return new InclusiveGateway(currentNode);
    }

    public static DataObject createDataObject(Node currentNode) {
        return new DataObject(currentNode);
    }

    public static ReceiveTask createReceiveTask(Node currentNode) {
        return new ReceiveTask(currentNode);
    }

    public static Operation createOperation(Node currentNode) {
        return new Operation(currentNode);
    }

    public static IntermediateCatchEvent createIntermediateCatchEvent(Node currentNode) {
        return new IntermediateCatchEvent(currentNode);
    }

    public static ParallelGateway createParallelGateway(Node currentNode) {
        return new ParallelGateway(currentNode);
    }
}
