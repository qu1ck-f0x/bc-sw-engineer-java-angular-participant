package com.academy.bank;

public abstract class Account {

    private String accountNumber;
    private double balance;
    private Customer customer;

    protected Account(String accountNumber, double balance, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customer = customer;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void deposit(double amount) {
        // TODO: reject amount <= 0; otherwise add amount to balance
        if (amount < 0) {
            System.out.println("Invalid amount");
            return;
        }
        this.balance += amount;
        System.out.println("Deposited " + amount + " to account " + getAccountNumber());
    }

    public boolean withdraw(double amount) {
        // TODO: reject amount <= 0
        // TODO: totalDeduction = amount + calculateCharges(); fail if > balance
        // TODO: subtract totalDeduction from balance; return true/false
        if (amount < 0) {
            System.out.println("Invalid amount");
            return false;
        }
        if (amount > balance) {
            System.out.println("Invalid amount");
            return false;
        }

        this.balance -= amount;
        System.out.println("Withdrawn " + amount + " from account " + getAccountNumber());
        return true;

    }

    public abstract void displayAccount();

    public double calculateCharges() {
        return 0.0;
    }

    public double calculateInterest() {
        return 0.0;
    }

    public String getAccountType() {
        return "Account";
    }
}
