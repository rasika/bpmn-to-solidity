 const BasicToken = artifacts.require("BasicToken");
 const Crowdsale = artifacts.require("Crowdsale");
 const Marriage = artifacts.require("Marriage");
 const Lottery10Users = artifacts.require("Lottery10Users");
 const RentalAgreement = artifacts.require("RentalAgreement");
 const Sellable = artifacts.require("Sellable");
// const MultiPartyApproval = artifacts.require("MultiPartyApproval");

// const ProcessRegistry = artifacts.require("ProcessRegistry");
// const BPMNInterpreter = artifacts.require("BPMNInterpreter");
// const BPM17_Running_ExampleFactory = artifacts.require("BPM17_Running_ExampleFactory");

module.exports = function(deployer, network, accounts) {
    // deployer.deploy(LoanApproval, 1, 1, 1);

    // deployer.deploy(MultiPartyApproval);

    // deployer.deploy(ProcessRegistry);
    // deployer.deploy(BPMNInterpreter);
    // deployer.deploy(BPM17_Running_ExampleFactory);
    deployer.deploy(BasicToken, 1000);
    deployer.deploy(Crowdsale, accounts[1], 10, accounts[2], 60, 10);
    deployer.deploy(Marriage);
    deployer.deploy(Lottery10Users);
    deployer.deploy(RentalAgreement, 1000, "HouseA");
    deployer.deploy(Sellable);
    return deployer;
};
