package com.github.rasika.bpmn2solidity.tree.model.task;

import com.github.rasika.bpmn2solidity.BPMN2SolidityParser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.solidty.SolidityInstruction;
import com.github.rasika.bpmn2solidity.solidty.SolidityInterface;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;
import com.github.rasika.bpmn2solidity.tree.model.DataInputAssociation;
import com.github.rasika.bpmn2solidity.tree.model.DataOutputAssociation;
import com.github.rasika.bpmn2solidity.tree.model.IOSpecification;
import com.github.rasika.bpmn2solidity.tree.model.MessageFlow;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.SolidityNode;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.VariableTypeDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

//import org.apache.commons.lang.StringEscapeUtils;

public class Task extends SolidityNode {
    public final String functionName;
    public List<String> sourceParams = new ArrayList<>();
    public List<String> targetParams = new ArrayList<>();
    protected IOSpecification ioSpecification;
    protected List<DataInputAssociation> dataInputAssociations = new ArrayList<>();
    protected List<DataOutputAssociation> dataOutputAssociations = new ArrayList<>();
    protected boolean isPublicByMsgFlows = false;
    protected Map<String, Node> idToNode = new HashMap<>();
    public static List<String> modifiersStack = new Stack<>();
    public static Map<String, List<Function<String, String>>> inContModifiers = new HashMap<>();
    public String incoming;
    public String outgoing;

    public Task(Node currentNode) {
        super(currentNode);
        getChildren().forEach(child -> {
            if ("ioSpecification".equals(child.type)) {
                ioSpecification = TreeBuilder.createIOSpecification(child);
            } else if ("dataInputAssociation".equals(child.type)) {
                dataInputAssociations.add(TreeBuilder.createDataInputAssociation(child));
            } else if ("dataOutputAssociation".equals(child.type)) {
                dataOutputAssociations.add(TreeBuilder.createDataOutputAssociation(child));
            } else if ("incoming".equals(child.type)) {
                incoming = child.text;
            } else if ("outgoing".equals(child.type)) {
                outgoing = child.text;
            }
        });
        this.functionName = SolidityFunction.getFunctionName(name);
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.idToNode = idToNode;
        if (ioSpecification != null) {
            ioSpecification.relink(idToNode);
        }
        for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
            dataInputAssociation.relink(idToNode);
        }
        for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
            dataOutputAssociation.relink(idToNode);
        }
        isPublicByMsgFlows = idToNode.entrySet().stream().anyMatch(s -> {
            if (s.getValue() instanceof MessageFlow) {
                MessageFlow flow = (MessageFlow) s.getValue();
                return (id.equals(flow.targetRef));
            }
            return false;
        });
        relinkNextItem(idToNode);
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        System.out.println("// ------Task:" + name);
        validate(this.idToNode);
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
        List<String> stmts = new ArrayList<>();
        String returnType = "";
        boolean explicitReturnFound = false;
        // check output for return type and function body
        for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
            if (dataOutputAssociation.assignment != null) {
                VariableTypeDefinition typeDef = dataOutputAssociation.assignment.returnType;
                if (typeDef != null) {
                    returnType = typeDef.toSolidity();
                }
                if (dataOutputAssociation.assignment.expression.startsWith("return ")) {
                    explicitReturnFound = true;
                }
                stmts.add(dataOutputAssociation.assignment.toSolidity());
            } else if (dataOutputAssociation.source != null && dataOutputAssociation.target != null) {
                String name = dataOutputAssociation.target.name;
                String[] parts = name.split(":");
                stmts.add(parts[0] + " = " + BPMN2SolidityParser.unescapeXml(dataOutputAssociation.source.name) + ";");
            }
        }
        if (explicitReturnFound && returnType.isEmpty()) {
            throw new SolidityParserException("Return type not specified for Task:" + name);
        }
        Function<String, String> body = (padding) -> {
            StringBuilder str = new StringBuilder();
            if (inContModifiers.containsKey(incoming)) {
                for (Function<String, String> funcAcceptor : inContModifiers.get(incoming)) {
                    str.append(padding).append(funcAcceptor.apply(""));
                }
            }
            for (String stmt : stmts) {
                str.append(padding).append(stmt).append(System.lineSeparator());
            }
            return str.toString();
        };
        SolidityContract contract = template.getContract(getParentProcess(idToNode).contractName);
        if (isTopLevelTask() && contract instanceof SolidityInterface) {
            List<String> modifiers = getModifiers();
            modifiersStack.stream().filter(m -> !modifiers.contains(m)).forEach(modifiers::add);
            modifiersStack.clear();
            contract.addFunction(SolidityFunction.getFunctionName(name), modifiers, sourceParams, returnType, body, documentation);
        } else if (isTopLevelTask()) {
            List<String> modifiers = getModifiers();
            modifiersStack.stream().filter(m -> !modifiers.contains(m)).forEach(modifiers::add);
            modifiersStack.clear();
            contract.addFunction(SolidityFunction.getFunctionName(name), modifiers, targetParams, returnType, body, documentation);
        } else {
            String functionName = SolidityFunction.getFunctionName(getTopLevelTask().name);
            SolidityFunction function = contract.getFunction(functionName);
            if (function == null) {
                List<String> modifiers = getModifiers();
                modifiersStack.stream().filter(m -> !modifiers.contains(m)).forEach(modifiers::add);
                modifiersStack.clear();
                function = contract.addFunction(functionName, modifiers, targetParams, returnType, body, documentation);
            }
            function.addInstruction(new SolidityInstruction(body.apply("")));
        }
        getNextItemsSolidity(template);
    }

    protected List<String> getModifiers() {
        List<String> modifiers = SolidityFunction.getModifiers(name);
        if (isPublicByMsgFlows && !modifiers.contains("public")) {
            modifiers.add(0, "public");
        }
        return modifiers;
    }
}
