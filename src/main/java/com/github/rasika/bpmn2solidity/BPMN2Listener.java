// Generated from ../resources/BPMN2.g4 by ANTLR 4.7.2
package com.github.rasika.bpmn2solidity;

import com.github.rasika.bpmn2solidity.antlr4.BPMN2;
import com.github.rasika.bpmn2solidity.antlr4.BPMN2BaseListener;
import com.github.rasika.bpmn2solidity.tree.DocumnetBuilder;
import com.github.rasika.bpmn2solidity.tree.model.DocumentNode;
import com.github.rasika.bpmn2solidity.tree.model.Node;
import org.antlr.v4.runtime.CommonTokenStream;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * This class provides an empty implementation of {@link com.github.rasika.bpmn2solidity.antlr4.BPMN2Listener}, which can be extended to create
 * a listener which only needs to handle a subset of the available methods.
 */
public class BPMN2Listener extends BPMN2BaseListener {
    private final DocumentNode documentNode;
    private final Path filePath;
    private final DocumnetBuilder documnetBuilder;
    private boolean afterProlog = false;

    public BPMN2Listener(CommonTokenStream tokenStream, DocumentNode documentNode, Path filePath) {
        this.documentNode = documentNode;
        this.filePath = filePath;
        this.documnetBuilder = new DocumnetBuilder(documentNode, tokenStream);
    }

    @Override
    public void exitElement(BPMN2.ElementContext ctx) {
        int a = 10;
    }

    @Override
    public void exitDocument(BPMN2.DocumentContext ctx) {
        documnetBuilder.relink();
        Set<String> names = new HashSet<>();
        for(Node node : documnetBuilder.getIdToNode().values()){
            names.add(node.getClass().getSimpleName());
        }
        String namesStr = String.join(", ", names);
        System.out.println("// BPMN Elements:" + namesStr);
    }

    @Override
    public void exitProlog(BPMN2.PrologContext ctx) {
        afterProlog = true;
    }

    @Override
    public void enterBlockElement(BPMN2.BlockElementContext ctx) {
        documnetBuilder.startBlockElement(ctx);
    }

    @Override
    public void exitBlockElement(BPMN2.BlockElementContext ctx) {
        documnetBuilder.endBlockElement(ctx);
    }

    @Override
    public void enterSelfClosingElement(BPMN2.SelfClosingElementContext ctx) {
        documnetBuilder.startSelfClosingElement(ctx);
    }

    @Override
    public void exitSelfClosingElement(BPMN2.SelfClosingElementContext ctx) {
        documnetBuilder.endSelfClosingElement(ctx);
    }

    @Override
    public void exitAttribute(BPMN2.AttributeContext ctx) {
        if (!afterProlog) {
            return;
        }
        documnetBuilder.addAttribute(ctx);
    }
}
