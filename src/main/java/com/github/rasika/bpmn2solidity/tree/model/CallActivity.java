package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.Parser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.solidty.SolidityInstruction;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CallActivity extends SolidityNode {
    private final List<Property> properties = new ArrayList<>();
    private Map<String, Node> idToNode;
    protected List<DataOutputAssociation> dataOutputAssociations = new ArrayList<>();

    public CallActivity(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("property".equals(child.type)) {
                properties.add(TreeBuilder.createProperty(child));
            } else if ("dataOutputAssociation".equals(child.type)) {
                dataOutputAssociations.add(TreeBuilder.createDataOutputAssociation(child));
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.idToNode = idToNode;
        for (Property property : properties) {
            property.relink(idToNode);
        }
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// ------CallActivity: " + name);
        SolidityContract contract = template.getContract(getParentProcess(idToNode).contractName);
        if (!isTopLevelTask()) {
            if (getTopLevelTask() != null) {
                SolidityFunction function = contract.getFunction(
                        SolidityFunction.getFunctionName(getTopLevelTask().name));
                function.addInstruction(new SolidityInstruction(
                        SolidityFunction.getFunctionName(name) + "(" +
                                properties.stream().map(p -> p.toSolidity().split(" ")[1]).collect(
                                        Collectors.joining(", ")) + ");"));
            } else {
                contract.getConstructor().addInstruction(new SolidityInstruction(
                        SolidityFunction.getFunctionName(name) + "(" +
                                properties.stream().map(p -> p.toSolidity().split(" ")[1]).collect(
                                        Collectors.joining(", ")) + ");"));
            }
        }

        List<String> stmts = new ArrayList<>();
        for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
            if (dataOutputAssociation.assignment != null) {
                stmts.add(dataOutputAssociation.assignment.toSolidity());
            } else if (dataOutputAssociation.source != null && dataOutputAssociation.target != null) {
                String name = dataOutputAssociation.target.name;
                String[] parts = name.split(":");
                stmts.add(parts[0] + " = " + Parser.unescapeXml(dataOutputAssociation.source.name) + ";");
            }
        }

        Function<String, String> body = (padding) -> {
            StringBuilder str = new StringBuilder();
            for (String stmt : stmts) {
                str.append(padding).append(stmt).append(System.lineSeparator());
            }
            return str.toString();
        };
        String functionName = SolidityFunction.getFunctionName(getTopLevelTask().name);
        SolidityFunction function = contract.getFunction(functionName);
        if (function != null) {
            function.addInstruction(new SolidityInstruction(body.apply("")));
        }

        getNextItemsSolidity(template);
    }
}
