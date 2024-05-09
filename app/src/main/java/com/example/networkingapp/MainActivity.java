package com.example.networkingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference usersRef = mDatabase.getReference("Users").child(currentUser.getUid());
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        boolean userIsStartup = snapshot.child("userIsStartup").getValue(Boolean.class);
                        if(userIsStartup){
                            startActivity(new Intent(MainActivity.this, StartupDashboardActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(MainActivity.this, GuestDashboardActivity.class));
                            finish();
                        }
                    }else{
                        Log.e("Firebase Main", "Данные о пользователе не найдены. Main");
                        Toast.makeText(MainActivity.this, "Данные о пользователе не найдены. Main", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    int errorCode = error.getCode();
                    String errorMessage = error.getMessage();
                    Log.e("Firebase Main", "Database error: "+ errorCode+ " " + errorMessage);
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
        } else {
            Log.e("FirebaseMain", "Нет текущего пользователя");
            Toast.makeText(MainActivity.this, "Нет текущего пользователя", Toast.LENGTH_SHORT).show();
            Intent intent;
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
