package com.example.networkingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.networkingapp.fragments.MyProfileFragment;
import com.example.networkingapp.fragments.SettingsFragment;
import com.example.networkingapp.fragments.StartupHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StartupDashboardActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.startupNavigation);
        bottomNav.setOnItemSelectedListener(navListener);

        if(savedInstanceState==null){
            bottomNav.setSelectedItemId(R.id.startup_nav_home);
        }
        if(getIntent().getBooleanExtra("openMyProfileFragment", false)){
            openMyProfileFragment();
        }
    }
    private final NavigationBarView.OnItemSelectedListener navListener =
            item -> {

                int itemId = item.getItemId();
                if (itemId == R.id.startup_nav_home) {
                        StartupHomeFragment fragment1 = new StartupHomeFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction ft1 = fragmentManager.beginTransaction();
                        ft1.replace(R.id.startupContent, fragment1, "");
                        ft1.commit();
                        return true;
                } else if (itemId == R.id.startup_nav_profile) {
                        MyProfileFragment fragment2 = new MyProfileFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction ft2 = fragmentManager.beginTransaction();
                        ft2.replace(R.id.startupContent, fragment2, "");
                        ft2.commit();
                        return true;
                } else if (itemId == R.id.startup_nav_users) {
                        SettingsFragment fragment3 = new SettingsFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction ft3 = fragmentManager.beginTransaction();
                        ft3.replace(R.id.startupContent, fragment3, "");
                        ft3.commit();
                        return true;
                }
                return false;
            };
    public void openMyProfileFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.startupContent, new MyProfileFragment())
                .addToBackStack(null)
                .commit();
    }
}