package com.example.networkingapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkingapp.SearchActivity;
import com.example.networkingapp.adapters.MyAdapter;
import com.example.networkingapp.classes.PostsClass;
import com.example.networkingapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GuestHomeFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference postsRef;
    DatabaseReference usersRef;
    TextInputEditText searchEditText;
    Button btnNewPosts, btnYourSub;
    RecyclerView postsRV;
    List<PostsClass> postsList;
    TextView noSubscriptionsTextView;
    MyAdapter adapter;
    ValueEventListener eventListener;
    ImageButton searchBtn;

    public GuestHomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guest_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        postsRef = database.getReference("Posts");
        usersRef = database.getReference("Users");

        searchEditText = view.findViewById(R.id.editTextName);
        searchBtn = view.findViewById(R.id.searchBtn);
        btnNewPosts = view.findViewById(R.id.btnNewPosts);
        btnYourSub = view.findViewById(R.id.btnYourSub);
        noSubscriptionsTextView = view.findViewById(R.id.no_subscriptions_text_view);
        postsRV = view.findViewById(R.id.postsRV);

        postsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        postsList = new ArrayList<>();
        adapter = new MyAdapter(getActivity(), postsList);
        postsRV.setAdapter(adapter);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString().trim();
                if(!searchQuery.isEmpty()){
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("searchQuery", searchQuery);
                    startActivity(intent);
                }
            }
        });

        btnNewPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPosts();
            }
        });
        btnYourSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference subscriptionsRef = usersRef.child(user.getUid()).child("subscriptions");
                subscriptionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> subscriptions = new ArrayList<>();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String subscriptionsID = ds.getValue(String.class);
                            subscriptions.add(subscriptionsID);
                        }
                        loadSubscriptionsPosts(subscriptions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase Home", "Error: " + error.getMessage());
                    }
                });
            }
        });

        // По умолчанию показываем все посты
        loadPosts();

        return view;
    }
    private void loadSubscriptionsPosts(List<String> subscriptions){
        hideNoSubscriptionsMessage();
        adapter.clear();
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(subscriptions.isEmpty()){
                    // Если список подписок не существует или пуст, показываем сообщение об отсутствии подписок
                    showNoSubscriptionsMessage();
                }else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        PostsClass post = ds.getValue(PostsClass.class);
                        if (subscriptions.contains(post.getAuthorID())) {
                            postsList.add(post);
                        }
                    }
                    postsList.sort((p1, p2) -> Long.compare((Long) p2.getTimestamp(), (Long) p1.getTimestamp()));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Home", "Не удалось загрузить данные о постах, ошибка: " + error.getMessage());
                Toast.makeText(getActivity(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        hideNoSubscriptionsMessage();
        adapter.clear();
        //Загружаем все посты
        Query query = FirebaseDatabase.getInstance().getReference("Posts").limitToFirst(100); // Пример: загрузить 100 последних постов
        eventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PostsClass post = ds.getValue(PostsClass.class);
                    postsList.add(post);
                }
                postsList.sort((p1, p2) -> Long.compare((Long) p2.getTimestamp(), (Long) p1.getTimestamp()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Home", "Не удалось загрузить данные о постах, ошибка: " + error.getMessage());
                Toast.makeText(getActivity(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNoSubscriptionsMessage(){
        noSubscriptionsTextView.setVisibility((View.VISIBLE));
        postsRV.setVisibility(View.GONE);
    }
    private void hideNoSubscriptionsMessage(){
        noSubscriptionsTextView.setVisibility((View.GONE));
        postsRV.setVisibility(View.VISIBLE);
    }
}