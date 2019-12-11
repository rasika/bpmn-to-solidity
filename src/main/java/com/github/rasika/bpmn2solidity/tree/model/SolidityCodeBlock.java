package com.github.rasika.bpmn2solidity.tree.model;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;
import com.github.rasika.bpmn2solidity.solidty.SolidityCode;
import com.github.rasika.bpmn2solidity.solidty.SolidityCodeTemplate;
import com.github.rasika.bpmn2solidity.solidty.SolidityContract;

public interface SolidityCodeBlock {
    String toSolidity() throws SolidityParserException;

    String toSolidity(SolidityContract solidityContract);

    String toSolidity(SolidityCode solidityCode);

    void toSolidity(SolidityCodeTemplate template) throws SolidityParserException;
}
