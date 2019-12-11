package com.github.rasika.bpmn2solidity.tree.model.toplevel;

import com.github.rasika.bpmn2solidity.Parser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityEnum;
import com.github.rasika.bpmn2solidity.solidty.SolidityGlobalVariable;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.Process;
import com.github.rasika.bpmn2solidity.tree.model.SolidityNode;

import java.util.List;
import java.util.Map;

public class DataStore extends SolidityNode {
    public Process parentProcess;
    public String dataState;
    private String itemSubjectRef;
    public VariableTypeDefinition variableTypeDefinition;
    public List<String> modifiers;

    public DataStore(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(pair -> {
            if ("itemSubjectRef".equals(pair.a)) {
                itemSubjectRef = pair.b;
            }
        });
        modifiers = SolidityGlobalVariable.getModifiers(name);
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.variableTypeDefinition = (VariableTypeDefinition) idToNode.get(itemSubjectRef);
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        if (variableTypeDefinition == null) {
            throw new SolidityParserException("Couldn't find data type for the DataStore:'" + name + "'");
        }
        String type = variableTypeDefinition.toSolidity();
        if (parentProcess != null) {
            SolidityContract contract = template.getContract(parentProcess.contractName);
            if ("enum".equals(type)) {
                String varName = SolidityEnum.getEnumName(name);
                contract.addEnum(type, varName, Parser.unescapeXml(dataState), modifiers, documentation);
            } else {
                String varName = SolidityGlobalVariable.getVariableName(name);
                contract.addStateVariable(type, varName, Parser.unescapeXml(dataState), modifiers, documentation);
            }
        }
    }

    @Override
    public String toSolidity() throws SolidityParserException {
        if (variableTypeDefinition == null) {
            throw new SolidityParserException("Couldn't find data type for the DataStore:'" + name + "'");
        }
        String varName = SolidityGlobalVariable.getVariableName(name);
        return variableTypeDefinition.toSolidity() + " " + varName;
    }
}
