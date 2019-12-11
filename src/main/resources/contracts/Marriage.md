```solidity
pragma solidity ^0.4.24;
/// @title Marriage Contract
contract Marriage {

 mapping (address => uint) balances;
 address wife = address(0); // dummy address
 address husband = address(1); // dummy address
 function withdraw () {
 uint amount = balances[msg.sender];
 balances[msg.sender] = 0;
 msg.sender.transfer(amount);
 }
 function () payable {
 balances[wife] += msg.value / 2;
 balances[husband] += msg.value / 2;
 }
}
```



#### Sequence Diagram
```
sequenceDiagram
alt Create Contract
  Husband ->> Contract:Create Contract
else
  Wife ->> Contract:Create Contract
end
Contract -->> Contract:Set Husband and Wife Addresses
  Husband ->> Contract:Create Contract
Gifter ->> Contract:Send Funds
Contract -->> Contract:Increase Wife's Balance by (Fund / 2)
Contract -->> Contract:Increase Husband's Balance (Fund / 2)
 Wife ->> Contract:Withdraw 
Contract ->> Wife:Transfer Available Balance
Husband ->> Contract:Withdraw
Contract ->> Husband:Transfer Available Balance
```
