package com.example.networkingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.networkingapp.fragments.HomeFragment;
import com.example.networkingapp.fragments.ProfileFragment;
import com.example.networkingapp.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        if(savedInstanceState==null){
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
    private final NavigationBarView.OnItemSelectedListener navListener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_home) {
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction ft1 = fragmentManager.beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;
                    } else if (itemId == R.id.nav_profile) {
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction ft2 = fragmentManager.beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            return true;
                    } else if (itemId == R.id.nav_users) {
                            SettingsFragment fragment3 = new SettingsFragment();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction ft3 = fragmentManager.beginTransaction();
                            ft3.replace(R.id.content, fragment3, "");
                            ft3.commit();
                            return true;
                    }
                    return false;
                }
            };
}