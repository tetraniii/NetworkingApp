package com.example.networkingapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkingapp.R;
import com.example.networkingapp.classes.SubscriptionsClass;
import com.example.networkingapp.classes.UserClass;
import com.example.networkingapp.adapters.SubscriptionsAdapter;
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

public class SubscriptionsFragment extends Fragment {

    SubscriptionsAdapter adapter;
    RecyclerView subsRV;
    List<String> subscriptionsList;
    TextView noSubscriptionsTextView;
    ValueEventListener eventListener;

    public SubscriptionsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();

        subsRV = view.findViewById(R.id.subsRecyclerView);
        noSubscriptionsTextView = view.findViewById(R.id.no_subscriptions_text_view);

        subsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        subscriptionsList = new ArrayList<>();
        adapter = new SubscriptionsAdapter(getActivity(), subscriptionsList);
        subsRV.setAdapter(adapter);

        Query query = FirebaseDatabase.getInstance().getReference("Users").child(currentUser).child("subscriptions");
        hideNoSubscriptionsMessage();
        eventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    subscriptionsList.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        subscriptionsList.add(ds.getValue(String.class));
                    }
                    adapter.notifyDataSetChanged();

                }else{
                    showNoSubscriptionsMessage();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Subs", "Error: " + error.getMessage());
            }
        });


        return view;
    }
    private void loadSubs(){
        hideNoSubscriptionsMessage();
        adapter.clear();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Query subscriptionsRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("subscriptions");
        eventListener = subscriptionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String subscriptionsID = ds.getValue(String.class);
                        subscriptionsList.add(subscriptionsID);
                    }
                    adapter.notifyDataSetChanged();
                    //loadSubsList(subscriptions);
                }else{
                    showNoSubscriptionsMessage();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Subs", "Error: " + error.getMessage());
            }
        });
    }

    private void loadSubsList(List<String> subscriptions){
        hideNoSubscriptionsMessage();
        adapter.clear();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(subscriptions.isEmpty()){
                    // Если список подписок не существует или пуст, показываем сообщение об отсутствии подписок
                    showNoSubscriptionsMessage();
                }else {
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String uid = ds.getKey();
                        if(subscriptions.contains(uid)){
                            subscriptionsList.add(uid);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Subs", "Не удалось загрузить данные о постах, ошибка: " + error.getMessage());
                Toast.makeText(getActivity(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showNoSubscriptionsMessage(){
        noSubscriptionsTextView.setVisibility((View.VISIBLE));
        subsRV.setVisibility(View.GONE);
    }
    private void hideNoSubscriptionsMessage(){
        noSubscriptionsTextView.setVisibility((View.GONE));
        subsRV.setVisibility(View.VISIBLE);
    }
}