package com.github.rasika.bpmn2solidity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
//            contracts/BasicToken.bpmn
//            contracts/CrowdSale.bpmn
//            contracts/Lottery.bpmn
//            contracts/Marriage.bpmn
//            contracts/RentalAgreement.bpmn
//            contracts/Sellable.bpmn
            String contractPath = "contracts/Sellable.bpmn";

            // Get path of BPMN XML
            Path bpmnXML = Paths.get(Main.class.getClassLoader().getResource(contractPath).toURI());

            // Translate to Solidity
            String solidityCode = BPMN2SolidityParser.parse(bpmnXML, false);

            // Save to file
            writeToFile(getSolidityPath(bpmnXML), solidityCode);

            // Save to truffle
//            writeToTruffle(contractPath, solidityCode);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(Path solidityPath, String content) throws IOException {
        Files.write(solidityPath, content.getBytes());
        System.out.println("File wrote to " + solidityPath.toString());
    }

    private static Path getSolidityPath(Path bpmnXML) {
        String fileName = bpmnXML.toFile().getName().substring(0, bpmnXML.toFile().getName().lastIndexOf(".") + 1);
        return bpmnXML.getParent().resolve(fileName + "sol");
    }

    private static void writeToTruffle(String relativePath, String content) throws IOException {
        Path path = Paths.get("src/test/solidity/").resolve(relativePath);
        Files.write(path, content.getBytes());
        System.out.println("File wrote to " + path.toString());
    }
}
