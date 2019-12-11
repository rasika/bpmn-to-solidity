pragma solidity >=0.4.0 <0.7.0;

contract RentalAgreement {

    struct PaidRent {
        uint id;
        uint value;
    }

    PaidRent public paidrents;
    uint public createdTimestamp;
    uint public rent;
    string public house;
    address public landloard;
    State public state;
    address public tenant;

    enum public State {Created, Started, Terminated};

    event PaidRent();
    event AgreementConfirmed();
    event ContractTerminated();

    constructor (uint _rent, string _house) {
        rent = _rent;
        house = _house;
        landloard = msg.sender;
        createdTimestamp = block.timestamp;
    }

    modifier onlyLandlord() {
        if (msg.sender != tenant) _;

    }

    modifier inState(State.Started)() {
        if (state == _state) _;

    }

    modifier onlyTenant() {
        if (msg.sender == tenant) _;

    }

    modifier inState(State.Created)() {
        if (state == _state) _;

    }


    /** Confirm the lease agreement as tenant */
    function confirmAgreement() public inState(State.Created) require(msg.sender != landlord) {
        emit AgreementConfirmed();
    }

    /** Terminate the contract so the tenant can’t pay rent anymore, and the contract is terminated */
    function terminateContract() public onlyLandlord {
        emit ContractTerminated();

        landlord.send(this.balance);
        /* If there is any value on the contract send it to the landlord*/
        state = State.Terminated;
    }

    function payRent() public inState(State.Started) onlyTenant require(msg.value == rent) {
        emit PaidRent();

        landlord.send(msg.value);
        paidrents.push(PaidRent({
        id : paidrents.length + 1,
        value : msg.value
        }));
    }

}