package com.example.moneytor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Object;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static TextView navUsername;
    public static TextView tv;
    private int exitCounter=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        FetchData process = new FetchData();
        process.execute();
        tv = (TextView) findViewById(R.id.textView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        int myColor = getResources().getColor(R.color.title);
        toolbar.setTitleTextColor(myColor);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.current_user_name);




        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        for(int i=0; i<FetchData.list.size();i++){
            System.out.println(i + " " + FetchData.list.get(i).getDate());
        }
    }


    @Override
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
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomePage.this, MainActivity.class);
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
            if(exitCounter<1){
                Toast.makeText(HomePage.this,"Press back again to logout.",Toast.LENGTH_SHORT).show();
                exitCounter++;
            } else {
//            MainActivity.emailId.setText("");
//            MainActivity.password.setText("");
                FirebaseAuth.getInstance().signOut();
                changeActivity(HomePage.this, MainActivity.class);
//            android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    public void changeActivity(Activity Current, Class Target){
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }

}
