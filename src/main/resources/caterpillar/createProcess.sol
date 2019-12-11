pragma solidity ^0.4.25;

import "AbstractFactory";
import "AbstractProcess";
import "AbstractRegistry";

contract Invoice_Handling_Factory is AbstractFactory {
    function newInstance(address parent, address processRegistry) public returns(address) {
        Invoice_Handling_Contract newContract = new Invoice_Handling_Contract(parent, worklist, processRegistry);
        return newContract;
    }

    function startInstanceExecution(address processAddress) public {
        Invoice_Handling_Contract(processAddress).startExecution();
    }
}


contract Invoice_Handling_Contract is AbstractProcess {

    uint public marking = uint(2);
    uint public startedActivities = 0;


    // Process Variables
    bool accepted;
    function Invoice_Handling_Contract(address _parent, address _worklist, address _processRegistry) public AbstractProcess(_parent, _worklist, _processRegistry) {
    }

    function startExecution() public {
        require(marking == uint(2) && startedActivities == 0);
        step(uint(2), 0);
    }

    function handleEvent(bytes32 code, bytes32 eventType, uint _instanceIndex, bool isInstanceCompleted) public {
        // Process without calls to external contracts.
        // No events to catch !!!
    }

    function killProcess() public {
        (marking, startedActivities) = killProcess(0, marking, startedActivities);
    }

    function killProcess(uint processElementIndex, uint tmpMarking, uint tmpStartedActivities) internal returns(uint, uint) {
        if(processElementIndex == 0)
            tmpMarking = tmpStartedActivities = 0;
        return (tmpMarking, tmpStartedActivities);
    }

    function broadcastSignal() public {
        var (tmpMarking, tmpStartedActivities) = broadcastSignal(marking, startedActivities, 0);
        step(tmpMarking, tmpStartedActivities);
    }

    function broadcastSignal(uint tmpMarking, uint tmpStartedActivities, uint sourceChild) internal returns(uint, uint) {
        return (tmpMarking, tmpStartedActivities);
    }


    function Issue_Invoice_complete(uint elementIndex) external {
        var (tmpMarking, tmpStartedActivities) = (marking, startedActivities);
        if(elementIndex == uint(1)) {
            require(msg.sender == worklist && tmpStartedActivities & uint(2) != 0);
            step(tmpMarking | uint(4), tmpStartedActivities & uint(~2));
            return;
        }
    }
    function Approve_Invoice_complete(uint elementIndex, bool _accepted) external {
        var (tmpMarking, tmpStartedActivities) = (marking, startedActivities);
        if(elementIndex == uint(2)) {
            require(msg.sender == worklist && tmpStartedActivities & uint(4) != 0);
           {accepted = _accepted;}
            step(tmpMarking | uint(16), tmpStartedActivities & uint(~4));
            return;
        }
    }
    function Fix_Invoice_complete(uint elementIndex) external {
        var (tmpMarking, tmpStartedActivities) = (marking, startedActivities);
        if(elementIndex == uint(6)) {
            require(msg.sender == worklist && tmpStartedActivities & uint(64) != 0);
            step(tmpMarking | uint(128), tmpStartedActivities & uint(~64));
            return;
        }
    }


    function step(uint tmpMarking, uint tmpStartedActivities) internal {
        while (true) {
            if (tmpMarking & uint(2) != 0) {
                Invoice_Handling_AbstractWorlist(worklist).Issue_Invoice_start(1);
                tmpMarking &= uint(~2);
                tmpStartedActivities |= uint(2);
                continue;
            }
            if (tmpMarking & uint(8) != 0) {
                Invoice_Handling_AbstractWorlist(worklist).Approve_Invoice_start(2);
                tmpMarking &= uint(~8);
                tmpStartedActivities |= uint(4);
                continue;
            }
            if (tmpMarking & uint(132) != 0) {
                tmpMarking &= uint(~132);
                tmpMarking |= uint(8);
                continue;
            }
            if (tmpMarking & uint(16) != 0) {
                tmpMarking &= uint(~16);
if (accepted)                tmpMarking |= uint(32);
else                 tmpMarking |= uint(64);
                continue;
            }
            if (tmpMarking & uint(32) != 0) {
                tmpMarking &= uint(~32);
                if (tmpMarking & uint(254) == 0 && tmpStartedActivities & uint(70) == 0) {
                    (tmpMarking, tmpStartedActivities) = propagateEvent("Invoice_Handling_Completed", "Default", tmpMarking, tmpStartedActivities, uint(32));
                }
                continue;
            }
            if (tmpMarking & uint(64) != 0) {
                Invoice_Handling_AbstractWorlist(worklist).Fix_Invoice_start(6);
                tmpMarking &= uint(~64);
                tmpStartedActivities |= uint(64);
                continue;
            }
            break;
        }
        if(marking != 0 || startedActivities != 0) {
            marking = tmpMarking;
            startedActivities = tmpStartedActivities;
        }
    }

    function getWorklistAddress() external view returns(address) {
        return worklist;
    }

    function getInstanceIndex() external view returns(uint) {
        return instanceIndex;
    }

}
pragma solidity ^0.4.25;

