package com.github.rasika.bpmn2solidity.solidty;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.github.rasika.bpmn2solidity.tree.model.SolidityNode.TAB;

public class SolidityInstruction {

    protected String preText;
    protected String postText;
    public List<SolidityInstruction> subInstructions = new ArrayList<>();

    public SolidityInstruction(String preText) {
        this.preText = preText;
        this.postText = "";
    }

    public SolidityInstruction(String preText, String postText) {
        this.preText = preText;
        this.postText = postText;
    }

    public String render(String padding) {
        return getText(padding);
    }

    @Override
    public String toString() {
        return getText("    ");
    }

    private String getText(String padding) {
        StringBuilder rv = new StringBuilder();

        Scanner scanner = new Scanner(preText);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            rv.append(padding).append(line.trim()).append(System.lineSeparator());
        }
        scanner.close();

        for (SolidityInstruction inst : subInstructions) {
            rv.append(inst.render(padding + TAB));
        }

        scanner = new Scanner(postText);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            rv.append(padding).append(line.trim()).append(System.lineSeparator());
        }
        scanner.close();

        return rv.toString();
    }
}
