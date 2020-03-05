package com.example.moneytor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class BudgetingPlan extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    int selectedElement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting_plan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_views);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.current_user_name);
        navUsername.setText(FetchData.fullname);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        selectedElement = FetchData.selectedElement;
        changeFragment();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
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
                changeActivity(this, MainActivity.class);
                break;
        }
        return true;
    }

    private void changeActivity(Activity Current, Class Target){
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }

    public void changeFragment(){
        Fragment fragment;

        if(selectedElement==0){
            fragment = new Fragment50_30_20();
        } else {
            fragment = new Fragment80_20();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }



}