import "AbstractWorklist";

contract Invoice_Handling_AbstractWorlist {

      function Issue_Invoice_start(uint) external;
      function Approve_Invoice_start(uint) external;
      function Fix_Invoice_start(uint) external;
  
      function Issue_Invoice_complete(uint) external;
      function Approve_Invoice_complete(uint, bool) external;
      function Fix_Invoice_complete(uint) external;
  
}

contract Invoice_Handling_Worklist is AbstractWorklist {

    // Events with the information to include in the Log when a workitem is registered
    event Issue_Invoice_Requested(uint);
    event Approve_Invoice_Requested(uint);
    event Fix_Invoice_Requested(uint);

    function Issue_Invoice_start(uint elementIndex) external {
        workitems.push(Workitem(elementIndex, msg.sender));
        Issue_Invoice_Requested(workitems.length - 1);
    }
    function Approve_Invoice_start(uint elementIndex) external {
        workitems.push(Workitem(elementIndex, msg.sender));
        Approve_Invoice_Requested(workitems.length - 1);
    }
    function Fix_Invoice_start(uint elementIndex) external {
        workitems.push(Workitem(elementIndex, msg.sender));
        Fix_Invoice_Requested(workitems.length - 1);
    }

    function Issue_Invoice(uint workitemId) external {
        require(workitemId < workitems.length && workitems[workitemId].processInstanceAddr != address(0) && 
        canPerform(msg.sender, workitems[workitemId].processInstanceAddr, workitems[workitemId].elementIndex));
        Invoice_Handling_AbstractWorlist(workitems[workitemId].processInstanceAddr).Issue_Invoice_complete(workitems[workitemId].elementIndex);
        workitems[workitemId].processInstanceAddr = address(0);
    }
    function Approve_Invoice(uint workitemId, bool _accepted) external {
        require(workitemId < workitems.length && workitems[workitemId].processInstanceAddr != address(0) && 
        canPerform(msg.sender, workitems[workitemId].processInstanceAddr, workitems[workitemId].elementIndex));
        Invoice_Handling_AbstractWorlist(workitems[workitemId].processInstanceAddr).Approve_Invoice_complete(workitems[workitemId].elementIndex, _accepted);
        workitems[workitemId].processInstanceAddr = address(0);
    }
    function Fix_Invoice(uint workitemId) external {
        require(workitemId < workitems.length && workitems[workitemId].processInstanceAddr != address(0) && 
        canPerform(msg.sender, workitems[workitemId].processInstanceAddr, workitems[workitemId].elementIndex));
        Invoice_Handling_AbstractWorlist(workitems[workitemId].processInstanceAddr).Fix_Invoice_complete(workitems[workitemId].elementIndex);
        workitems[workitemId].processInstanceAddr = address(0);
    }

}