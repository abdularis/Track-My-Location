package com.github.abdularis.trackmylocation.dashboard;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.github.abdularis.trackmylocation.R;
import com.github.abdularis.trackmylocation.startupui.StartupActivity;
import com.github.abdularis.trackmylocation.sharelocation.ShareLocationActivity;
import com.github.abdularis.trackmylocation.tracklocation.TrackLocationActivity;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check for authentication
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            goToStartupActivity();
            return;
        }
        mAuth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                goToStartupActivity();
            }
        });

        // if it goes here user is already logged in
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            AuthUI.getInstance().signOut(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToStartupActivity() {
        Intent i = new Intent(this, StartupActivity.class);
        startActivity(i);
        finish();
    }

    public void onShareLocClick(View view) {
        Intent i = new Intent(this, ShareLocationActivity.class);
        startActivity(i);
    }

    public void onTrackLocClick(View view) {
        Intent i = new Intent(this, TrackLocationActivity.class);
        startActivity(i);
    }

    public void onProfileClick(View view) {
        Toast.makeText(this, "TODO: Implement this", Toast.LENGTH_SHORT).show();
    }
}
