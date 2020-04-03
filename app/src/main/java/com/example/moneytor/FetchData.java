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
    public static HashMap<String, Integer> entry = new HashMap<>();
    private DecimalFormat df = new DecimalFormat("#.00");
    private String transactions;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference current_user_db;
    private String name = "";
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Map<String, String> headers = new HashMap<>();

    private Context context;

    public FetchData(Context context) {
        this.context = context;
    }

    // Method gets rid of extra characters around JSON array
    private static String parseJSON(String transactions) {
        String parsed = transactions.substring(transactions.indexOf("["));
        return parsed.substring(0, parsed.length() - 1);
    }

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
//                System.err.println("Error code " + statusCode);
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

    private static String parse(String data, String type) {
        try {
            JSONObject JO = new JSONObject(data);
//            if (type.equals("balance")) {
//                return JO.getString("total_balance");
//            } else if (type.equals("spend_today")) {
//                return JO.getString("spend_today");
//            } else if (type.equals("currency")) {
//                return JO.getString("currency");
//            }
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
        return "-1";
    }

    private static Date parseDate(String dateStr) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Authentication.SHARED_PREFS, MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(Authentication.ACCESS_TOKEN, "");
        getLeaderboard();

        headers.put("Authorization", ("Bearer " + accessToken));
        handleResponse();
//        System.out.println("access token in fetchdata: " + accessToken2);
        return null;
    }

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
                    Double amount = Double.parseDouble(JO.get("amount").toString());
                    String currency = JO.get("currency").toString();
                    String description = JO.get("description").toString();
                    String merchant = JO.get("merchant").toString();
                    String notes = JO.get("notes").toString();

                    if (JO.has("decline_reason")) {
                        declined = true;
                    }

                    // If transaction was a transfer (not in store) then the merchant will be null and there will be no lat/long values. These transactions will not be on the map
                    if (!JO.get("merchant").toString().equals("null")) {
                        JSONObject mJO = (JSONObject) JO.get("merchant");
                        JSONObject aJO = (JSONObject) mJO.get("address");
                        name = mJO.get("name").toString();
                        latitude = Double.parseDouble(aJO.get("latitude").toString());
                        longitude = Double.parseDouble(aJO.get("longitude").toString());
                    }
                    String category = capitalise(JO.get("category").toString()).trim();
                    if (category.equals("Eating_out")) {
                        category = "Eating out";
                    }

                    Transaction transaction = new Transaction(ID, amount, category, currency, epochDate, declined, description, latitude, longitude, merchant, name, notes);
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
                        Transaction transaction = snapshot.getValue(Transaction.class);
                        if (transaction != null && !transaction.getDeclined()) { // Must first check if transaction is null to prevent NullPointerException warning
                            list.add(transaction);
                        }
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
            System.out.println("error jsonE");
        }
        getSelectedElement();
        addToTotals();
        getName();
    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        String b = "Account Balance\n" + "         £" + balance;
        HomePage.tv.setText(b);
    }

    private String capitalise(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

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
//        1583024400

        for (int i = 0; i < list.size(); i++) {
            // Format transaction amounts
            double amount = list.get(i).getAmount() / 100;
            // Obtaining transactions from this month
//            if (list.get(i).getDate() > startOfMonth) {
            if (list.get(i).getDate() > startOfMonth) {
                // If transaction amount is positive, add to money coming in otherwise add to money going out
                transactionsThisMonthFD.add(list.get(i));
                if (amount > 0.0) {
                    moneyIn += (amount * 100);
                }
            }
        }
    }

    private void getSelectedElement() {
        String path = "Users/" + userID + "/Selected Element";
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    selectedElement = Integer.parseInt(dataSnapshot.getValue().toString());
                } catch (Exception e) {
                    System.out.println("getSelectedElement error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        return selectedElement;
    }

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

    private void getLeaderboard() {
        current_user_db = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
        String path = "Leaderboard";
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    int score = Integer.parseInt(snapshot.getValue().toString());
                    System.out.println("name + score: " + name + ":" + score);
                    entry.put(name, score);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}

