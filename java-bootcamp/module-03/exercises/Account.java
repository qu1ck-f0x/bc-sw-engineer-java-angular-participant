public class Account {
    // TODO: hide balance from outside code (private field already shown — focus on methods)
    private double balance;

    public Account(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException(
                    "Initial balance cannot be negative");
        }
        balance = initialBalance;
    }

    public void deposit(double amount) {
        // TODO: reject non-positive amounts (print message, return early)
        if (amount < 0) {
            System.out.println("Deposit amount cannot be negative");
            return;
        }
        balance += amount;
    }

    public boolean withdraw(double amount) {
        // TODO: reject if amount <= 0 OR amount > balance
        if (amount < 0) {
            System.out.println("Withdraw amount cannot be negative");
            return false;
        }
        if  (balance < amount) {
            System.out.println("Withdraw amount cannot be more than balance");
            return false;
        }

        balance -= amount;
        return true;
    }

    // TODO: read-only accessor — return balance
    public double getBalance() {
        return balance;
    }

    // Exercise 3 will override this method
    public String getAccountType() {
        return "Account";
    }
}
