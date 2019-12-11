package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.tree.SourcePosition;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DocumentNode extends SolidityNode {
    private List<Node> topLevelNodes;

    public DocumentNode(Path fileName, SourcePosition sourcePosition) {
        super("parent", null);
        this.position = sourcePosition;
        this.topLevelNodes = new ArrayList<>();
        this.name = fileName.toString();
    }

    public void addTopLevelNode(Node node) {
        this.topLevelNodes.add(node);
    }

    public List<Node> getTopLevelNodes() {
        return topLevelNodes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("'").append(this.name).append("' -> Top Level Elements:-\n");
        this.getTopLevelNodes().forEach(e -> builder.append("\t").append(e).append("\n"));
        return builder.toString();
    }

    @Override
    public String toSolidity() throws SolidityParserException {
        StringBuilder rv = new StringBuilder();
        for (Node node : topLevelNodes) {
            if (node instanceof SolidityCodeBlock) {
                SolidityCodeTemplate template = new SolidityCodeTemplate();
                ((SolidityCodeBlock) node).toSolidity(template);
                rv.append(template.render()).append("\n");
            }
        }
        return rv.toString();
    }
}
