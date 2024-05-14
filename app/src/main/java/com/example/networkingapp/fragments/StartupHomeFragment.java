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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.networkingapp.R;
import com.example.networkingapp.SearchActivity;
import com.example.networkingapp.adapters.MyAdapter;
import com.example.networkingapp.classes.PostsClass;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StartupHomeFragment extends Fragment {

    TextInputEditText searchEditText;
    RecyclerView postsRV;
    ImageButton searchBtn;
    List<PostsClass> postsList;
    MyAdapter adapter;
    ValueEventListener eventListener;

    public StartupHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startup_home, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        postsRV = view.findViewById(R.id.postsRV);
        searchBtn = view.findViewById(R.id.searchBtn);

        postsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        postsList = new ArrayList<>();
        adapter = new MyAdapter(getActivity(), postsList);
        postsRV.setAdapter(adapter);

        loadPosts();

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

        return view;
    }
    private void loadPosts(){
        adapter.clear();
        Query query = FirebaseDatabase.getInstance().getReference("Posts").limitToFirst(100);
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
}