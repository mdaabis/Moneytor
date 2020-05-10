package com.example.moneytor;

public class Transaction {

    private String transaction_id;
    private double amount;
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


    /**
     * Constructor used to initialise variables
     *
     * @param transaction_id The transaction ID
     *
     * @param amount Cost of transaction
     *
     * @param category The associated Monzo category
     *
     * @param currency Currency of transaction (differs in other countries)
     *
     * @param date Epoch date that transaction was made
     *
     * @param declined Whether transaction was declined or not
     *
     * @param description The Monzo description for the transaction
     *
     * @param latitude Latitude of store where transaction was made
     *
     * @param longitude Longitude of store where transaction was made
     *
     * @param merchant Merchant ID
     *
     * @param name Name of store where purchase was made
     *
     * @param notes Note attached to transaction
     */
    public Transaction(String transaction_id, double amount, String category, String currency, long date, boolean declined, String description, double latitude, double longitude,
                       String merchant, String name, String notes) {
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
        this.transaction_id = transaction_id;
    }

    public Transaction() {
    }

    /**
     * @return Transaction amount
     */
    public double getAmount() {
        return this.amount;
    }


    /**
     * @return Transaction category
     */
    public String getCategory() {
        return this.category;
    }


    /**
     * @return Transaction currency
     */
    public String getCurrency() {
        return this.currency;
    }


    /**
     * @return Transaction date
     */
    public long getDate() {
        return this.date;
    }


    /**
     * @return Whether transaction was declined or not
     */
    public boolean getDeclined() {
        return declined;
    }


    /**
     * @return Transaction desciption
     */
    public String getDescription() {
        return description;
    }


    /**
     * @return Transaction latitude
     */
    public double getLatitude() {
        return latitude;
    }


    /**
     * @return Transaction longitude
     */
    public double getLongitude() {
        return longitude;
    }


    /**
     * @return Merchant ID
     */
    public String getMerchant() {
        return this.merchant;
    }


    /**
     * @return Store name
     */
    public String getName() {
        return name;
    }


    /**
     * @return Transaction notes
     */
    public String getNotes() {
        return this.notes;
    }


    /**
     * @return Transaction ID
     */
    public String getTransaction_id() {
        return transaction_id;
    }

}