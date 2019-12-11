package com.github.rasika.bpmn2solidity.tree.model.gateway;

import com.github.rasika.bpmn2solidity.Parser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.AbstractFunction;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityConstructor;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.solidty.SolidityIfElseInstruction;
import com.github.rasika.bpmn2solidity.solidty.SolidityInstruction;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.Process;
import com.github.rasika.bpmn2solidity.tree.model.SequenceFlow;
import com.github.rasika.bpmn2solidity.tree.model.SolidityNode;
import com.github.rasika.bpmn2solidity.tree.model.task.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

public class ExclusiveGateway extends SolidityNode {
    private Map<String, Node> idToNode;
    private String defaultSeq = null;
    private boolean isDiverging = false;

    public ExclusiveGateway(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(pair -> {
            if ("default".equals(pair.a)) {
                defaultSeq = pair.b;
            } else if ("gatewayDirection".equals(pair.a)) {
                isDiverging = "Diverging".equals(pair.b);
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
        System.out.println("// ------ExclusiveGW:" + id);
        if (isConverging()) {
            Process process = getParentProcess(idToNode);
            SolidityContract contract = template.getContract(process.contractName);
            Task topLevelTask = findTopLevelTask(id, process, idToNode);
            if (topLevelTask != null) {
                SolidityFunction function = contract.getFunction(topLevelTask.functionName);
                if (!function.getParentInstruction().empty()) {
                    function.clearAllParentInstructions();
                    return;
                }
            }

            List<SequenceFlow> flows = process.sequenceFlows.stream().filter(s -> id.equals(s.sourceRef)).collect(Collectors.toList());
            for (SequenceFlow flow : flows) {
                Node node = idToNode.get(flow.targetRef);
                if (node instanceof SolidityNode) {
                    ((SolidityNode) node).toSolidity(template);
                }
            }
        } else {
            Process process = getParentProcess(idToNode);
            SolidityContract contract = template.getContract(process.contractName);
            Task topLevelTask = findTopLevelTask(id, process, idToNode);
            if (topLevelTask != null) {
                SolidityFunction function = contract.getFunction(topLevelTask.functionName);
                List<SequenceFlow> flows = process.sequenceFlows.stream().filter(s -> id.equals(s.sourceRef)).collect(Collectors.toList());
                processIfConditions(template, function, flows);
            } else {
                SolidityConstructor constructor = contract.getConstructor();
                List<SequenceFlow> flows = process.sequenceFlows.stream().filter(s -> id.equals(s.sourceRef)).collect(Collectors.toList());
                processIfConditions(template, constructor, flows);
            }
        }
    }

    private void processIfConditions(SolidityCodeTemplate template, AbstractFunction function, List<SequenceFlow> flows) throws SolidityParserException {
        validateGateway(flows, defaultSeq);
        SolidityIfElseInstruction ifElseInstr = null;
        List<SolidityNode> toVisitNodes = new ArrayList<>();

        // Sort conditional sequence flows first
        Comparator<SequenceFlow> byFirst = comparing(f -> f.getChildren().stream().anyMatch(c -> "conditionExpression".equals(c.type)), reverseOrder());
        List<SequenceFlow> sortedFlow = flows.stream().sorted(byFirst).collect(Collectors.toList());

        for (SequenceFlow flow : sortedFlow) {
            Optional<String> optCondition = flow.getChildren().stream().map(c -> {
                if ("conditionExpression".equals(c.type)) {
                    return c.text;
                } else {
                    return null;
                }
            }).findAny();
            if (optCondition.isPresent()) {
                if (ifElseInstr == null) {
                    ifElseInstr = new SolidityIfElseInstruction(Parser.unescapeXml(optCondition.get()));
                } else {
                    ifElseInstr.addElseIfStmt(new SolidityIfElseInstruction(Parser.unescapeXml(optCondition.get())));
                }
                SolidityInstruction parentInstruction = function.getCurrentParentInstruction();
                if (parentInstruction != null) {
                    function.getCurrentParentInstruction().subInstructions.add(ifElseInstr);
                } else {
                    function.addInstruction(ifElseInstr);
                }
                //marks sub-instructions directive
                function.addParentInstruction(ifElseInstr);
            } else {
                if (defaultSeq != null && defaultSeq.equals(flow.id)) {
                    //clears sub-instructions directive
                    function.clearLastParentInstruction();
                } else if (ifElseInstr != null) {
                    SolidityInstruction elseStmt = new SolidityInstruction("");
                    ifElseInstr.setElseStmt(elseStmt);
                    //marks sub-instructions directive
                    function.addParentInstruction(elseStmt);
                } else {
                    //clears all sub-instructions directive
                    function.clearAllParentInstructions();
                }
            }
            // process forward nodes
            Node node = idToNode.get(flow.targetRef);
            if (node instanceof SolidityNode) {
                SolidityNode solidityNode = (SolidityNode) node;
                if (node instanceof InclusiveGateway) {
                    toVisitNodes.add(solidityNode);
                } else {
                    solidityNode.toSolidity(template);
                }
            }
        }
        for (SolidityNode solidityNode : toVisitNodes) {
            solidityNode.toSolidity(template);
        }
    }

    private void validateGateway(List<SequenceFlow> flows, String defaultSeq) throws SolidityParserException {
        if (flows.size() == 1) return;
        List<SequenceFlow> temp = new ArrayList<>(flows);
        // Remove if conditions and the default flow
        temp.removeIf(f -> (f.getChildren().stream().anyMatch(c -> "conditionExpression".equals(c.type)) || (defaultSeq != null && defaultSeq.equals(f.id))));
        // Now, remaining count should be one
        if (temp.size() > 1 && !isDiverging) {
            String suggestion = "";
            if (temp.size() == 2) {
                suggestion = " Make one flow default";
            }
            throw new SolidityParserException("Exclusive Gateway:" + id + " has more than one else cases!" + suggestion);
        }
    }

    private boolean checkForSubRoutine(SolidityCodeTemplate template) {
        Process process = getParentProcess(idToNode);
        SolidityContract contract = template.getContract(process.contractName);
        Task topLevelTask = findTopLevelTask(id, process, idToNode);
        if (topLevelTask != null) {
            SolidityFunction function = contract.getFunction(topLevelTask.functionName);
            if (!function.getParentInstruction().empty()) {
                function.clearAllParentInstructions();
                return true;
            }
        }
        return false;
    }

    private boolean isConverging() {
        Process process = getParentProcess(idToNode);
        List<SequenceFlow> outFlows = process.sequenceFlows.stream().filter(s -> id.equals(s.sourceRef)).collect(Collectors.toList());
        List<SequenceFlow> inFlows = process.sequenceFlows.stream().filter(s -> id.equals(s.targetRef)).collect(Collectors.toList());
        return outFlows.size() == 1 && inFlows.size() > 1;
    }
}
