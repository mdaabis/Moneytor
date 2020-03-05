package com.example.moneytor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    TextView resetTV, deleteTV, logoutTV, budgetingTV;
    AlertDialog.Builder builder;
    public int selectedElement;
    private AlertDialog alert;
    SharedPreferences.Editor editor;
    private DatabaseReference current_user_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        resetTV = (TextView) findViewById(R.id.change_pswd);
        deleteTV = (TextView) findViewById(R.id.delete_account);
        logoutTV = (TextView) findViewById(R.id.logout_settings);
        budgetingTV = (TextView) findViewById(R.id.change_plan);

        builder = new AlertDialog.Builder(this);
        builder.setTitle("My title");
        builder.setMessage("This is my message.");
        builder.setPositiveButton("OK", null);

        budgetingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        resetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(Settings.this, ResetPassword.class);
            }
        });

        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                changeActivity(Settings.this, MainActivity.class);
            }
        });

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
                Intent intent = new Intent(Settings.this, MainActivity.class);
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

    public void changeActivity(Activity Current, Class Target){
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }

    private void SingleChoiceWithRadioButton() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        selectedElement = sharedPref.getInt("selectedElement",0);
        editor = sharedPref.edit();
        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(FetchData.userID).child("Selected Element");


        final String[] selectTechnique= new String[]{"50/30/20","80/20"}; //Ad"Debt Avalanche", "Debt Snowfall"
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a budgeting plan");
        builder.setSingleChoiceItems(selectTechnique, selectedElement,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedElement) {
                        editor.putInt("selectedElement", selectedElement);
                        editor.apply();
                        current_user_db.setValue(selectedElement);
                    }
                });
        builder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void showDialog(){
        if(alert==null)
            SingleChoiceWithRadioButton();
        else
            alert.show();
    }
}