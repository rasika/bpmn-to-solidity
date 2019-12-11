package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.Parser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.solidty.SolidityInstruction;
import com.github.rasika.bpmn2solidity.tree.model.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class IntermediateCatchEvent extends SolidityNode {
    private String condition;
    private Map<String, Node> idToNode = new HashMap<>();

    public IntermediateCatchEvent(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("conditionalEventDefinition".equals(child.type)) {
                child.getChildren().forEach(c -> {
                    if ("condition".equals(c.type)) {
                        this.condition = c.text;
                    }
                });
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.idToNode = idToNode;
        relinkNextItem(idToNode);
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        if(condition == null) {
            condition = name;
        }
        if(!condition.endsWith(";")) {
            condition += ";";
        }
        System.out.println("// ------IntermediateCatchEvent: " + name);
        if (getTopLevelTask() == null) {
            // Add modifier
            Task.modifiersStack.add(name);
            Process parentProcess = getParentProcess(idToNode);
            if (parentProcess != null && name != null && !name.startsWith("require(")) {
                SolidityContract contract = template.getContract(parentProcess.contractName);

                Scanner scanner = new Scanner(condition);
                List<String> stmts = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    stmts.add(line + System.lineSeparator());
                }

                Function<String, String> body = (padding) -> {
                    StringBuilder str = new StringBuilder();
                    for (String stmt : stmts) {
                        str.append(padding).append(stmt).append(System.lineSeparator());
                    }
                    return str.toString();
                };

                if(!contract.hasModifier(name)) {
                    contract.addModifier(name, new ArrayList<>(), body, documentation);
                }
            }
        } else {
            Process parentProcess = getParentProcess(idToNode);
            if (parentProcess != null && name != null) {
                SolidityContract contract = template.getContract(parentProcess.contractName);

                Scanner scanner = new Scanner(condition);
                List<String> stmts = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String postfix = "";
                    String line = scanner.nextLine();
                    if (scanner.hasNextLine()) {
                        postfix = System.lineSeparator();
                    }
                    stmts.add(Parser.unescapeXml(line.trim() + postfix));
                }

                Function<String, String> body = (padding) -> {
                    StringBuilder str = new StringBuilder();
                    for (String stmt : stmts) {
                        str.append(padding).append(stmt);
                    }
                    return str.toString();
                };

                if (name.startsWith("require(")) {
                    String functionName = SolidityFunction.getFunctionName(getTopLevelTask().name);
                    SolidityFunction function = contract.getFunction(functionName);
                    function.addInstruction(new SolidityInstruction(body.apply("")));
                } else {
                    Task.modifiersStack.add(name);
                    if(!contract.hasModifier(name)) {
                        contract.addModifier(name, new ArrayList<>(), body, documentation);
                    }
                }
            }
        }
        getNextItemsSolidity(template);
    }
}
