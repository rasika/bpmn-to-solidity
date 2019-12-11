package com.github.rasika.bpmn2solidity.solidty;

import java.util.ArrayList;
import java.util.List;

public class SolidityIfElseInstruction extends SolidityInstruction {

    private List<SolidityIfElseInstruction> elseIfStmts = new ArrayList<>();
    private SolidityInstruction elseStmt;

    public SolidityIfElseInstruction(String ifCond) {
        super("if (" + ifCond + ")" + " {", "}");
        this.elseStmt = null;
    }

    public void addElseIfStmt(SolidityIfElseInstruction elseIfStmt) {
        this.elseIfStmts.add(elseIfStmt);
    }

    public void setElseStmt(SolidityInstruction elseStmt) {
        this.elseStmt = elseStmt;
    }

    public String render(String padding) {
        for (SolidityIfElseInstruction elseIfStmt : elseIfStmts) {
            String render = elseIfStmt.render(padding);
            postText += (render.isEmpty()) ? "" : " else " + render;
        }
        if (elseStmt != null) {
            String render = elseStmt.render(padding);
            postText += (render.isEmpty()) ? "" : " else {" + System.lineSeparator() + render + "}";
        }
        return super.render(padding);
    }

    private String trimStart(String value) {
        // Remove leading spaces.
        return value.replaceFirst("^\\s+", "");
    }
}
