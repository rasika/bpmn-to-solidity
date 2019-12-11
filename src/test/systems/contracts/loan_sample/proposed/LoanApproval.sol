pragma solidity >=0.4.0 <0.7.0;

// The original import should be as below, modified to make it work with truffle
// import github.com/oraclize/ethereum-api/oraclizeAPI.sol
import "./usingOraclize.sol";

contract LoanApproval is usingOraclize {

    mapping(bytes32 => uint) validIds;
    uint public cost;
    uint public loanAmount;
    uint public monthlyRevenue;
    uint loanRisk = 0;
    uint appraiseProperty = 0;

    event ConfirmationSent();
    event LoanRejected();
    event LoanAccepted();

    constructor (uint _cost, uint _loanAmount, uint _monthlyRevenue) {
        loanAmount = _loanAmount;
        cost = _cost;
        monthlyRevenue = _monthlyRevenue;

        bytes32 _queryId = 1;
        //        bytes32 _queryId = oraclize_query("URL", 'json(https://assess.loan.risk.url).result');
        validIds[_queryId] = 1;
        //        _queryId = oraclize_query("URL", 'json(https://assess.loan.risk.url).result');
        validIds[_queryId] = 2;

        if (loanRisk >= appraiseProperty){
            emit ConfirmationSent();
        } else {
            emit LoanRejected();
        }


        if (loanRisk >= appraiseProperty){
            emit ConfirmationSent();
        } else {
            emit LoanRejected();
        }
    }

    function confirmAcceptance(bool _confirmation) {
        if (_confirmation == true){
            emit LoanAccepted();
        }
    }

    function __callback(bytes32 _queryId, string memory _result) public {
        require(msg.sender == oraclize_cbAddress());
        if (validIds[_queryId] == 1) {
            appraiseProperty = parseInt(_result);
            delete validIds[_queryId];
        }
        if (validIds[_queryId] == 2) {
            loanRisk = parseInt(_result);
            delete validIds[_queryId];
        }
    }

}

