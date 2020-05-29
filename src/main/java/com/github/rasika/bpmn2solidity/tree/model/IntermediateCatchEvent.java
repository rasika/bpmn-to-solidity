package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.BPMN2SolidityParser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.solidty.SolidityInstruction;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;
import com.github.rasika.bpmn2solidity.tree.model.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntermediateCatchEvent extends SolidityNode {
    private String condition;
    private Map<String, Node> idToNode = new HashMap<>();
    public List<Property> properties = new ArrayList<>();
    public String incoming;
    public String outgoing;

    public IntermediateCatchEvent(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("conditionalEventDefinition".equals(child.type)) {
                child.getChildren().forEach(c -> {
                    if ("condition".equals(c.type)) {
                        this.condition = c.text;
                    }
                });
            } else if ("property".equals(child.type)) {
                properties.add(TreeBuilder.createProperty(child));
            } else if ("incoming".equals(child.type)) {
                incoming = child.text;
            } else if ("outgoing".equals(child.type)) {
                outgoing = child.text;
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.idToNode = idToNode;
        for (Property property : properties) {
            property.relink(idToNode);
        }
        relinkNextItem(idToNode);
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        if(condition == null) {
            condition = name;
        }
        if(!condition.trim().endsWith(";")) {
            condition += ";";
        }
        System.out.println("// ------IntermediateCatchEvent: " + name);
        if (getTopLevelTask() == null) {
            Process parentProcess = getParentProcess(idToNode);
            if (parentProcess != null && name != null) {
                SolidityContract contract = template.getContract(parentProcess.contractName);

                Scanner scanner = new Scanner(condition);
                List<String> stmts = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    stmts.add(line);
                }

                Function<String, String> body = (padding) -> {
                    StringBuilder str = new StringBuilder();
                    for (String stmt : stmts) {
                        str.append(padding).append(stmt).append(System.lineSeparator());
                    }
                    return str.toString();
                };
                if (name.startsWith("require(")) {
                    List<Function<String, String>> list;
                    if (Task.inContModifiers.containsKey(incoming)) {
                        list = Task.inContModifiers.remove(incoming);
                    } else {
                        list = new ArrayList<>();
                    }
                    list.add(body);
                    Task.inContModifiers.put(outgoing, list);
                } else {
                    Task.modifiersStack.add(name);
                    if (!contract.hasModifier(extractModifierName(name))) {
                        contract.addModifier(extractModifierName(name), getDefs(properties), body, documentation);
                    }
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
                    stmts.add(BPMN2SolidityParser.unescapeXml(line.trim() + postfix));
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
                    if (!contract.hasModifier(extractModifierName(name))) {
                        contract.addModifier(extractModifierName(name), getDefs(properties), body, documentation);
                    }
                }
            }
        }
        getNextItemsSolidity(template);
    }

    private String extractModifierName(String name) {
        int index = name.indexOf("(");
        return index > -1 ? name.substring(0, index) : name;
    }

    private List<String> getDefs(List<Property> properties) {
        return properties.stream()
                .map(p -> p.toSolidity().split("=")[0])
                .collect(Collectors.toList());
    }
}
