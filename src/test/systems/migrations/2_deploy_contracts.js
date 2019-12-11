const LoanApproval = artifacts.require("LoanApproval");

//const MultiPartyApproval = artifacts.require("MultiPartyApproval");

const ProcessRegistry = artifacts.require("ProcessRegistry");
const BPMNInterpreter = artifacts.require("BPMNInterpreter");
const BPM17_Running_ExampleFactory = artifacts.require("BPM17_Running_ExampleFactory");

module.exports = function(deployer) {
    deployer.deploy(LoanApproval, 1, 1, 1);

//    deployer.deploy(MultiPartyApproval);

    deployer.deploy(ProcessRegistry);
    deployer.deploy(BPMNInterpreter);
    deployer.deploy(BPM17_Running_ExampleFactory);

    return deployer;
};
