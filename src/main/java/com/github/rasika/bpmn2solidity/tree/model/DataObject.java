package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.Parser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityGlobalVariable;
import com.github.rasika.bpmn2solidity.solidty.SolidityStruct;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.DataStore;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.VariableTypeDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataObject extends SolidityNode {
    private String itemSubjectRef;
    private String dataStoreRef;
    private VariableTypeDefinition variableTypeDefinition;
    private DataStore dataStore;
    private String dataState;
    private Map<String, Node> idToNode;

    public DataObject(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(pair -> {
            if ("itemSubjectRef".equals(pair.a)) {
                itemSubjectRef = pair.b;
            } else if ("dataStoreRef".equals(pair.a)) {
                dataStoreRef = pair.b;
            }
        });
        getChildren().forEach(child -> {
            if ("dataState".equals(child.type)) {
                dataState = child.name;
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.idToNode = idToNode;
        if (itemSubjectRef != null) {
            this.variableTypeDefinition = (VariableTypeDefinition) idToNode.get(itemSubjectRef);
        }
        if (dataStoreRef != null) {
            this.dataStore = (DataStore) idToNode.get(dataStoreRef);
            this.dataStore.parentProcess = getParentProcess(idToNode);
            this.dataStore.dataState = dataState;
            this.dataStore.relink(idToNode);
        }
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) {
        SolidityContract contract = template.getContract(getParentProcess(idToNode).contractName);
        Process parentProcess = getParentProcess(idToNode);
        List<Association> associations = parentProcess.associations.stream().filter(a -> id.equals(a.targetRef)).collect(Collectors.toList());
        boolean isStruct = !associations.isEmpty() && associations.stream().allMatch(a -> idToNode.get(a.sourceRef) instanceof DataInput || idToNode.get(a.sourceRef) instanceof DataStoreReference);
        if (isStruct) {
            List<String> modifiers = SolidityStruct.getModifiers(name);
            modifiers.removeIf(s -> s.equals("public"));
            String varName = SolidityStruct.getStructName(name);
            List<String> subElements = new ArrayList<>();
            associations.stream()
                    .map(association -> idToNode.get(association.sourceRef))
                    .peek(node -> {
                        if (node instanceof SolidityNode) ((SolidityNode) node).relink(idToNode);
                    })
                    .forEach(node -> {
                if (node instanceof DataInput) {
                    subElements.add(((DataInput) node).toSolidity());
                } else if (node instanceof DataStoreReference) {
                    subElements.add(((DataStoreReference) node).toSolidity());
                }
            });
            contract.addStruct(varName, subElements, modifiers);
        } else if (variableTypeDefinition != null) {
            List<String> modifiers = SolidityGlobalVariable.getModifiers(name);
            modifiers.removeIf(s->s.equals("public"));
            String varName = SolidityGlobalVariable.getVariableName(name);
            contract.addStateVariable(variableTypeDefinition.toSolidity(), varName, Parser.unescapeXml(dataState), modifiers, documentation);
        } else if (dataStore != null) {
            try {
                dataStore.name = name;
                dataStore.toSolidity(template);
            } catch (SolidityParserException e) {
                throw new RuntimeException("Couldn't find data type for the DataObject:'" + name + "'", e);
            }
        }
    }
}
