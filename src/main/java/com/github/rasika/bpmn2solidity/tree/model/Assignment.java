package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.BPMN2SolidityParser;
import com.github.rasika.bpmn2solidity.tree.model.toplevel.VariableTypeDefinition;

import java.util.Map;

public class Assignment extends SolidityNode {
	private String fromRef;
	public String expression;
	private String returnTypeRef;
	public VariableTypeDefinition returnType;

	public Assignment(Node currentNode) {
		super(currentNode);
		getChildren().forEach(child -> {
			if ("from".equals(child.type)) {
				child.getAttributes().forEach(pair -> {
					if (pair.a.endsWith(":type") && pair.b.endsWith(":tFormalExpression")) {
						fromRef = child.text;
					}
				});
			} else if ("to".equals(child.type)) {
				child.getAttributes().forEach(pair -> {
					if (pair.a.endsWith(":type") && pair.b.endsWith(":tFormalExpression")) {
						expression = BPMN2SolidityParser.unescapeXml(child.text);
					} else if ("evaluatesToTypeRef".equals(pair.a)) {
						returnTypeRef = pair.b;
					}
				});
			}
		});
	}

	@Override
	public void relink(Map<String, Node> idToNode) {
		if (returnTypeRef != null) {
			returnType = (VariableTypeDefinition) idToNode.get(returnTypeRef);
		}
	}

	@Override
	public String toSolidity() {
		return expression;
	}
}
