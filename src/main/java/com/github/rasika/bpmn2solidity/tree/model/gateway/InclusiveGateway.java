package com.github.rasika.bpmn2solidity.tree.model.gateway;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.solidty.SolidityFunction;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.Process;
import com.github.rasika.bpmn2solidity.tree.model.SequenceFlow;
import com.github.rasika.bpmn2solidity.tree.model.SolidityNode;
import com.github.rasika.bpmn2solidity.tree.model.task.Task;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InclusiveGateway extends SolidityNode {

    private Map<String, Node> idToNode;

    public InclusiveGateway(Node currentNode) {
        super(currentNode);
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        this.idToNode = idToNode;
        relinkNextItem(idToNode);
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        if (checkForSubRoutine(template)) return;
        System.out.println("// ------InclusiveGW:" + id);
        Process process = getParentProcess(idToNode);
        List<SequenceFlow> flows = process.sequenceFlows.stream().filter(s -> id.equals(s.sourceRef)).collect(Collectors.toList());
        for (SequenceFlow flow : flows) {
            Node node = idToNode.get(flow.targetRef);
            if (node instanceof SolidityNode) {
                ((SolidityNode) node).toSolidity(template);
            }
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
}
