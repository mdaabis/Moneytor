package com.example.moneytor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;


public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static TextView navUsername;
    public static TextView tv;
    public static Boolean isPositive;
    private DrawerLayout drawer;
    private int exitCounter = 0;
    private ArrayList<String> mAmount = new ArrayList<>();
    private ArrayList<String> mCategory = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mDescription = new ArrayList<>();
    private ArrayList<String> mNotes = new ArrayList<>();
    private ArrayList<Boolean> mIsPositive = new ArrayList<>();
    private RecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Fetching and storing data from Monzo API into Firebase
        FetchData process = new FetchData(getApplicationContext());
        process.execute();

        FetchLeaderboard fetchLeaderboard = new FetchLeaderboard();
        fetchLeaderboard.execute();

        tv = findViewById(R.id.textView);

        initListBitmaps();


        // Setting title and toolbar with correct colours and formatting
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawer = findViewById(R.id.drawer_layout);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.current_user_name);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();    }

    public void initListBitmaps() {
        for (int i = 0; i < FetchData.list.size(); i++) {
            boolean notZero = FetchData.list.get(i).getAmount() != 0.0;
            String amount = amountToPound(Double.toString(FetchData.list.get(i).getAmount()));
            String category = FetchData.list.get(i).getCategory();
            String date = dateTimeToDate(epochToDate(Long.toString(FetchData.list.get(i).getDate())));
            String description = FetchData.list.get(i).getDescription();
            String notes = FetchData.list.get(i).getNotes();
            isPositive = Double.toString(FetchData.list.get(i).getAmount()).charAt(0) != '-';

            if (notZero) {
                mAmount.add(amount);
                mCategory.add(category);
                mDate.add(date);
                mDescription.add(description);
                if (notes.equals("")) {
                    mNotes.add("No notes");
                } else {
                    mNotes.add(notes);
                }
                mIsPositive.add(isPositive);
            }

            // Adds divider between each item
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
            RecyclerView recyclerViewObj = findViewById(R.id.recyclerView);
            recyclerViewObj.addItemDecoration(dividerItemDecoration);

        }

        initRecyclerView();
    }

    public void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(this, mAmount, mCategory, mDate, mDescription, mNotes, mIsPositive);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_settings:
                changeActivity(this, Settings.class);
                break;
            case R.id.nav_home:
                changeActivity(this, HomePage.class);
                break;
            case R.id.nav_leaderboard:
                changeActivity(this, Leaderboard.class);
                break;
            case R.id.nav_notifications:
                changeActivity(this, Notifications.class);
                break;
            case R.id.nav_map:
                changeActivity(this, Map.class);
                break;
            case R.id.nav_budgeting:
                changeActivity(this, BudgetingPlan.class);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = getSharedPreferences(Authentication.SHARED_PREFS, MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                changeActivity(this, MainActivity.class);
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exitCounter < 1) {
                Toast.makeText(HomePage.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                exitCounter++;
            } else {
                FirebaseAuth.getInstance().signOut();
                changeActivity(HomePage.this, MainActivity.class);
            }
        }
    }

    private void changeActivity(Activity Current, Class Target) {
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }


    private String epochToDate(String dateStr) {
        Long date = Long.parseLong(dateStr);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        return format.format(date);
    }

    private String dateTimeToDate(String date) {
        return date.substring(0, 10);
    }

    private String amountToPound(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount) / 100;
        if (amount.charAt(0) == '-') {
            return "-£" + df.format(amountL).substring(1);
        }
        return "£" + df.format(amountL);
    }

}
