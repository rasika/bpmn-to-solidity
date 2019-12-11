pragma solidity ^0.4.25;

contract TaskRoleContract_Contract {

    function getRoleFromTask(uint taskIndex, bytes32 processId) public pure returns(uint) {
        if (processId == '5d741b93e931401a68852d79') {
            uint[7] memory I5d741b93e931401a68852d79 = [uint(0), 5, 6, 0, 0, 0, 5];
            if(taskIndex < 7)
                return I5d741b93e931401a68852d79[taskIndex];
        }
        return 0;
    }
}