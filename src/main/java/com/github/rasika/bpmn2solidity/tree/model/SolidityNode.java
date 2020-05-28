package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.Parser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCode;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;
import com.github.rasika.bpmn2solidity.tree.model.task.Task;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class SolidityNode extends Node implements SolidityCodeBlock {
    private Task topLevelTask = null;
    protected String documentation = "";
    public static final String TAB = "    ";

    protected Map<String, SolidityNode> nextItems = new LinkedHashMap<>();

    public SolidityNode(String type, Node parent) {
        super(type, parent);
    }

    public SolidityNode(Node node) {
        super(node);
        getChildren().forEach(child -> {
            if ("documentation".equals(child.type)) {
                documentation = Parser.unescapeXml(child.text);
            }
        });
    }

    @Override
    public String toSolidity() throws SolidityParserException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toSolidity(SolidityCode code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toSolidity(SolidityContract solidityContract) {
        throw new UnsupportedOperationException();
    }

    public void relink(Map<String, Node> idToNode) {
        relinkNextItem(idToNode);
    }

    protected boolean isTopLevelTask() {
        Node parent = getParent();
        while (parent != null) {
            if ("process".equals(parent.type)) {
                // rooted all the way upto process
                // top-level task should be a task
                return ("task".equals(type) || "scriptTask".equals(type));
            } else if ("task".equals(parent.type) || "scriptTask".equals(parent.type)) {
                // no intermediate tasks
                return false;
            }
            parent = parent.getParent();
        }
        return false;
    }

    protected Task findTopLevelTask(String ref, Process process, Map<String, Node> idToNode) {
        Optional<SequenceFlow> optIncomingFlow = process.sequenceFlows.stream().filter(s -> ref.equals(s.targetRef)).findFirst();
        if (!optIncomingFlow.isPresent()) return null;
        Node node = idToNode.get(optIncomingFlow.get().sourceRef);
        if (node == null) return null;
        if (node instanceof Task) {
            if (((Task) node).isTopLevelTask()) {
                return (Task) node;
            }
        }
        return findTopLevelTask(node.id, process, idToNode);
    }

    protected Process getParentProcess(Map<String, Node> idToNode) {
        Node node = getParent();
        while (node != null) {
            if ("process".equals(node.type)) {
                return (Process) idToNode.get(node.id);
            }
            node = node.getParent();
        }
        return (Process) node;
    }

    public Task getTopLevelTask() {
        return topLevelTask;
    }

    public void setTopLevelTask(Task topLevelTask) {
        this.topLevelTask = topLevelTask;
    }

    protected void relinkNextItem(Map<String, Node> idToNode) {
        if (getTopLevelTask() == null && isTopLevelTask()) {
            setTopLevelTask((Task) this);
        }
        Process process = getParentProcess(idToNode);
        process.sequenceFlows.stream().filter(f -> id.equals(f.sourceRef)).forEach(s -> {
            Node node = idToNode.get(s.targetRef);
            if (node instanceof SolidityNode) {
                SolidityNode solidityNode = (SolidityNode) node;
                solidityNode.setTopLevelTask(topLevelTask);
                nextItems.put(solidityNode.id, solidityNode);
            }
        });
        for (SolidityNode node : nextItems.values()) {
            node.relink(idToNode);
        }
    }

    protected void getNextItemsSolidity(SolidityCodeTemplate template) throws SolidityParserException {
        for (SolidityNode node : nextItems.values()) {
            node.toSolidity(template);
        }
    }

    public void validate(Map<String, Node> idToNode) throws SolidityParserException {
        boolean outgoingFound = idToNode.entrySet().stream().anyMatch(s -> {
            if (s.getValue() instanceof SequenceFlow) {
                SequenceFlow flow = (SequenceFlow) s.getValue();
                return (id.equals(flow.sourceRef));
            }
            return false;
        });
        boolean incomingFound = idToNode.entrySet().stream().anyMatch(s -> {
            if (s.getValue() instanceof SequenceFlow) {
                SequenceFlow flow = (SequenceFlow) s.getValue();
                return (id.equals(flow.targetRef));
            }
            return false;
        });
        if (!outgoingFound && !(this instanceof EndEvent)) {
            throw new SolidityParserException(this.type + ": " + id + " has no outgoing connections");
        }
        if (!incomingFound && !(this instanceof StartEvent)) {
            throw new SolidityParserException(this.type + ": " + id + " has no incoming connections");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SolidityNode)) {
            return false;
        }
        SolidityNode user = (SolidityNode) o;
        return user.id.equals(id);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id.hashCode();
        return result;
    }
}
