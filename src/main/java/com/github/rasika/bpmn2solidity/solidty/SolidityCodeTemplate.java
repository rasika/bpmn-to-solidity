package com.github.rasika.bpmn2solidity.solidty;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolidityCodeTemplate {
    private final Map<String, SolidityContract> nameToContract = new HashMap<>();
    private final Map<String, SolidityInterface> nameToInterface = new HashMap<>();
    private final List<String> imports = new ArrayList<>();

    public void addContract(String name, SolidityContract contract) throws SolidityParserException {
        if (nameToContract.keySet().contains(contract.contractName)) {
            throw new SolidityParserException("A contract with the name '" + contract.contractName + "' already exists!");
        }
        nameToContract.put(name, contract);
    }

    public SolidityContract getContract(String name) {
        return nameToContract.get(name);
    }

    public String render() {
        StringBuilder rv = new StringBuilder();
        rv.append("--------------------------------------------------------------\n");
        rv.append("pragma solidity >=0.4.0 <0.7.0;\n\n");
        if (!imports.isEmpty()) {
            for (String importLink : imports) {
                rv.append("import \"").append(importLink).append("\";").append(System.lineSeparator());
            }
            rv.append("\n");
        }
        for (SolidityContract contract : nameToContract.values()) {
            rv.append(contract.render());
        }
        return rv.toString();
    }

    public void addImport(String importLink) {
        imports.add(importLink);
    }
}
