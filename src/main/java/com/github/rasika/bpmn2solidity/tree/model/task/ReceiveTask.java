package com.github.rasika.bpmn2solidity.tree.model.task;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.solidty.SolidityInstruction;
import com.github.rasika.bpmn2solidity.tree.model.DataInputAssociation;
import com.github.rasika.bpmn2solidity.tree.model.DataOutputAssociation;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.Operation;
import com.github.rasika.bpmn2solidity.tree.model.Property;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.Message;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.VariableTypeDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ReceiveTask extends Task {
    private String messageRef;
    private String operationRef;
    private String implementation;
    private List<Property> properties = new ArrayList<>();
    private Message message;
    private Operation operation;
    private static boolean isFirst = true;
    private static List<Entry> entries = new ArrayList<>();

    public ReceiveTask(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(attr -> {
            if ("implementation".equals(attr.a)) {
                implementation = attr.b;
            } else if ("messageRef".equals(attr.a)) {
                messageRef = attr.b;
            } else if ("operationRef".equals(attr.a)) {
                operationRef = attr.b;
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        super.relink(idToNode);
        message = (Message) idToNode.get(messageRef);
        operation = (Operation) idToNode.get(operationRef);
        entries.add(new Entry(dataOutputAssociations.get(0).target.name, entries.size() + 1));
        relinkNextItem(idToNode);
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// ------ReceiveTask:" + name);
        // check parameters
        for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
            try {
                sourceParams.add(dataInputAssociation.source.toSolidity());
                targetParams.add(dataInputAssociation.target.toSolidity());
            } catch (SolidityParserException e) {
                //ignore
            }
        }
        String returnType = "";
        for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
            if (dataOutputAssociation.assignment != null) {
                VariableTypeDefinition typeDef = dataOutputAssociation.assignment.returnType;
                if (typeDef != null) {
                    returnType = typeDef.toSolidity();
                }
            }
        }
        Operation operation = (Operation) idToNode.get(operationRef);
        String url = operation.interfaceImplementationRef + operation.implementationRef;
        Function<String, String> body = (padding) -> {
            StringBuilder str = new StringBuilder();
            if (isFirst) {
                if (entries.size() > 1) {
                    IntStream.range(0, entries.size()).forEach(i -> {
                        str.append(padding);
                        if (i == 0) {
                            str.append("bytes32 ");
                        }
                        str.append("_queryId").append(" = oraclize_query(\"URL\", '").append(url).append("');").append(
                                System.lineSeparator());
                        str.append("validIds[_queryId] = ").append(++i).append(";").append(System.lineSeparator());
                    });
                } else {
                    str.append(padding).append("oraclize_query(\"URL\", '").append(url).append("');").append(
                            System.lineSeparator());
                }
            }
            return str.toString();
        };

        SolidityContract contract = template.getContract(getParentProcess(idToNode).contractName);
        if (!entries.isEmpty()) {
            if (isFirst) {
                template.addImport("github.com/oraclize/ethereum-api/oraclizeAPI.sol");
                contract.addStateVariable("mapping(bytes32 => uint)", "validIds", "", new ArrayList<>(), "");
                contract.addInheritedContract("usingOraclize");
                // Add callback function
                DataOutputAssociation out = dataOutputAssociations.get(0);
                List<String> modifiers = new ArrayList<>();
                modifiers.add("public");
                List<String> params = new ArrayList<>();
                params.add("bytes32 _queryId");
                params.add("string memory _result");
                Function<String, String> callbackBody = (padding) -> {
                    StringBuilder str = new StringBuilder();
                    str.append(padding).append("require(msg.sender == oraclize_cbAddress());").append(
                            System.lineSeparator());
                    if (entries.size() > 1) {
                        IntStream.range(0, entries.size()).forEach(i -> {
                            Entry s = entries.get(i);
                            String result = "_result;";
                            try {
                                if (out.target.toSolidity().split(" ")[0].equals("uint")) {
                                    result = "parseInt(_result);";
                                }
                            } catch (Exception e) {
                                //ignore
                            }
                            str.append(padding).append("if (validIds[_queryId] == ").append(++i).append(") {").append(
                                    System.lineSeparator());
                            str.append(padding).append("\t").append(s.dataType).append(" = ").append(result)
                                    .append(System.lineSeparator());
                            str.append(padding).append("\t").append("delete validIds[_queryId];")
                                    .append(System.lineSeparator());
                            str.append(padding).append("}").append(System.lineSeparator());
                        });
                    } else {
                        entries.forEach(s -> {
                            String result = "_result;";
                            try {
                                if (out.target.toSolidity().split(" ")[0].equals("uint")) {
                                    result = "parseInt(_result);";
                                }
                            } catch (Exception e) {
                                //ignore
                            }
                            str.append(padding).append(s.dataType).append(" = ").append(result).append(
                                    System.lineSeparator());
                        });
                    }
                    return str.toString();
                };
                contract.addFunction("__callback", modifiers, params, "", callbackBody, "");
            }

            if (isTopLevelTask()) {
                contract.addFunction(SolidityFunction.getFunctionName(name), getModifiers(), targetParams, returnType, body, documentation);
            } else {
                Task topLevelTask = getTopLevelTask();
                if (topLevelTask != null) {
                    contract.getFunction(SolidityFunction.getFunctionName(
                            topLevelTask.name)).addInstruction(new SolidityInstruction(body.apply("")));
                } else {
                    contract.getConstructor().addInstruction(new SolidityInstruction(body.apply("")));
                }
            }
        }
        isFirst = false;
        getNextItemsSolidity(template);
    }

    private static class Entry {
        private String dataType;
        private int count;

        public Entry(String dataType, int count) {
            this.dataType = dataType;
            this.count = count;
        }
    }
}
