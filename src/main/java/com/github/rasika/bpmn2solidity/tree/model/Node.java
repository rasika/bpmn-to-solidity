package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.tree.SourcePosition;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Node {
    public String name;
    public String id;
    public String type;
    public String text;
    public Node parent;
    public SourcePosition position;
    private List<Pair<String, String>> attributes = new ArrayList<>();
    private List<Node> children = new ArrayList<>();

    public Node(String type, Node parent) {
        this.name = "";
        this.id = "";
        this.type = type;
        this.text = "";
        this.parent = parent;
    }

    public Node(Node node) {
        this.name = node.name;
        this.id = node.id;
        this.type = node.type;
        this.text = node.text;
        this.parent = node.parent;
        this.position = node.position;
        this.attributes = node.getAttributes();
        this.children = node.getChildren();
    }

    public void addChild(Node childNode) {
        children.add(childNode);
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addAttribute(Pair<String, String> attribute) {
        if ("name".equals(attribute.a)) {
            name = attribute.b;
        } else if ("id".equals(attribute.a)) {
            id = attribute.b;
        }
        attributes.add(attribute);
    }

    public List<Pair<String, String>> getAttributes() {
        return attributes;
    }

    public Optional<String> getAttribute(String name) {
        return attributes.stream().filter(s -> s.a.equals(name)).map(s -> s.b).findAny();
    }

    public Node getParent() {
        return parent;
    }

    public SourcePosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        if (name.isEmpty()) {
            return "{" + "type='" + type + '\'' + ", position=" + position + '}';
        }
        return "{" + "name='" + name + '\'' + ", type=" + type + '\'' + ", position=" + position + '}';
    }
}
