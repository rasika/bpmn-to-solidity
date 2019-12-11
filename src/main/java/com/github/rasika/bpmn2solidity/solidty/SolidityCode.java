package com.github.rasika.bpmn2solidity.solidty;

import java.util.HashMap;
import java.util.Map;

public class SolidityCode {
    public final Map<String, SolidityContract> nameToContract = new HashMap<>();
    public final Map<String, SolidityInterface> nameToInterface = new HashMap<>();
}
