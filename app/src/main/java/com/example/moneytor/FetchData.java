package com.example.moneytor;
import android.os.AsyncTask;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FetchData extends AsyncTask<Void, Void, Void> {
    DecimalFormat df = new DecimalFormat("#.00");
    private String balance;
    private String spendToday;
    private String currency;
    private String pots;
    private String transactions;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference current_user_db;


    @Override
    protected Void doInBackground(Void... voids) {
        String accessToken = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJlYiI6Im1UbTIvRFN5b0hrM2VQbER2eGY3IiwianRpIjoiYWNjdG9rXzAwMDA5cnU0dlZIOXdYb2FLaXZWbm0iLCJ0eXAiOiJhdCIsInYiOiI2In0.D1kefDHdDaxSqf5DJtokuIxYHdWkgs_C1WbC82yEQlaYPPU_9us-ezgcFtdJHu_s7BlufT2iRXL1OuG7Bbg6ag";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", ("Bearer "+ accessToken));

        String balanceURL = "https://api.monzo.com/balance?account_id=acc_00009np8oRwjAPAYEP0mCA";
        String potsURL = "https://api.monzo.com/pots";
        String transactionsURL = "https://api.monzo.com/transactions?account_id=acc_00009np8oRwjAPAYEP0mCA";

        balance = df.format(Double.parseDouble(parse(getJSON(balanceURL, headers), "balance")) / 100);
        currency = parse(getJSON(balanceURL, headers), "currency");
        spendToday = df.format(Double.parseDouble(parse(getJSON(balanceURL, headers), "spend_today")));
        pots = getJSON(potsURL, headers);
        transactions = getJSON(transactionsURL, headers);
        mFirebaseAuth = FirebaseAuth.getInstance();
        String userID = mFirebaseAuth.getCurrentUser().getUid();

        System.out.println(pots);
        System.out.println("This is transactions: "+ transactions);
//        System.out.println("This is the user ID from FetchData: " + userID);

        try {
            if (!transactions.equals("403")) {
                String parsedTransactions = parseJSON(transactions);
//                System.out.println("Transaction not equal to 403 and parsed transactions = " + parsedTransactions);

                JSONArray JA = new JSONArray(parsedTransactions);
                for (int i = 0; i < JA.length(); i++) {
                    Map newPost = new HashMap();
                    JSONObject JO = (JSONObject) JA.get(i);
                    current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Transactions").child(JO.get("id").toString());
//                    newPost.put("amount", JO.get("amount"));
//                    newPost.put("description", JO.get("description"));
//                    newPost.put("created", JO.get("created"));
//                    newPost.put("currency", JO.get("currency"));
//                    newPost.put("notes", JO.get("notes"));
                    String date = JO.get("created").toString().substring(0,JO.get("created").toString().indexOf("T"));
                    Transaction transaction = new Transaction(JO.get("id").toString(), Double.parseDouble(JO.get("amount").toString()), date, JO.get("currency").toString(), JO.get("merchant").toString(), JO.get("notes").toString(), JO.get("category").toString());
                    current_user_db.setValue(transaction);
                }
                System.out.println("Transactions not expired: " + transactions);
            } else {
                System.out.println("Transactions expired: " + transactions);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("error jsonE");
        }

        // This ValueEventListener reads
//        current_user_db.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                System.out.println(dataSnapshot.getValue());
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return null;
    }

    public static String parseJSON(String transactions){
        String parsed=transactions.substring(transactions.indexOf("["));
        return parsed.substring(0, parsed.length()-1);
    }

//    public static void readData(){
//
//    }
//    public static void writeData(){
//
//    }

    public static String getJSON(String address, Map<String, String> headers) {
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

//    public static boolean getErrorCode(String address, Map<String, String> headers) {
//        StringBuilder builder = new StringBuilder();
//        HttpClient client = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet(address);
//
//        if (headers != null) {
//            for (Map.Entry<String, String> header : headers.entrySet()) {
//                httpGet.addHeader(header.getKey(), header.getValue());
//            }
//        }
//        try {
//            HttpResponse response = client.execute(httpGet);
//            StatusLine statusLine = response.getStatusLine();
//            int statusCode = statusLine.getStatusCode();
//            if (statusCode == 200) {
//                return true;
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//            e.getMessage();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            e.getMessage();
//        }
//        return false;
//    }

    public String parse(String data, String type){
        try {
            JSONObject JO = new JSONObject(data);
            if (type.equals("balance")) {
                return JO.getString("total_balance");
            } else if (type.equals("spend_today")) {
                return JO.getString("spend_today");
            } else if (type.equals("currency")) {
                return JO.getString("currency");
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return "-1";
    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        String b = "£"+this.balance;
        HomePage.tv.setText(b);

    }

}

