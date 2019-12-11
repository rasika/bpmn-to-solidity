pragma solidity >=0.4.0 <0.7.0;

contract Crowdsale {

    address public beneficiary;
    uint public deadline;
    token public tokenReward;
    uint public price;
    uint public fundingGoal;
    bool crowdsaleClosed = false;
    bool fundingGoalReached = false;

    event FundTransfer(address beneficiary, uint amountRaised, bool fundingGoalReached);
    event GoalReached(uint beneficiary, uint amountRaised);

    constructor (address ifSuccessfulSendTo, uint fundingGoalInEthers, address addressOfTokenUsedAsReward, uint durationInMinutes, uint etherCostOfEachToken) {
        beneficiary = ifSuccessfulSendTo;
        fundingGoal = fundingGoalInEthers * 1 ether;
        deadline = now + durationInMinutes * 1 minutes;
        price = etherCostOfEachToken * 1 ether;
        tokenReward = token(addressOfTokenUsedAsReward);
    }

    modifier afterDeadline() {
        if (now >= deadline) _;

    }


    function() public payable {
        require(!crowdsaleClosed);
        uint amount = msg.value;
        balanceOf[msg.sender] += amount;
        amountRaised += amount;
        tokenReward.transfer(msg.sender, amount / price);
        emit FundTransfer(beneficiary, amountRaised, false);
    }

    function safeWithdrawal() public {
        if (!fundingGoalReached) {
            uint amount = balanceOf[msg.sender];
            balanceOf[msg.sender] = 0;
            if (amount > 0) {
                if (msg.sender.send(amount)) {
                    emit FundTransfer(beneficiary, amountRaised, false);
                } else {
                    balanceOf[msg.sender] = amount;
                }
            }
        }

        if (fundingGoalReached && beneficiary == msg.sender) {
            if (beneficiary.send(amountRaised)) {
                emit FundTransfer(beneficiary, amountRaised, false);
            } else {
                fundingGoalReached = true;
            }
        }
    }

    function checkGoalReached() public afterDeadline {
        if (amountRaised >= fundingGoal) {
            fundingGoalReached = true
            }

            emit GoalReached(beneficiary, amountRaised);

            crowdsaleClosed = true;
        }

    }

interface token {
    function transfer(address receiver, uint amount) external;
}
