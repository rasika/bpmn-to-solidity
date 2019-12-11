package com.github.rasika.bpmn2solidity.tree;

import java.nio.file.Path;

public class SourcePosition {
    private Path src;
    private int sLine;
    private int eLine;
    private int sCol;
    private int eCol;

    public SourcePosition(Path src, int sLine, int eLine, int sCol, int eCol) {
        this.src = src;
        this.sLine = sLine;
        this.eLine = eLine;
        this.sCol = sCol;
        this.eCol = eCol;
    }

    public Path getSrc() {
        return src;
    }

    public int getStartLine() {
        return sLine;
    }

    public int getEndLine() {
        return eLine;
    }

    public int getStartCol() {
        return sCol;
    }

    public int getEndCol() {
        return eCol;
    }

    @Override
    public String toString() {
        return "{" + src.getFileName().toString() +
                ", " + sLine +
                "," + eLine +
                " - " + sCol +
                "," + eCol +
                '}';
    }
}
