pragma solidity >=0.4.0 <0.7.0;

contract Marriage {

    mapping (address => uint) balances;
    address wife = address(0);
    address husband = address(1);

    function withdraw() {
        uint amount = balances[msg.sender];
        balances[msg.sender] = 0;
        msg.sender.transfer(amount);
    }

    function () payable {
        balances[wife] += msg.value / 2;
        balances[husband] += msg.value / 2;
    }

}
