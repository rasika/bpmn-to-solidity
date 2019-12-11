https://github.com/pbrudny/learning-solidity-2018

```solidity
pragma solidity ^0.4.23;

contract Lottery10Users {
    address[10] participants;
    uint8 participantsCount = 0;
    uint randNonce = 0;

    function join() public payable {
        require(msg.value == 0.1 ether, "Must send 0.1 ether");
        require(participantsCount < 10, "User limit reached");
        require(joinedAlready(msg.sender) == false, "User already joined");
        participants[participantsCount] = msg.sender;
        participantsCount++;
        if (participantsCount == 10) {
            selectWinner();
        }
    }
    
    function joinedAlready(address _participant) private view returns(bool) {
        bool containsParticipant = false;
        for(uint i = 0; i < 10; i++) {
            if (participants[i] == _participant) {
                containsParticipant = true;
            }
        }
        return containsParticipant;
    }
    
    function selectWinner() private returns(address) {
        require(participantsCount == 10, "Waiting for more users");
        address winner = participants[randomNumber()];
        winner.transfer(address(this).balance);
        delete participants;
        participantsCount = 0;
        return winner;
    }
    
    function randomNumber() private returns(uint) {
        uint rand = uint(keccak256(abi.encodePacked(now, msg.sender, randNonce))) % 10;
        randNonce++;
        return rand;
    }
        
}
```

#### Lottery10Users
```solidity
sequenceDiagram
Owner ->> Contract: Create Contract
loop loop
Participants ->>Contract: Join
Contract->>Contract: Check Transfer Amount is 0.1 either?
Contract->>Contract: Check User limit is reached?
Contract->>Contract: Check Already joined?
Contract->>Contract: Add User
alt if User limit is 10
Contract->>Contract: SelectWinner
Contract->>Participants: Transfer Money to Winner
Contract->>Contract: Remove All Participants
end
end
```
