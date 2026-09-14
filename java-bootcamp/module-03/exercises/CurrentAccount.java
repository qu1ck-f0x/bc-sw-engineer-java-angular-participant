public class CurrentAccount extends Account {
    private static final double WITHDRAWAL_FEE = 2.00;

    public CurrentAccount(double initialBalance) {
        super(initialBalance);
    }

    @Override
    public boolean withdraw(double amount) {
        // TODO: reuse parent validation — withdraw amount + WITHDRAWAL_FEE
        super.withdraw(amount + WITHDRAWAL_FEE);
        return true;
    }

    @Override
    public String getAccountType() {
        return "Current";
    }
}
