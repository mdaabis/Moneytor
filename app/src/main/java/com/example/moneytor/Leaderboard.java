package com.example.moneytor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Leaderboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mScore = new ArrayList<>();
    private ArrayList<String> mImageURLs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        initImageBitmaps();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.current_user_name);
        navUsername.setText(FetchData.fullname);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_pots:
                changeActivity(this, Pots.class);
                break;
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
                Intent intentM = new Intent(this, Map.class);
                startActivityForResult(intentM, 0);
                break;
            case R.id.nav_budgeting:
                changeActivity(this, BudgetingPlan.class);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Leaderboard.this, MainActivity.class);
                startActivity(intent);
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

    private void initImageBitmaps(){
        mImageURLs.add("https://images.unsplash.com/photo-1558487661-9d4f01e2ad64?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80");
        mNames.add("Mostafa Daabis");
        mScore.add("103");


        mImageURLs.add("https://images.pexels.com/photos/733872/pexels-photo-733872.jpeg?cs=srgb&dl=closeup-photo-of-woman-with-brown-coat-and-gray-top-733872.jpg&fm=jpg");
        mNames.add("Jane Doe");
        mScore.add("83");

        mImageURLs.add("https://images.pexels.com/photos/594610/pexels-photo-594610.jpeg?cs=srgb&dl=man-wearing-a-jacket-sitting-on-brown-wooden-crate-594610.jpg&fm=jpg");
        mNames.add("Scott Richardson");
        mScore.add("78");

        mImageURLs.add("https://images.pexels.com/photos/936119/pexels-photo-936119.jpeg?cs=srgb&dl=laughing-man-wearing-gray-v-neck-t-shirt-936119.jpg&fm=jpg");
        mNames.add("Jake Maxwell");
        mScore.add("76");

        mImageURLs.add("https://images.unsplash.com/photo-1492633423870-43d1cd2775eb?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80");
        mNames.add("Eva Goodman");
        mScore.add("71");


        mImageURLs.add("https://images.pexels.com/photos/842548/pexels-photo-842548.jpeg?cs=srgb&dl=man-holding-mug-in-front-of-laptop-842548.jpg&fm=jpg");
        mNames.add("Andreas Christinsen");
        mScore.add("66");

        mImageURLs.add("https://images.unsplash.com/photo-1534751516642-a1af1ef26a56?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=635&q=80");
        mNames.add("Autumn Reid");
        mScore.add("63");

        initRecyclerView();

    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.leaderboard_recyclerView);
        LeaderboardRecyclerview adapter = new LeaderboardRecyclerview(this, mNames, mScore, mImageURLs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}