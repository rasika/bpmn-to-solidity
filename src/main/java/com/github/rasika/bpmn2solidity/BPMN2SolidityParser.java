package com.github.rasika.bpmn2solidity;

import com.github.rasika.bpmn2solidity.antlr4.BPMN2;
import com.github.rasika.bpmn2solidity.antlr4.BPMN2Lexer;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.tree.SourcePosition;
import com.github.rasika.bpmn2solidity.tree.TreeBuilder;
import com.github.rasika.bpmn2solidity.tree.model.DocumentNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;

public class BPMN2SolidityParser {
    public static String parse(Path filePath, boolean isVerbose) throws IOException, SolidityParserException {
        CharStream charStream = CharStreams.fromPath(filePath);
        long startTime = System.nanoTime();
        BPMN2Lexer lexer = new BPMN2Lexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        BPMN2 bpmn2 = new BPMN2(tokenStream);
        DocumentNode documentNode = TreeBuilder.createDocumentNode(filePath.getFileName(),
                                                                   new SourcePosition(filePath, 1, 1, 1, 1));
        bpmn2.addParseListener(new BPMN2Listener(tokenStream, documentNode, filePath));

        PrintStream oldOut = null;
        if(!isVerbose) {
            oldOut = System.out;
            System.setOut(new EmptyPrintStream());
        }
        bpmn2.document();
        String lines = documentNode.toSolidity();
        if(!isVerbose) {
            System.setOut(oldOut);
        }

        long stopTime = System.nanoTime();
        System.out.println(lines);
        System.out.println("--------------------------------------------------------");
        System.out.println("Time Taken: " + ((stopTime - startTime) / 1_000_000) + "ms");
        System.out.println("--------------------------------------------------------");
        return lines;
    }

    public static String unescapeXml(String xml) {
        xml = xml.replaceAll("\\<!\\[CDATA\\[", "");
        xml = xml.replaceAll("]]>", "");
        return StringEscapeUtils.unescapeXml(xml);
    }

    /**
     * Represents an empty print stream to avoid writing to the standard print stream.
     */
    public static class EmptyPrintStream extends PrintStream {
        public EmptyPrintStream() throws UnsupportedEncodingException {
            super(new OutputStream() {
                @Override
                public void write(int b) {
                }
            }, true, "UTF-8");
        }
    }
}
