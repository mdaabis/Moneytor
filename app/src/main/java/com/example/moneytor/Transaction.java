package com.example.moneytor;

public class Transaction {

    private String transaction_id;
    private double amount;
    private String date; //Needs to be formatted
    private String currency;
    private String merchant;
    private String notes;
    private String category;

    public Transaction(String transaction_id, double amount, String date, String currency,
                       String merchant, String notes, String category){
        this.transaction_id=transaction_id;
        this.amount=amount;
        this.date=date;
        this.currency=currency;
        this.merchant=merchant;
        this.notes=notes;
        this.category=category;
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

    public String getTransaction_id() {
        return this.transaction_id;
    }
}
