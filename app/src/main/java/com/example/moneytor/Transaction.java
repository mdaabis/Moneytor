package com.example.moneytor;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Transaction {

    private String transaction_id;
    private double amount;
    private String category;
    private String currency;
    private long date; //Needs to be formatted
    private String description;
    private String merchant;
    private String name;
    private String notes;
    private double latitude;
    private double longitude;
    private boolean declined;

    public Transaction(String transaction_id, double amount, String category, String currency, long date, boolean declined, String description, double latitude, double longitude,
                       String merchant, String name, String notes){
        this.amount=amount;
        this.category=category;
        this.currency=currency;
        this.date=date;
        this.declined=declined;
        this.description=description;
        this.latitude=latitude;
        this.longitude=longitude;
        this.merchant=merchant;
        this.name=name;
        this.notes=notes;
        this.transaction_id=transaction_id;
    }

    public Transaction() {
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

    public boolean getDeclined() {
        return declined;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMerchant(){
        return this.merchant;
    }

    public String getName() {
        return name;
    }

    public String getNotes(){
        return this.notes;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

}