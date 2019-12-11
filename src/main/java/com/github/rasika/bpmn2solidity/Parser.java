package com.github.rasika.bpmn2solidity;

import com.github.rasika.bpmn2solidity.antlr4.BPMN2;
import com.github.rasika.bpmn2solidity.antlr4.BPMN2Lexer;
import com.github.rasika.bpmn2solidity.tree.SourcePosition;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;
import com.github.rasika.bpmn2solidity.tree.model.DocumentNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang3.StringEscapeUtils;

import java.nio.file.Path;

public class Parser {
    public static void parse(Path filePath) {
        try {
            CharStream charStream = CharStreams.fromPath(filePath);
            long startTime = System.nanoTime();
            BPMN2Lexer lexer = new BPMN2Lexer(charStream);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            BPMN2 bpmn2 = new BPMN2(tokenStream);
            DocumentNode documentNode = TreeBuilder.createDocumentNode(filePath.getFileName(),
                    new SourcePosition(filePath, 1, 1, 1, 1));
            bpmn2.addParseListener(new BPMN2Listener(tokenStream, documentNode, filePath));
            bpmn2.document();

            String lines = documentNode.toSolidity();
            long stopTime = System.nanoTime();
            System.out.println("--------------------------------------------------------");
            System.out.println("Time Taken: " + ((stopTime - startTime) / 1_000_000) + "ms");
            System.out.println(lines);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String unescapeXml(String xml) {
        xml = xml.replaceAll("\\<!\\[CDATA\\[", "");
        xml = xml.replaceAll("]]>", "");
        return StringEscapeUtils.unescapeXml(xml);
    }
}
