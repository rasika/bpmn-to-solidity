package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Process extends SolidityNode {
    /* solidty constructors */
    private final List<StartEvent> startEvents = new ArrayList<>();
    public String contractName;
    private IOSpecification ioSpecification;
    /* Only Process has sequenceFlows */
    public final List<SequenceFlow> sequenceFlows = new ArrayList<>();
    public final List<Association> associations = new ArrayList<>();
    public final List<DataStoreReference> dataStoreReferences = new ArrayList<>();
    public final List<DataObject> dataObjects = new ArrayList<>();

    public Process(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("ioSpecification".equals(child.type)) {
                ioSpecification = TreeBuilder.createIOSpecification(child);
            } else if ("sequenceFlow".equals(child.type)) {
                sequenceFlows.add(TreeBuilder.createSequenceFlow(child));
            } else if ("association".equals(child.type)) {
                associations.add(TreeBuilder.createAssociation(child));
            } else if ("dataStoreReference".equals(child.type)) {
                dataStoreReferences.add(TreeBuilder.createDataStoreReference(child));
            } else if ("dataObject".equals(child.type)) {
                dataObjects.add(TreeBuilder.createDataObject(child));
            }
        });

    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        if (ioSpecification != null) {
            ioSpecification.relink(idToNode);
        }
        for (SequenceFlow sequenceFlow : sequenceFlows) {
            Node sourceNode = idToNode.get(sequenceFlow.sourceRef);
            Node targetNode = idToNode.get(sequenceFlow.targetRef);
            if (sourceNode instanceof SolidityNode && targetNode instanceof SolidityNode) {
                // re-sync parent
                targetNode.parent = sourceNode;
            }
            if (sourceNode instanceof StartEvent) {
                // capture constructors
                StartEvent event = (StartEvent) sourceNode;
                startEvents.add(event);
            }
        }
        for (StartEvent event : startEvents) {
            event.relink(idToNode);
        }
        for (DataStoreReference dataStoreReference : dataStoreReferences) {
            dataStoreReference.relink(idToNode);
        }
        for (DataObject dataObject : dataObjects) {
            dataObject.relink(idToNode);
        }
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// ----Process: " + name);
        for (StartEvent event : startEvents) {
            event.toSolidity(template);
        }
        for (DataStoreReference dataStoreReference : dataStoreReferences) {
            dataStoreReference.toSolidity(template);
        }
        for (DataObject dataObject : dataObjects) {
            dataObject.toSolidity(template);
        }
    }
}
