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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkingapp.MyAdapter;
import com.example.networkingapp.PostsClass;
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
    TextInputEditText editTextName;
    Button btnNewPosts, btnYourSub;
    RecyclerView postsRV;
    List<PostsClass> postsList;
    TextView noSubscriptionsTextView;
    MyAdapter adapter;
    ValueEventListener eventListener;

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

        editTextName = view.findViewById(R.id.editTextName);
        btnNewPosts = view.findViewById(R.id.btnNewPosts);
        btnYourSub = view.findViewById(R.id.btnYourSub);
        noSubscriptionsTextView = view.findViewById(R.id.no_subscriptions_text_view);
        postsRV = view.findViewById(R.id.postsRV);

        postsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        postsList = new ArrayList<>();
        adapter = new MyAdapter(getActivity(), postsList);
        postsRV.setAdapter(adapter);

        btnNewPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPosts(true);
            }
        });
        btnYourSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPosts(false);
            }
        });

        // По умолчанию показываем все посты
        loadPosts(true);

        return view;
    }

    private void loadPosts(boolean showAllPosts){
        hideNoSubscriptionsMessage();
        adapter.clear();
        if(showAllPosts){
            //Загружаем все посты
            Query query = FirebaseDatabase.getInstance().getReference("Posts").limitToFirst(100); // Пример: загрузить 100 последних постов
            eventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()){
                        PostsClass post = ds.getValue(PostsClass.class);
                        String userId = post.getAuthorID();
                        String userName = getUserName(userId);
                        post.setAuthorName(userName);
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
        } else{
            // Загружаем посты подписок текущего пользователя
            Query query = usersRef.child(user.getUid()).child("subscriptions");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        // Если список подписок не существует или пуст, показываем сообщение об отсутствии подписок
                        showNoSubscriptionsMessage();
                    }else{
                        // Если список подписок существует, загружаем посты подписок текущего пользователя
                        List<String> subscriptions = new ArrayList<>();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            subscriptions.add(ds.getKey());
                        }
                        loadPostsForSubscriptions(subscriptions);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadPostsForSubscriptions(final List<String> subscriptions){
        adapter.clear();
        for(final String userID : subscriptions){
            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("authorID").equalTo(userID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        PostsClass post = ds.getValue(PostsClass.class);
                        postsList.add(post);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showNoSubscriptionsMessage(){
        noSubscriptionsTextView.setVisibility((View.VISIBLE));
        postsRV.setVisibility(View.GONE);
    }
    private void hideNoSubscriptionsMessage(){
        noSubscriptionsTextView.setVisibility((View.GONE));
        postsRV.setVisibility(View.VISIBLE);
    }
    public static String getUserName(String userId) {
        final String[] userName = {null}; // Используем массив, чтобы хранить значение внутри анонимного класса

        // Ссылка на узел "users" в вашей базе данных Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Запрос к базе данных Firebase для получения имени пользователя по его id
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Проверяем, существует ли пользователь с данным id
                if (dataSnapshot.exists()) {
                    // Получаем имя пользователя из снимка данных
                    String name = dataSnapshot.child("name").getValue(String.class);
                    userName[0] = name; // Сохраняем имя пользователя
                } else {
                    // Если пользователь с данным id не найден, можно выполнить соответствующие действия
                    // Например, вернуть null или какое-то стандартное значение
                    userName[0] = "Пользователь удален";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки при чтении из базы данных Firebase
                Log.e("Firebase Home", "Ошибка чтения имени пользователя: " + databaseError.getMessage());
            }
        });

        return userName[0]; // Возвращаем имя пользователя
    }
}