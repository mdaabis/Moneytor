package com.example.moneytor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FetchData extends AsyncTask<Void, Void, Void> {
    public static double moneyIn = 0.0;
    public static int selectedElement = -1;

    public static List<Transaction> list = new ArrayList<>();
    public static List<Transaction> transactionsThisMonthFD = new ArrayList<>();
    public static String userID;
    public static String firstName;
    public static String surname;
    public static String fullName;
    public static String balance = "";
    public static Map<String, Integer> entry = new HashMap<>();
    private final String secretKey = HomePage.key;
    private DecimalFormat df = new DecimalFormat("#.00");
    private String transactions;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference current_user_db;
    private String name = "";
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Map<String, String> headers = new HashMap<>();
    private Context context;

    /**
     * Constructors are not normally required for AsyncTask
     *
     * Needed to allow access to shared preferences
     *
     * @param context Current context of the class
     */
    public FetchData(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Authentication.SHARED_PREFS, MODE_PRIVATE);
        // Access token retrieved from shared preferences
        String accessToken = sharedPreferences.getString(Authentication.ACCESS_TOKEN, "");
        setLeaderboard();

        headers.put("Authorization", ("Bearer " + accessToken));
        handleResponse();
        return null;
    }


    /**
     * HTTP Client used to create and call API endpoint and retrieve response
     *
     * HashMap used to add headers to HTTP reqeuest
     *
     * If statusCode == 200 means the API call was successful
     *
     * In which case response is read using a BufferedReader
     *
     * @param address URL used for HTTP request
     *
     * @param headers Headers added to URL (e.g. access token)
     *
     * @return JSON array with transaction data from Monzo
     */
    private static String getJSON(String address, Map<String, String> headers) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpGet.addHeader(header.getKey(), header.getValue());
            }
        }
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                return "403";
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            e.getMessage();

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        }
        return builder.toString();
    }


    /**
     * Method gets rid of extra characters around JSON array
     *
     * @param transactions JSON returned by Monzo
     *
     * @return The JSON object without the square brackets
     */
    private static String parseJSON(String transactions) {
        String parsed = transactions.substring(transactions.indexOf("["));
        return parsed.substring(0, parsed.length() - 1);
    }

    /**
     * Method used to determine what key is used to obtain the necessary value from JSON object
     *
     * At the moment type will always be 'balance' but with future use in mind, this method could be
     * useful
     *
     * @param data Data to be parsed
     *
     * @param type What is being extracted from JSON
     *
     * @return The appropriate string to extract the relevant value from the JSON object
     */
    private static String parse(String data, String type) {
        try {
            JSONObject JO = new JSONObject(data);
            switch (type) {
                case "balance":
                    return JO.getString("total_balance");
                case "spend_today":
                    return JO.getString("spend_today");
                case "currency":
                    return JO.getString("currency");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "-1"; // Executed if error occurs
    }

    /**
     * Converts epoch time in the form of a string to an actual time and date
     *
     * @param dateStr String date to be converted into Date format
     *
     * @return Return date in Date format
     */
    private static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * If transactions can be retrieved from the Monzo API (has not yet passed 5 minutes since access
     * token was issues) then they are retrieved
     *
     * Instance of EncryptedTransaction is made with the necessary data from the API response encryped
     * and used in the constructor
     *
     * Stored in the Firebase Realtime Database
     *
     * If transactions have expired and cannot be retrieved from Monzo, that means they are already
     * in Firebase
     *
     * Transactions retrieved from Firebase, decrypted and added to the list of transactions
     */
    private void handleResponse() {
        String balanceURL = "https://api.monzo.com/balance?account_id=acc_00009np8oRwjAPAYEP0mCA";
        String transactionsURL = "https://api.monzo.com/transactions?expand[]=merchant&account_id=acc_00009np8oRwjAPAYEP0mCA";

        do {
            balance = df.format(Double.parseDouble(parse(getJSON(balanceURL, headers), "balance")) / 100);
            transactions = getJSON(transactionsURL, headers);
            mFirebaseAuth = FirebaseAuth.getInstance();
            userID = mFirebaseAuth.getCurrentUser().getUid();
        } while (balance.equals("-.01"));
        try {
            if (!transactions.equals("403")) {
                String parsedTransactions = parseJSON(transactions);
                JSONArray JA = new JSONArray(parsedTransactions);
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Transactions").child(JO.get("id").toString());

                    String dateAsString = JO.get("created").toString().substring(0, JO.get("created").toString().indexOf(".")) + "Z";
                    Date date = parseDate(dateAsString);
                    long epochDate = date.getTime();
                    boolean declined = false;
                    String ID = JO.get("id").toString();
                    String amount = AES.encrypt(JO.get("amount").toString(), secretKey); //double
                    String currency = AES.encrypt(JO.get("currency").toString(), secretKey);
                    String description = AES.encrypt(JO.get("description").toString(), secretKey);
                    String merchant = AES.encrypt(JO.get("merchant").toString(), secretKey);
                    String notes = AES.encrypt(JO.get("notes").toString(), secretKey);

                    if (JO.has("decline_reason")) {
                        declined = true;
                    }

                    // If transaction was a transfer (not in store) then the merchant will be null and there will be no lat/long values. These transactions will not be on the map
                    if (!JO.get("merchant").toString().equals("null")) {
                        JSONObject mJO = (JSONObject) JO.get("merchant");
                        JSONObject aJO = (JSONObject) mJO.get("address");
                        name = AES.encrypt(mJO.get("name").toString(), secretKey);
                        latitude = Double.parseDouble(aJO.get("latitude").toString());
                        longitude = Double.parseDouble(aJO.get("longitude").toString());
                    }

                    String category = capitalise(JO.get("category").toString()).trim();
                    if (category.equals("Eating_out")) {
                        category = "Eating out";
                    }
                    category = AES.encrypt(category, secretKey);

                    EncryptedTransaction transaction = new EncryptedTransaction(ID, amount, category, currency, epochDate, declined, description, latitude, longitude, merchant, name, notes);
                    current_user_db.setValue(transaction);
                }
            }
            String path = "Users/" + userID + "/Transactions";
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(path);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        EncryptedTransaction encryptedTransaction = snapshot.getValue(EncryptedTransaction.class);
                        String transaction_id = encryptedTransaction.getTransaction_id();
                        double amount = Double.parseDouble(AES.decrypt(encryptedTransaction.getAmount(), secretKey));
                        String category = AES.decrypt(encryptedTransaction.getCategory(), secretKey);
                        String currency = AES.decrypt(encryptedTransaction.getCurrency(), secretKey);
                        long date = encryptedTransaction.getDate();
                        String description = AES.decrypt(encryptedTransaction.getDescription(), secretKey);
                        String merchant = AES.decrypt(encryptedTransaction.getMerchant(), secretKey);
                        String name = AES.decrypt(encryptedTransaction.getName(), secretKey);
                        String notes = AES.decrypt(encryptedTransaction.getNotes(), secretKey);
                        double latitude = encryptedTransaction.getLatitude();
                        double longitude = encryptedTransaction.getLongitude();
                        boolean declined = encryptedTransaction.getDeclined();
                        Transaction transaction = new Transaction(transaction_id, amount, category, currency, date, declined, description, latitude, longitude, merchant, name, notes);

                        list.add(transaction);

                        // Can below go outside of for loop
                        Collections.sort(list, new Comparator<Transaction>() {
                            public int compare(Transaction t1, Transaction t2) {
                                return Long.valueOf(t2.getDate()).compareTo(t1.getDate());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSelectedElement();
        addToTotals();
        getName();
    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        String b = "Account Balance\n" + "         £" + balance;
        HomePage.tv.setText(b); //Display balance
    }

    /**
     * Capitalises first letter in string
     *
     * @param str String to be capitalised
     *
     * @return Capitalised string
     */
    private String capitalise(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Determining which transactions have happened this month (used for budgeting)
     *
     * Income and expenditure for this month also determined
     */
    private void addToTotals() {
        moneyIn = 0.0;

        // Getting start of month in epoch form
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        long startOfMonth = cal.getTimeInMillis();

        transactionsThisMonthFD.clear();

        for (int i = 0; i < list.size(); i++) {
            // Format transaction amounts
            double amount = list.get(i).getAmount() / 100;
            // Obtaining transactions from this month
            if (list.get(i).getDate() > startOfMonth) {
                // If transaction amount is positive, add to money coming in otherwise add to money going out
                transactionsThisMonthFD.add(list.get(i));
                if (amount > 0.0) {
                    moneyIn += (amount * 100);
                }
            }
        }
    }

    /**
     * Gets selected element (budgeting technique choice) from Firebase Realtime Database
     */
    private void getSelectedElement() {
        String path = "Users/" + userID + "/Selected Element";
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    selectedElement = Integer.parseInt(dataSnapshot.getValue().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Retrieves and displays user's name in navigation bar
     */
    private void getName() {
        String path = "Users/" + userID;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firstName = dataSnapshot.child("First name").getValue().toString();
                surname = dataSnapshot.child("Surname").getValue().toString();
                fullName = firstName + " " + surname;

                HomePage.navUsername.setText(fullName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * User scores store in Firebase Realtime Database using a hashmap called 'entry'
     */
    private void setLeaderboard() {
        current_user_db = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
        String path = "Leaderboard";
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    int score = Integer.parseInt(snapshot.getValue().toString());
                    entry.put(name, score);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}