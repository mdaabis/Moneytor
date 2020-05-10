package com.example.moneytor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Authentication extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXPIRE_DATE = "expireDate";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String STATE_TOKEN = "stateToken";
    private static String authorisationCode = "";
    private static String accessToken;
    private WebView webView;
    private int executions = 0;
    private String clientID = "oauth2client_00009rR0hHMOqkIriiVAQ5";
    private String redirectURI = "https://www.moneytor.com/";
    private String redMonzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        String state = randomString();
        // Storing the random string that is being used as a state token into shared preference if there is no state token already stored there
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (!sharedPreferences.contains(STATE_TOKEN)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(STATE_TOKEN, state);
            editor.apply();
        }
        redMonzo = "https://auth.monzo.com/?client_id=" + clientID + "&redirect_uri=" + redirectURI + "&response_type=code&state=" + state;

        // Sets webview to be the Monzo authentication page
        webView = findViewById(R.id.webviewAuth);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        authentication();
    }

    /**
     * The response URI obtained after being redirected from the user's email to app is parsed
     *
     * 'Stringbetween' method used to extract authorisation code and returned state token
     *
     * Returned state token compared to initially chosen random string to prevent cross-site
     * request forgery
     *
     * User returned to login page if state tokens don't match and allowed to continue into
     * homepage if they do
     */
    private void authentication() {
        Uri uri = Uri.parse(redMonzo);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        if (getIntent().getData() != null) {

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String stateCheck = sharedPreferences.getString(STATE_TOKEN, "");
            String returnedURI = getIntent().getData().toString();
            authorisationCode = stringBetween(returnedURI, "?code=", "&state");
            String returnedStateToken = stringBetween(returnedURI + "end", "&state=", "end");

            if (!returnedStateToken.equals(stateCheck)) {
                Toast.makeText(Authentication.this, "Authorisation failed", Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().clear().apply();
                changeActivity(this, MainActivity.class);
            } else {
                Toast.makeText(Authentication.this, "Authorisation succeeded", Toast.LENGTH_SHORT).show();
            }

            // Instance of BackgroundAuth class made to carry out network operations in the background
            BackgroundAuth backgroundAuth = new BackgroundAuth();
            backgroundAuth.execute();
        }
    }

    /**
     * Overrides normal behavior when device's back button is pressed
     *
     * Acts like going back in a browser
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Returns a substring of a string 'uri' between two indices, 'start' and 'end'
     *
     * @param uri The URI returned that is now being parsed
     *
     * @param start Desired start substring
     *
     * @param end Desired end substring
     *
     * @return The substring in between start and end substrings
     */
    private String stringBetween(String uri, String start, String end) {
        return StringUtils.substringBetween(uri, start, end);
    }

    /**
     * Changes activity from current to target activity
     *
     * @param Current The current activity the user is in
     *
     * @param Target The activity the user will be redirected to
     */
    private void changeActivity(Activity Current, Class Target) {
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }

    /**
     * Generates a random 12-character alpha-numeric string
     *
     * Does this by picking a random integer and using it as the index to choose a character
     * from 'AlphaNumericString'
     *
     * 'for-loop' used to append chosen character to stringbuilder 12 times
     *
     * @return Returns a random string of the specified length
     */
    private String randomString() {
        int length = 12;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Class extends AsyncTask
     *
     * Runs in background
     *
     * Used to carry out network operations that cannot be carried out on main thread
     */

    class BackgroundAuth extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getAccessToken();
            return null;
        }

        /**
         * Method gets an access token
         *
         * Creates a HTTP POST request
         *
         * Response parsed to obtain access token and token duration
         */
        private void getAccessToken() {

            String accessTokenURL = "https://api.monzo.com/oauth2/token";
            String clientSecret = "mnzpub.nio98cyoi2OnW4hdtK9fOwFXdj8cSfIGHL/etY7y93mqxRO3bKRYShZAgh39aXd6s2ejXafbXTdhYyJxMI2f";

            // Method was called twice unnecessarily so if statement is used to ensure it's run once
            if (executions == 0) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(accessTokenURL);

                // Request parameters and other properties.
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("grant_type", "authorization_code"));
                params.add(new BasicNameValuePair("client_id", clientID));
                params.add(new BasicNameValuePair("client_secret", clientSecret));
                params.add(new BasicNameValuePair("redirect_uri", redirectURI));
                params.add(new BasicNameValuePair("code", authorisationCode));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // Writing error to Log
                    e.printStackTrace();
                }

                // Execute the HTTP Request
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity respEntity = response.getEntity();

                    if (respEntity != null) {
                        // EntityUtils to get the response content
                        String content = EntityUtils.toString(respEntity);
                        executions++;
                        accessToken = stringBetween(content, "\"access_token\":\"", "\",\"client_id\"");
                        long expires = Integer.parseInt(stringBetween(content, "expires_in\":", ",\"scope\""));
                        setExpirationEpoch(expires);
                    }
                } catch (ClientProtocolException e) {
                    // Writing exception to log
                    e.printStackTrace();
                } catch (IOException e) {
                    // Writing exception to log
                    e.printStackTrace();
                }
            }
        }

        /**
         * Method takes token duration in as parameter
         *
         * Expiration time set to duration of token added to current epoch time
         *
         * Expiration time stored in shared preferences so it can be checked upon next login
         *
         * @param expires The epoch time of how long the access token is valid for
         */
        private void setExpirationEpoch(long expires) {
            Instant instant = Instant.now();
            long expirationTime = expires + instant.getEpochSecond();

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(EXPIRE_DATE, expirationTime);
            editor.putString(ACCESS_TOKEN, accessToken);
            editor.apply();
        }

        /**
         * Executed once everything in doInBackground() is has been executed
          */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            changeActivity(Authentication.this, HomePage.class); // Redirects user to homepage
        }
    }
}

