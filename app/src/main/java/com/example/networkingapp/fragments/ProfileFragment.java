package com.example.networkingapp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkingapp.MyAdapter;
import com.example.networkingapp.PostsClass;
import com.example.networkingapp.ProfileEditActivity;
import com.example.networkingapp.R;
import com.example.networkingapp.StartupDashboardActivity;
import com.example.networkingapp.UploadPostActivity;
import com.google.android.material.button.MaterialButton;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    ImageView userPicIv;
    TextView nameTv, descriptionTv;
    MaterialButton editProfileButton, linksButton;
    String phone, phoneAdd, website, email;
    Button newPostBtn;
    RecyclerView recyclerView;
    List<PostsClass> postsList;
    ValueEventListener eventListener;


    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        userPicIv = view.findViewById(R.id.userPicIv);
        nameTv = view.findViewById(R.id.nameTv);
        descriptionTv = view.findViewById(R.id.descriptionTv);
        linksButton = view.findViewById(R.id.btnLinks);
        editProfileButton = view.findViewById(R.id.fab);
        newPostBtn = view.findViewById(R.id.newPostBtn);
        recyclerView = view.findViewById(R.id.postsRV);

        Query query = reference.orderByChild("id").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ ds.child("name").getValue();
                    String description = ""+ ds.child("description").getValue();
                    String imageURL = ""+ ds.child("image").getValue();

                    if(name.isEmpty())
                        nameTv.setText("Название вашего проекта");
                    else
                        nameTv.setText(name);

                    if(description.isEmpty())
                        descriptionTv.setText("Описание вашего проекта");
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
                Log.e("Firebase Profile", error.getMessage());
                //Toast.makeText(StartupDashboardActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        postsList = new ArrayList<>();

        MyAdapter adapter = new MyAdapter(getActivity(), postsList);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference("Posts");
        dialog.show();
        Query queryPosts = (Query) reference.orderByChild("authorID").equalTo(user.getUid());
        eventListener = queryPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    PostsClass post = ds.getValue(PostsClass.class);
                    if(Objects.equals(post.getAuthorID(), user.getUid())){
                        post.setAuthorName(snapshot.child("name").getValue(String.class));
                        postsList.add(post);
                    }
                }
                postsList.sort((p1, p2) -> Long.compare((Long) p2.getTimestamp(), (Long) p1.getTimestamp()));
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Profile", "Не удалось загрузить данные о постах, ошибка: " + error.getMessage());
                Toast.makeText(getActivity(), "Не удалось загрузить данные о постах",
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        linksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadPostActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void showBottomDialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        TextView phoneText = dialog.findViewById(R.id.textPhone);
        TextView phoneAddText = dialog.findViewById(R.id.textPhoneAdditional);
        TextView websiteText = dialog.findViewById(R.id.textWebsite);
        TextView emailText = dialog.findViewById(R.id.textEmail);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        Query query = reference.orderByChild("id").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    phone = ""+ ds.child("number").getValue();
                    phoneAdd = ""+ ds.child("additionalNumber").getValue();
                    website = ""+ ds.child("websiteLink").getValue();
                    email = ""+ ds.child("contactEmail").getValue();

                    if(phone.isEmpty())
                        phoneText.setText("Номер не указан");
                    else
                        phoneText.setText(phone);

                    if(phoneAdd.isEmpty())
                        phoneAddText.setText("Номер не указан");
                    else
                        phoneAddText.setText(phoneAdd);

                    if(website.isEmpty())
                        websiteText.setText("Адрес не указан");
                    else
                        websiteText.setText(website);

                    if(email.isEmpty())
                        emailText.setText("Почта не указана");
                    else
                        emailText.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        phone = Objects.requireNonNull(phoneText.getText()).toString().trim();
        phoneAdd = Objects.requireNonNull(phoneAddText.getText()).toString().trim();
        website = Objects.requireNonNull(websiteText.getText()).toString().trim();
        email = Objects.requireNonNull(emailText.getText()).toString().trim();

        phoneText.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Номер телефона скопирован", phone);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "Номер скопирован", Toast.LENGTH_SHORT).show();
        });

        phoneAddText.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Дополнительный номер телефона скопирован", phoneAdd);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "Номер скопирован", Toast.LENGTH_SHORT).show();
        });

        websiteText.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Адрес сайта скопирован", website);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "Адрес скопирован", Toast.LENGTH_SHORT).show();
        });

        emailText.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Почта скопирована", email);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "Почта скопирована", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}