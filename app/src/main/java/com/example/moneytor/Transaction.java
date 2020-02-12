package com.example.moneytor;

import java.util.Date;

public class Transaction {

    private String transaction_id;
    private double amount;
    private String category;
    private String currency;
    private long date; //Needs to be formatted
    private String merchant;
    private String notes;

    public Transaction(String transaction_id, double amount, String category, String currency, long date,
                       String merchant, String notes){
        this.transaction_id=transaction_id;
        this.amount=amount;
        this.date=date;
        this.currency=currency;
        this.merchant=merchant;
        this.notes=notes;
        this.category=category;
    }

    public Transaction() {
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

    public String getTransaction_id() {
        return this.transaction_id;
    }

    public double getAmount(){
        return this.amount;
    }

    public String getCategory(){
        return this.category;
    }

    public String getCurrency(){
        return this.currency;
    }

    public long getDate(){
        return this.date;
    }

    public String getMerchant(){
        return this.merchant;
    }

    public String getNotes(){
        return this.notes;
    }
}
