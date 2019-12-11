const BasicToken = artifacts.require('BasicToken');

contract('BasicToken', (accounts) => {
    let basicToken;
    const ownerAccount = accounts[0];
    const otherAccount = accounts[1];
    const initialSupply = 100;

    // build up and tear down a new BasicToken contract before each test
    beforeEach(async () => {
        basicToken = await BasicToken.new(initialSupply, { from: ownerAccount });
    });

    it("should equal the initial supply", async () => {
        let amount = await basicToken.initialSupply({ from: otherAccount });
        assert.equal(amount, initialSupply);
    });

    it("should provide balance of the owner", async () => {
        let amount = await basicToken.balanceOf(ownerAccount, { from: otherAccount });
        assert.equal(amount, initialSupply);
    });

    it("should provide balance of zero for the other account", async () => {
        let amount = await basicToken.balanceOf(otherAccount, { from: otherAccount });
        assert.equal(amount, 0);
    });

    it("should provide balance of 200 for the other account after transfer", async () => {
        await basicToken.transfer(otherAccount, 1, { from: ownerAccount});
        let amount = await basicToken.balanceOf(otherAccount, { from: otherAccount });
        assert.equal(amount, 1);
    });
});