import com.github.rasika.bpmn2solidity.BPMN2SolidityParser;
import com.github.rasika.bpmn2solidity.exceptions.SolidityParserException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestContracts {
    public static void main(String[] args) throws IOException, SolidityParserException {
        String[] contractPaths =
                new String[]{"contracts/BasicToken.bpmn", "contracts/CrowdSale.bpmn", "contracts/Lottery.bpmn",
                        "contracts/Marriage.bpmn", "contracts/RentalAgreement.bpmn", "contracts/Sellable.bpmn"};

        for (String contractPath : contractPaths) {
            // Get path of BPMN XML
            Path bpmnXML = Paths.get("src/main/resources/").resolve(contractPath);

            // Translate to Solidity
            BPMN2SolidityParser.parse(bpmnXML, false);
        }
    }
}
