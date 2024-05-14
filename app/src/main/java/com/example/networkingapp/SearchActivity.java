package com.example.networkingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.networkingapp.adapters.SearchAdapter;
import com.example.networkingapp.classes.UserClass;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    RecyclerView searchResRV;
    List<UserClass> usersList;
    SearchAdapter adapter;
    TextInputEditText searchEditText;
    ImageButton searchBtn, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchResRV = findViewById(R.id.searchResultRV);
        searchEditText = findViewById(R.id.searchEditText);
        searchBtn = findViewById(R.id.searchBtn);
        btnBack = findViewById(R.id.btnBack);

        String searchQuery = getIntent().getStringExtra("searchQuery");

        searchEditText.setText(searchQuery);

        searchResRV.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        usersList = new ArrayList<>();
        adapter = new SearchAdapter(this, usersList);
        searchResRV.setAdapter(adapter);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query queryByName = usersRef.orderByChild("name");
        queryByName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    UserClass user = new UserClass();
                    user.setId(ds.child("id").getValue(String.class));
                    user.setDesc(ds.child("description").getValue(String.class));
                    user.setName(ds.child("name").getValue(String.class));
                    if (user != null && user.getUserName() != null && user.getUserDesc() != null &&
                            (user.getUserName().contains(searchQuery) || user.getUserDesc().contains(searchQuery))){
                        usersList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Search", "Error: " + error.getMessage());
                Toast.makeText(SearchActivity.this, "Ошибка: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString().trim();
                if(!searchQuery.isEmpty()){
                    Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                    intent.putExtra("searchQuery", searchQuery);
                    startActivity(intent);
                }
            }
        });
    }
}