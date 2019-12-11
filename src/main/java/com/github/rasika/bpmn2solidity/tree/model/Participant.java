package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;

import java.util.Map;
import java.util.regex.Pattern;

public class Participant extends SolidityNode {
    private String processRef;
    private Process process;
    private Pattern contractPattern = Pattern.compile("Contract\\s+(.*)");

    public Participant(Node currentNode) {
        super(currentNode);
        getAttributes().forEach(pair -> {
            if ("processRef".equals(pair.a)) {
                this.processRef = pair.b;
            }
        });
    }

    @Override
    public void relink(Map<String, Node> idToNode) {
        if (processRef != null) {
            Process process = (Process) idToNode.get(processRef);
            process.relink(idToNode);
            this.process = process;
        }
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        if (process != null) {
            System.out.println("// --Participant: " + name);
            SolidityContract contract = SolidityContract.build(name);
            template.addContract(contract.contractName, contract);
            process.contractName = contract.contractName;
            process.toSolidity(template);
//			Matcher matcher = contractPattern.matcher(name);
//			if (matcher.find() && matcher.groupCount() > 0) {
//				String contractName = matcher.group(1);
//				template.addContract(contractName, new SolidityContract(contractName));
//				process.contractName = contractName;
//				process.toSolidity(template);
//			}
        }
    }
}
