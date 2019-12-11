pragma solidity ^0.4.24;

// Represents the base of a unibright contract
contract UnibrightContract {

    // the owner of the contract
    address public owner;

    // state of the contract
    ContractState public contractState;

    // states the contract can be in
    enum ContractState { CREATED, PUBLISHED, RUNNING, CANCELLED, FINISHED }

    // A modifier that can limit the execution of a function to a specific address
    modifier onlyBy(address _account) {
        require(msg.sender == _account, "Not allowed");
        _;
    }

    // A modifier that can limit the execution of a function to a specific contract state
    modifier onlyInState(ContractState state) {
        require(contractState == state, string(abi.encodePacked("Only allowed when contract is in state ", state)));
        _;
    }

    constructor() public {
        owner = msg.sender;
    }

    // publish is called by the owner of the contract to progress to the state PUBLISHED
    function publish() public onlyBy(owner) {
        contractState = ContractState.PUBLISHED;
    }

    // start is called by the owner of the contract to progress to the state RUNNING
    function start() public onlyBy(owner) {
        contractState = ContractState.RUNNING;
    }

    // cancel is called by the owner of the contract to progress to the state cancelled
    function cancel() public onlyBy(owner) {
        contractState = ContractState.CANCELLED;
    }

    // finish is called to progress to the state FINISHED
    function finish() private {
        contractState = ContractState.FINISHED;
    }
}

