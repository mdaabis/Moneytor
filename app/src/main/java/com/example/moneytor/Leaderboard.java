package com.example.moneytor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Leaderboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mScore = new ArrayList<>();
    private ArrayList<String> mImageURLs = new ArrayList<>();
    private ArrayList<String> mRanks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        initImageBitmaps();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.current_user_name);
        navUsername.setText(FetchData.fullName);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

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
            super.onBackPressed();
        }
    }

    private void changeActivity(Activity Current, Class Target) {
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }

    private void initImageBitmaps() {
        for (int i = 0; i < FetchLeaderboard.leaderboardEntries.size(); i++) {
            mImageURLs.add("https://s3.eu-west-2.amazonaws.com/racingleaguehub/img/avatars/default.jpg");
            mNames.add(FetchLeaderboard.leaderboardEntries.get(i).getFullName());
            mScore.add("" + FetchLeaderboard.leaderboardEntries.get(i).getScore());
            mRanks.add(FetchLeaderboard.leaderboardEntries.get(i).getRank());
        }
        initRecyclerView();

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.leaderboard_recyclerView);
        LeaderboardRecyclerview adapter = new LeaderboardRecyclerview(this, mNames, mScore, mImageURLs, mRanks);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}