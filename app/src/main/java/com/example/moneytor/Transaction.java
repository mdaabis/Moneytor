package com.example.moneytor;

public class Transaction {

    private double account_balance;
    private double amount;
    private String date; //Needs to be formatted
    private String currency;
    private String merchant;
    private String notes;
    private String category;

    public Transaction(double account_balance, double amount, String date, String currency,
                       String merchant, String notes, String category){
        this.account_balance=account_balance;
        this.amount=amount;
        this.date=date;
        this.currency=currency;
        this.merchant=merchant;
        this.notes=notes;
        this.category=category;
    }

    public double getAccount_balance(){
        return this.account_balance;
    }

    public double getAmount(){
        return this.amount;
    }

    public String getDate(){
        return this.date;
    }

    public String getCurrency(){
        return this.currency;
    }

    public String getMerchant(){
        return this.merchant;
    }

    public String getNotes(){
        return this.notes;
    }

    public String getCategory(){
        return this.category;
    }
}
