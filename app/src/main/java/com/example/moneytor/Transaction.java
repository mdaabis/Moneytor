package com.example.moneytor;

import java.util.Date;

public class Transaction {

    private String transaction_id;
    private double amount;
    private long date; //Needs to be formatted
    private String currency;
    private String merchant;
    private String notes;
    private String category;

    public Transaction(String transaction_id, double amount, long date, String currency,
                       String merchant, String notes, String category){
        this.transaction_id=transaction_id;
        this.amount=amount;
        this.date=date;
        this.currency=currency;
        this.merchant=merchant;
        this.notes=notes;
        this.category=category;
    }

//    public Transaction() {
//        this.transaction_id="-1";
//        this.amount=-1.0;
//        this.date="-1";
//        this.currency="-1";
//        this.merchant="-1";
//        this.notes="-1";
//        this.category="-1";
//    }

    public double getAmount(){
        return this.amount;
    }

    public long getDate(){
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
