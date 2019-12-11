package com.github.rasika.bpmn2solidity.tree.model.gateway;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import com.github.rasika.bpmn2solidity.tree.model.SolidityNode;

import java.util.Map;

public class EventBasedGateway extends SolidityNode {

	public EventBasedGateway(Node currentNode) {
		super(currentNode);
	}

	@Override
	public void relink(Map<String, Node> idToNode) {
		relinkNextItem(idToNode);
	}

	@Override
	public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
		System.out.println("// ------EventBasedGW:" + id);
		getNextItemsSolidity(template);
	}
}
