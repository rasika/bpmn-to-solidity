package com.github.rasika.bpmn2solidity;

import java.net.URISyntaxException;
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
            Parser.parse(Paths.get(Main.class.getClassLoader().getResource("contracts/BasicToken.bpmn").toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
