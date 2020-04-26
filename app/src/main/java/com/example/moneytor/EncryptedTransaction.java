package com.example.moneytor;

public class EncryptedTransaction {

    private String transaction_id;
    private String amount;
    private String category;
    private String currency;
    private long date;
    private boolean declined;
    private String description;
    private double latitude;
    private double longitude;
    private String merchant;
    private String name;
    private String notes;

    /*
     * Constructor used to initialise variables
     */
    public EncryptedTransaction(String transaction_id, String amount, String category, String currency, long date, boolean declined, String description, double latitude, double longitude,
                                String merchant, String name, String notes) {
        this.transaction_id = transaction_id;
        this.amount = amount;
        this.category = category;
        this.currency = currency;
        this.date = date;
        this.declined = declined;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.merchant = merchant;
        this.name = name;
        this.notes = notes;
    }

    public EncryptedTransaction() {
    }


    public String getAmount() {
        return this.amount;
    }

    public String getCategory() {
        return this.category;
    }

    public String getCurrency() {
        return this.currency;
    }

    public long getDate() {
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

    public String getMerchant() {
        return this.merchant;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

}
