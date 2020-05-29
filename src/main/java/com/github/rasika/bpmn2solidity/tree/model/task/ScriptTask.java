package com.github.rasika.bpmn2solidity.tree.model.task;

import com.github.rasika.bpmn2solidity.BPMN2SolidityParser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.solidty.SolidityInstruction;
import com.github.rasika.bpmn2solidity.tree.model.DataInputAssociation;
import com.github.rasika.bpmn2solidity.tree.model.DataOutputAssociation;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.VariableTypeDefinition;

import java.util.Scanner;
import java.util.function.Function;

public class ScriptTask extends Task {
    private String script = "";

    public ScriptTask(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
                    if ("script".equals(child.type)) {
                        script = BPMN2SolidityParser.unescapeXml(child.text);
                    }
                }
        );
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// ------ScriptTask:" + name);
        String returnType = "";
        // check parameters
        for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
            try {
                if (dataInputAssociation.source != null && dataInputAssociation.target != null) {
                    sourceParams.add(dataInputAssociation.source.toSolidity());
                    targetParams.add(dataInputAssociation.target.toSolidity());
                }
            } catch (SolidityParserException e) {
                //ignore
            }
        }
        // check output for return type and function body
        for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
            if (dataOutputAssociation.assignment != null) {
                VariableTypeDefinition typeDef = dataOutputAssociation.assignment.returnType;
                if (typeDef != null) {
                    returnType = typeDef.toSolidity();
                }
                script = script + System.lineSeparator() + dataOutputAssociation.assignment.toSolidity();
            }
        }
        Function<String, String> body = (padding) -> {
            StringBuilder str = new StringBuilder();
            Scanner scanner = new Scanner(script);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                str.append(padding).append(line.trim()).append(System.lineSeparator());
            }
            scanner.close();
            return str.toString();
        };
        SolidityContract contract = template.getContract(getParentProcess(idToNode).contractName);
        if (isTopLevelTask()) {
            contract.addFunction(SolidityFunction.getFunctionName(name), getModifiers(), targetParams, returnType, body, documentation);
        } else {
            contract.getFunction(SolidityFunction.getFunctionName(getTopLevelTask().name)).addInstruction(new SolidityInstruction(body.apply("")));
        }
        getNextItemsSolidity(template);
    }
}
