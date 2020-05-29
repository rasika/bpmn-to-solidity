pragma solidity >=0.4.0 <0.7.0;

contract Sellable {

    // The owner of the contract
    address public owner;
    // Current sale status
    bool public selling = false;
    // Who is the selected buyer, if any.
    address public sellingTo;
    // How much ether (wei) the seller has asked the buyer to send
    uint public askingPrice;

    event Transfer(uint _saleDate, address _from, address _to, uint _salePrice);

    constructor (){
        owner = msg.sender;
        emit Transfer(now, address(0), owner, 0);
    }

    // Makes functions require the called to be the owner of the contract
    modifier onlyOwner {
        require(msg.sender == owner);
        _;
    }


    /**
     * @dev initiateSale is called by the owner of the contract to start the sale process.
     * @param _price is the asking price for the sale
     * @param _to (OPTIONAL) is the address of the person that the owner
     * wants to sell the contract to. If set to 0x0, anyone can buy it.
     */
    function initiateSale(uint _price, address _to) public onlyOwner {
        require(_to != address(this) && _to != owner);
        require(!selling);
        selling = true;
        sellingTo = _to;
        askingPrice = _price;
    }

    /**
     * @dev cancelSale allows the owner to cancel the sale before someone buys
     * the contract.
     */
    function cancelSale() onlyOwner public {
        require(selling);
        resetSale();
    }

    /**
     * @dev completeSale is called buy the specified buyer (or anyone if sellingTo) was not set, to make the purchase.
     * Value sent must match the asking price.
     */
    function completeSale() public payable {
        require(selling);
        require(msg.sender != owner);
        require(msg.sender == sellingTo || sellingTo == address(0));
        require(msg.value == askingPrice);
        // Swap ownership
        address prevOwner = owner;
        address newOwner = msg.sender;
        uint salePrice = askingPrice;

        owner = newOwner;
        resetSale();
        prevOwner.transfer(salePrice);

        Transfer(now,prevOwner,newOwner,salePrice);
    }

    /**
     * @dev resets the variables related to a sale process
     */
    function resetSale() internal onlyOwner {
        selling = false;
        sellingTo = address(0);
        askingPrice = 0;
    }

}
