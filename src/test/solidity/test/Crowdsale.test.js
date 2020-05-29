const BasicToken = artifacts.require('BasicToken');

contract('Crowdsale', (accounts) => {
    let crowdSale;
    const ownerAccount = accounts[0];
    const otherAccount1 = accounts[1];
    const otherAccount2 = accounts[2];
    const initialSupply = 100;

    // build up and tear down a new BasicToken contract before each test
    beforeEach(async () => {
        crowdSale = await Crowdsale.new(ownerAccount, 10, ownerAccount, 60, 10, { from: ownerAccount });
    });
});
