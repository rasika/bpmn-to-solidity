pragma solidity ^0.4.24;

import "./UnibrightContract.sol";

contract MultiPartyApproval is UnibrightContract {

    // the status of the whole approval process
    Status public approvalStatus;

    // holds the timestamp of the last update
    uint public lastUpdate;

    // states the contract and the feedbacks of the approvers can be in
    enum Status { UNDEFINED, NEEDSWORK, APPROVED }

    // uuid of the end node
    bytes16 private endId;

    // map of uuids to approvers, filled during the deployment
    mapping(bytes16 => Approver) private approvers;

    // map of uuids to feedbacks, filled during the deployment
    mapping(bytes16 => Feedback) private feedbacks;

    // map of addresses to uuids, filled during the setup phase
    mapping(address => bytes16) private addresses;

    // Approver holds information about an Approver
    struct Approver {
        Status status;          // the status of the approval
        string comment;         // a description related to the approval status
        uint approvalDate;      // time at which the approver gave his approval

        uint feedbacksNeeded;   // remaining number of feedbacks needed for this approver to give his approval
        bytes16 feedbackId;     // the id of the feedback object attached to this approver

        bool exists;            // required to check if mapping exists
    }

    // Feedback holds information about the feedback given by an group of approvers
    struct Feedback {
        Status status;          // the status of the feedback node

        uint approvalsGiven;    // number of approvals given for this feedback node
        uint approvalsNeeded;   // number of approvals needed for this feedback node to set its status
        bytes16[] approverIds;  // ids of successor approvers attached to the feedback

        bool exists;            // required to check if mapping exists
    }

    constructor() public {
        approvalStatus = Status.UNDEFINED;
        publish();

        start();
    }

    // Adds the Approver node to the MPA network that represents the end of the process
    function addEndNode(bytes16 endNodeId, uint feedbacksNeeded) public onlyBy(owner) onlyInState(ContractState.PUBLISHED) {
        addApprover(endNodeId, 0, feedbacksNeeded);
        endId = endNodeId;
    }

    // Add an approver to the MPA network
    function addApprover(bytes16 approverId, bytes16 feedbackId, uint feedbacksNeeded) public
    onlyBy(owner) onlyInState(ContractState.PUBLISHED) {

        require(!approvers[approverId].exists, "approver already exists");

        Approver memory approver = Approver({
            status: Status.UNDEFINED,
            comment: "",
            approvalDate: 0,
            feedbackId: feedbackId,
            feedbacksNeeded: feedbacksNeeded,
            exists: true
            });
        approvers[approverId] = approver;
    }

    // Set the address for an approver
    function setApproverAddress(address approverAddress, bytes16 approverId) public onlyBy(owner) onlyInState(ContractState.PUBLISHED) {
        require(approvers[approverId].exists, "no approver with given id found");
        addresses[approverAddress] = approverId;
    }

    // returns the approver for a given uuid
    function getApprover(bytes16 id) public view returns (Status, string, uint, uint, bytes16) {
        Approver memory approver = approvers[id];
        require(approver.exists, "this approver does not exist.");
        return (
        approver.status,
        approver.comment,
        approver.approvalDate,
        approver.feedbacksNeeded,
        approver.feedbackId
        );
    }

    // Add a feedback node to the MPA network
    function addFeedback(bytes16 feedbackId, uint approvalsNeeded, bytes16[] approverIds) public
    onlyBy(owner) onlyInState(ContractState.PUBLISHED) {

        require(!feedbacks[feedbackId].exists, "a feedback object with this id already exists");

        Feedback memory feedback = Feedback({
            status: Status.UNDEFINED,
            approvalsGiven: 0,
            approvalsNeeded: approvalsNeeded,
            approverIds: approverIds,
            exists: true
            });
        feedbacks[feedbackId] = feedback;
    }

    // returns the feedback note for the given uuid
    function getFeedback(bytes16 id) public view returns (Status, uint, uint, bytes16[]) {
        require(feedbacks[id].exists, "this feedbackId does not exist.");
        return (feedbacks[id].status, feedbacks[id].approvalsGiven, feedbacks[id].approvalsNeeded, feedbacks[id].approverIds);
    }

    // the actual method called by the approvers to give their feedback
    function giveFeedback(Status status, string comment) public onlyInState(ContractState.RUNNING) {

        Approver storage approver = approvers[addresses[msg.sender]];
        require(approver.exists, "not allowed to provide feedback");
        require(approver.feedbacksNeeded == 0, "not allowed to provide approval yet");

        require(status != Status.UNDEFINED, "approval status cannot be set to undefined");
        require(approver.status == Status.UNDEFINED, "approval already submitted");

        Feedback storage feedback = feedbacks[approver.feedbackId];
        require(feedback.status != Status.NEEDSWORK, "feedback already set");
        require(feedback.approvalsGiven < feedback.approvalsNeeded, "feedback already set");

        approver.status = status;
        approver.comment = comment;
        approver.approvalDate = now;
        lastUpdate = now;

        // if any approver gives the state NEEDSWORK the contract is finished
        if (status == Status.NEEDSWORK) {
            feedback.status = status;
            approvalStatus = status;
            finish();
            return;
        }

        feedback.approvalsGiven++;

        // if enough approvals have been given for this feedback, reduce the feedbacksNeeded counter further down the MPANet
        if (feedback.approvalsGiven == feedback.approvalsNeeded) {
            feedback.status = status;

            for (uint i = 0; i < feedback.approverIds.length; i++) {
                bytes16 id = feedback.approverIds[i];
                approvers[id].feedbacksNeeded--;
            }
        }

        // if all feedbacks attached to the end node are set, finish the contract
        if (approvers[endId].feedbacksNeeded == 0) {
            approvalStatus = status;
            finish();
        }
    }

    function publish() public onlyBy(owner) {
        super.publish();
        lastUpdate = now;


        addApprover(0xa18cf2d1ec4f9e4f9ce462f655d1681a, 0x88eb7ed440370447b645eba9da3eee83, 0);
        addApprover(0xafcee4fa6da8464b83052031e6bc311a, 0x4aec548707509e4dbc5ed8e4fee4ca67, 1);
        addEndNode(0x8e463739c420934ca7d27c52694cf835, 1);
        addApprover(0x319fa836a55e814393ac66b08776cdcc, 0x88eb7ed440370447b645eba9da3eee83, 0);

        bytes16[] memory approverList1 = new bytes16[](1);
        approverList1[0] = 0xafcee4fa6da8464b83052031e6bc311a;
        addFeedback(0x88eb7ed440370447b645eba9da3eee83, 2, approverList1);
        bytes16[] memory approverList3 = new bytes16[](1);
        approverList3[0] = 0x8e463739c420934ca7d27c52694cf835;
        addFeedback(0x4aec548707509e4dbc5ed8e4fee4ca67, 1, approverList3);


    }

    function start() public onlyBy(owner) onlyInState(ContractState.PUBLISHED) {
        super.start();
        lastUpdate = now;

        //%%CODEGENERATED_IN_PROGRESS%%
    }

    function cancel() public onlyBy(owner) onlyInState(ContractState.RUNNING) {
        super.cancel();
        lastUpdate = now;

        //%%CODEGENERATED_ABORT%%
    }

    function finish() private onlyInState(ContractState.RUNNING) {
        contractState = ContractState.FINISHED;
        lastUpdate = now;

        //%%CODEGENERATED_FINISH%%
    }
}

