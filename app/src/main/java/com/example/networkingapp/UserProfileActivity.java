package com.example.networkingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    ImageButton btnBack;
    ImageView userPicIv;
    TextView nameTv, descriptionTv;
    Button btnSub;
    RecyclerView postsRV;
    List<PostsClass> postsList;
    MyAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference userRef, postsRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        String userID = getIntent().getStringExtra("userId");

        btnBack = findViewById(R.id.btnBack);
        userPicIv = findViewById(R.id.userPicIv);
        nameTv = findViewById(R.id.nameTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        btnSub = findViewById(R.id.btnSub);
        postsRV = findViewById(R.id.postsRV);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
        postsRef = database.getReference("Posts");

        Query userQuery = userRef.orderByChild("id").equalTo(userID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ ds.child("name").getValue();
                    String description = ""+ ds.child("description").getValue();
                    String imageURL = ""+ ds.child("image").getValue();

                    if(name.isEmpty())
                        nameTv.setText("Названия проекта нет :(");
                    else
                        nameTv.setText(name);

                    if(description.isEmpty())
                        descriptionTv.setText("Описания проекта нет :(");
                    else
                        descriptionTv.setText(description);

                    try {
                        Picasso.get().load(imageURL).into(userPicIv);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.user_picture_default).into(userPicIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase User Profile", "Error: " + error.getMessage());
                Toast.makeText(UserProfileActivity.this, "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        postsRV.setLayoutManager(new LinearLayoutManager(UserProfileActivity.this));
        postsList = new ArrayList<>();
        adapter = new MyAdapter(UserProfileActivity.this, postsList);
        postsRV.setAdapter(adapter);

        Query postsQuery = postsRef.orderByChild("authorID").equalTo(userID);
        eventListener = postsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    PostsClass post = ds.getValue(PostsClass.class);
                    if(Objects.equals(post.getAuthorID(), userID)){
                        post.setAuthorName(nameTv.getText().toString());
                        postsList.add(post);
                    }
                }
                postsList.sort((p1, p2) -> Long.compare((Long) p2.getTimestamp(), (Long) p1.getTimestamp()));
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase User Profile", "Не удалось загрузить данные о постах, ошибка: " + error.getMessage());
                Toast.makeText(UserProfileActivity.this, "Не удалось загрузить данные о постах",
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndAddSubscription(userID);
            }
        });
    }
    private void checkAndAddSubscription(String userId){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        usersRef.child("subscriptions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    List<String> subscriptions = new ArrayList<>();
                    subscriptions.add(userId);
                    usersRef.child("subscriptions").setValue(subscriptions);
                }else{
                    List<String> subscriptions = (List<String>) snapshot.getValue();
                    if(!subscriptions.contains(userId)){
                        subscriptions.add(userId);
                        usersRef.child("subscriptions").setValue(subscriptions);
                    }else{
                        Toast.makeText(UserProfileActivity.this, "Вы уже подписаны на пользователя", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase User Profile", "Error: " + error.getMessage());
            }
        });
    }
}