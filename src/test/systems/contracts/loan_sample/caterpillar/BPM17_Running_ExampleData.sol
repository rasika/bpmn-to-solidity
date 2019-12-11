pragma solidity ^0.4.25;

import './IData.sol';

contract BPM17_Running_ExampleData is IDataImp {
    bool applicantEligible;
    uint monthlyRevenue;
    uint loanAmount;
    uint cost;
    uint appraisePropertyResult;
    uint assessLoanRiskResult;

    function executeScript(uint eInd) public returns(uint) {
        if (eInd == 4) {
            if (assessLoanRiskResult >= appraisePropertyResult)
                applicantEligible = true;
            else
                applicantEligible = false;
            return 8;
        }
        if (eInd == 5) {
            if (applicantEligible)
                return 16;
        }
        if (eInd == 12) {
            if (applicantEligible)
                return 4096;
        }
    }

    function checkIn(uint eInd, uint i1, uint i2, uint i3) public {
        require (4 & (1 << eInd) != 0);
        monthlyRevenue = i3; loanAmount = i2; cost = i1;
        continueExecution(eInd);
    }

    function checkIn(uint eInd, uint i1) public {
        require (1032 & (1 << eInd) != 0);

        if (eInd == 3) {
            assessLoanRiskResult = i1;
        }
        else if (eInd == 10) {
            appraisePropertyResult = i1;
        }
        continueExecution(eInd);
    }

    function checkIn(uint eInd, bool i1) public {
        require (2048 & (1 << eInd) != 0);
        applicantEligible = i1;
        continueExecution(eInd);
    }

    function checkOut(uint eInd) public view  {
        require (3084 & (1 << eInd) == 0);
    }

}