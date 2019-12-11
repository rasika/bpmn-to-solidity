pragma solidity ^0.4.25;

import './BPM17_Running_ExampleData.sol';

contract BPM17_Running_ExampleFactory {

    function newInstance() public returns(address) {
        return new BPM17_Running_ExampleData();
    }

}
