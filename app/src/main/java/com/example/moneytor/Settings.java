package com.example.moneytor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public int selectedElement;
    private TextView resetTV, deleteTV, logoutTV, budgetingTV;
    private AlertDialog.Builder builder;
    private SharedPreferences.Editor editor;
    private DrawerLayout drawer;
    private AlertDialog alert;
    private DatabaseReference current_user_db;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        user = FirebaseAuth.getInstance().getCurrentUser();
        resetTV = findViewById(R.id.change_pswd);
        deleteTV = findViewById(R.id.delete_account);
        logoutTV = findViewById(R.id.logout_settings);
        budgetingTV = findViewById(R.id.change_plan);
        deleteTV = findViewById(R.id.delete_account);

        builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", null);

        /*
         * Allows user to delete account
         *
         * If account deleted then it is removed from Firebase Authentication and all of the account's
         * corresponding data is deleted from the Firebase Realtime Database
         */
        deleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Settings.this);
                dialog.setTitle("Delete Account");
                dialog.setMessage("Are you sure you want to delete your account? Once deleted, your account cannot be recovered and all of your account data will be deleted.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(FetchData.userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                                    FirebaseDatabase.getInstance().getReference("Leaderboard").child(FetchData.userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                    changeActivity(Settings.this, MainActivity.class);
                                    Toast.makeText(Settings.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        // Allows the user to change their budgeting technique
        budgetingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        // Redirects user to password reset page
        resetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, ResetPassword.class);
                startActivityForResult(intent, 0);
            }
        });

        // Logs the user out of the Moneytor account and invalidates the Monzo access token
        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Settings.this);
                dialog.setTitle("Logout");
                dialog.setMessage("Are you sure you want to logout?");
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        changeActivity(Settings.this, MainActivity.class);
                    }
                });
                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

            }
        });

        // Sets toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        drawer = findViewById(R.id.drawer_layout);

        // Sets navigation bar
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


    /*
     * Redirects the user to another page depending on what they chose in the navigation bar
     *
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


    /*
     * onBackPressed() overridden to determine what's done when used presses back button
     *
     * Considers case that the navigation bar is open (in which case it is closed) and when it's not
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /*
     * Changes activity from current to target activity
     */
    public void changeActivity(Activity Current, Class Target) {
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }


    /*
     * This method offers the user the budgeting options available in a dialog view
     *
     * Once user has chosen their desired budgeting technique, their choice is stored in the Firebase Realtime Database
     */
    private void SingleChoiceWithRadioButton() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        selectedElement = sharedPref.getInt("selectedElement", 0);
        editor = sharedPref.edit();
        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(FetchData.userID).child("Selected Element");


        final String[] selectTechnique = new String[]{"50/30/20", "80/20"}; //Ad"Debt Avalanche", "Debt Snowfall"
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

    /*
     * Checks to see if a dialog (called alert) is initialised or not
     *
     * If dialog is not initialised then initialise the dialog by calling SingleChoiceWithRadioButton() method
     *
     * Else open dialog
     */
    private void showDialog() {
        if (alert == null)
            SingleChoiceWithRadioButton();
        else
            alert.show();
    }

    /*
     * Executed when the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        super.onDestroy();
    }
}