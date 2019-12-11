package com.github.rasika.bpmn2solidity.solidty;

import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import static com.github.rasika.bpmn2solidity.tree.model.SolidityNode.TAB;

public class SolidityContract {
    protected final Map<String, SolidityFunction> nameToFunction = new LinkedHashMap<>();
    private final Map<String, SolidityEvent> nameToEvent = new LinkedHashMap<>();
    private SolidityConstructor constructor;
    private final List<SolidityGlobalVariable> stateVariables = new ArrayList<>();
    private final List<SolidityEnum> enums = new ArrayList<>();
    private final List<SolidityStruct> structs = new ArrayList<>();
    private final List<SolidityModifier> modifiersList = new ArrayList<>();
    private final List<String> inheritedContracts = new ArrayList<>();
    public final String contractName;

    SolidityContract(String name) {
        this.contractName = name;
    }

    public static SolidityContract build(String name) {
        if (name.toLowerCase().startsWith("interface ")) {
            return new SolidityInterface(getContractName(name));
        }
        return new SolidityContract(getContractName(name));
    }

    public void addConstructor(List<String> modifiers, List<String> params, Function<String, String> body) throws SolidityParserException {
        SolidityConstructor solidityConstructor = new SolidityConstructor(modifiers, params, body);
        if (constructor != null) {
            throw new SolidityParserException("A constructor with the same signature already exists! 'constructor(" + String.join(", ", params) + "){}'");
        }
        this.constructor = solidityConstructor;
    }

    public SolidityConstructor getConstructor() {
        return this.constructor;
    }

    public SolidityFunction addFunction(String name, List<String> modifiers, List<String> params, String returnType,
                                        Function<String, String> body, String documentation)
            throws SolidityParserException {
        SolidityFunction function = new SolidityFunction(name, modifiers, params, returnType, body, documentation);
        if (nameToFunction.values().contains(function)) {
            throw new SolidityParserException("A function with the same signature already exists! 'function " + name + "(" + String.join(", ", params) + ") " + ((returnType != null && !returnType.isEmpty()) ? "returns " + returnType : "") + "{}'");
        }
        nameToFunction.put(name, function);
        return function;
    }

    public SolidityFunction getFunction(String name) {
        return nameToFunction.get(name);
    }

    public void addEvent(String name, List<String> properties) {
        nameToEvent.put(name, new SolidityEvent(name, properties));
    }

    public void addStateVariable(String type, String name, String value, List<String> modifiers, String documentation) {
        stateVariables.add(new SolidityGlobalVariable(type, name, value, modifiers, documentation));
    }

    public void addEnum(String type, String name, String value, List<String> modifiers, String documentation) {
        enums.add(new SolidityEnum(type, name, value, modifiers, documentation));
    }

    public void addModifier(String name, List<String> params, Function<String, String> body, String documentation) {
        modifiersList.add(new SolidityModifier(name, params, body, documentation));
    }


    public boolean hasModifier(String name) {
        return modifiersList.stream().anyMatch(s->s.name.equals(name));
    }

    private static String getContractName(String name) {
        String result = "";
        if (name.toLowerCase().startsWith("contract ")) {
            result = name.substring(name.indexOf("contract ") + "contract ".length());
        } else if (name.toLowerCase().startsWith("interface ")) {
            result = name.substring(name.indexOf("interface ") + "interface ".length());
        } else {
            result = name;
        }
        return result.replaceAll("\\s", "");
    }

    public String render() {
        if (isEmpty()) {
            return "";
        }
        StringBuilder rv = new StringBuilder();
        rv.append("contract ").append(contractName).append(" ");
        if (!inheritedContracts.isEmpty()) {
            rv.append("is ").append(String.join(", ", inheritedContracts)).append(" ");
        }
        rv.append("{\n");
        if (!structs.isEmpty()) {
            rv.append("\n");
            for (SolidityStruct struct : structs) {
                rv.append(struct.render(TAB));
            }
        }
        if (!stateVariables.isEmpty()) {
            rv.append("\n");
            for (SolidityGlobalVariable variable : stateVariables) {
                rv.append(variable.render(TAB));
            }
        }
        if (!enums.isEmpty()) {
            rv.append("\n");
            for (SolidityEnum solidityEnum : enums) {
                rv.append(solidityEnum.render(TAB));
            }
        }
        if (!nameToEvent.isEmpty()) {
            rv.append("\n");
            for (SolidityEvent event : nameToEvent.values()) {
                rv.append(event.render());
            }
        }
        if (constructor != null) {
            rv.append("\n");
            rv.append(constructor.render());
        }
        if (!modifiersList.isEmpty()) {
            rv.append("\n");
            for (SolidityModifier modifier : modifiersList) {
                rv.append(modifier.render());
            }
        }
        if (!nameToFunction.isEmpty()) {
            rv.append("\n");
            for (SolidityFunction function : nameToFunction.values()) {
                rv.append(function.render());
            }
        }
        rv.append("}\n\n");
        return rv.toString();
    }

    private boolean isEmpty() {
        return constructor == null &&
                nameToFunction.isEmpty() &&
                stateVariables.isEmpty() &&
                structs.isEmpty() &&
                enums.isEmpty();
    }

    public void addInheritedContract(String inheritedContract) {
        inheritedContracts.add(inheritedContract);
    }

    public void addStruct(String name, List<String> subElements, List<String> modifiers) {
        SolidityStruct solidityStruct = new SolidityStruct(name, subElements, modifiers);
        structs.add(solidityStruct);
    }

    public static void renderDocs(String documentation, StringBuilder rv) {
        rv.append(TAB);
        rv.append("/*");
        Scanner scanner = new Scanner(documentation);
        boolean isFirstLine = true;
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            if (!isFirstLine) rv.append(TAB).append(" ");
            String line = scanner.nextLine();
            rv.append("* ").append(line.trim()).append(System.lineSeparator());
            isFirstLine = false;
            lineCount++;
        }
        if (lineCount == 1) {
            // remove last line feed
            rv.setLength(rv.length() - 1);
        } else {
            rv.append(TAB);
        }
        rv.append(" */").append(System.lineSeparator());
    }

    public static void renderDocsInline(String documentation, StringBuilder rv) {
        rv.append(TAB);
        Scanner scanner = new Scanner(documentation);
        boolean isFirstLine = true;
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            if (!isFirstLine) rv.append(TAB).append(" ");
            String line = scanner.nextLine();
            rv.append("// ").append(line.trim()).append(System.lineSeparator());
            isFirstLine = false;
            lineCount++;
        }
        if (lineCount == 1) {
            // remove last line feed
            rv.setLength(rv.length() - 1);
        } else {
            rv.append(TAB);
        }
        rv.append(System.lineSeparator());
    }
}
