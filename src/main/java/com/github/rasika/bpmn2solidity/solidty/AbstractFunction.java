package com.github.rasika.bpmn2solidity.solidty;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class AbstractFunction {
    protected final List<SolidityInstruction> instructions = new ArrayList<>();
    protected Stack<SolidityInstruction> parentInstruction = new Stack<>();

    public void addInstruction(SolidityInstruction instruction) {
        if (!parentInstruction.empty()) {
            parentInstruction.peek().subInstructions.add(instruction);
        } else {
            instructions.add(instruction);
        }
    }

    public void addParentInstruction(SolidityInstruction parentInstruction) {
        this.parentInstruction.push(parentInstruction);
    }

    public void clearLastParentInstruction() {
        if (!parentInstruction.empty()) {
            this.parentInstruction.pop();
        }
    }

    public void clearAllParentInstructions() {
        if (!parentInstruction.empty()) {
            this.parentInstruction.clear();
        }
    }

    public SolidityInstruction getCurrentParentInstruction() {
        if (parentInstruction.empty()) {
            return null;
        }
        return parentInstruction.peek();
    }

    public Stack<SolidityInstruction> getParentInstruction() {
        return parentInstruction;
    }
}
