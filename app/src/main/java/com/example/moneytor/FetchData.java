package com.example.moneytor;
import android.os.AsyncTask;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    public  String whoAmI;
    @Override
    protected Void doInBackground(Void... voids) {
        String accessToken = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJlYiI6InIxQVZXdjB6a1Vhc3NDVTRtQ1B5IiwianRpIjoiYWNjdG9rXzAwMDA5cldiY0xpNXV3QWJGcmVrekoiLCJ0eXAiOiJhdCIsInYiOiI2In0.LPbgi59KycMcERQ17e7YyPQYNNDfee0U1bXz1Fdl2pgPDgmzR6ATnpHGh1x8SBr91oRJJcesNi7pHRhhhE8XAg";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", ("Bearer "+ accessToken));
        whoAmI = getJSON("https://api.monzo.com/ping/whoami", headers);
//        Pattern pattern = Pattern.compile("\"user_id\":\"(.*?)\"}", Pattern.DOTALL);
//        Matcher matcher = pattern.matcher(whoAmI);
//        while (matcher.find()) {
//            System.out.println("This is the account_id: " + matcher.group(1));
//        }

        System.out.println("This is Whomai from FetchData: " +whoAmI);
//        System.out.println(getJSON("https://api.monzo.com/ping/whoami", headers));
//        System.out.println(getJSON("https://api.monzo.com/accounts", headers));
//        headers.put("account_id", "${acc_00009np8oRwjAPAYEP0mCA}");
//        System.out.println(headers);
//        System.out.println(getJSON("https://api.monzo.com/balance", headers));
//        System.out.println(getJSON("https://api.monzo.com/balance?account_id==$acc_00009np8oRwjAPAYEP0mCA", headers));
//        System.out.println(getJSON("https://api.monzo.com/pots", headers));

        return null;
    }
//
//    public String getWhoAmI(){
//        return this.whoAmI;
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
//        System.out.println(httpGet.toString());

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

//                JSONArray JA = new JSONArray(builder);
//                for(int i=0;i<JA.length();i++){
//                    JSONObject JO = (JSONObject) JA.get(i);
//
//                }

            } else {
                System.err.println("Error code " + statusCode);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            e.getMessage();

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
//        } catch (JSONException e){
//            e.printStackTrace();
        }

        //


        return builder.toString();
    }

}

