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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class BudgetingPlan extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int selectedElement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting_plan);

        // Sets toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        DrawerLayout drawer;
        drawer = findViewById(R.id.drawer_layout);

        // Sets navigation bar
        NavigationView navigationView = findViewById(R.id.nav_views);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.current_user_name);
        navUsername.setText(FetchData.fullName);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        selectedElement = FetchData.selectedElement;
        changeFragment(); // Determines which fragment will be used depending on user's budgeting choice
    }

    /**
     * Redirects the user to another page depending on what they chose in the navigation bar
     * <p>
     * Logs user out, signs them out of Firebase and deletes shared preferences if 'Logout' is clicked
     */
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
            case R.id.nav_map:
                Intent intentM = new Intent(this, Map.class);
                startActivityForResult(intentM, 0);
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

    /**
     * Changes activity from current to target activity
     *
     * @param Current The current activity the user is in
     * @param Target  The activity the user will be redirected to
     */
    private void changeActivity(Activity Current, Class Target) {
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }


    /**
     * Selected element is determined in the Settings page and represents budgeting technique chosen
     * <p>
     * Appropriate fragment is displayed
     */
    private void changeFragment() {
        Fragment fragment;

        if (selectedElement == 0) {
            fragment = new Fragment50_30_20();
        } else {
            fragment = new Fragment80_20();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }


}
