package com.example.networkingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networkingapp.classes.PostsClass;
import com.example.networkingapp.R;
import com.example.networkingapp.StartupDashboardActivity;
import com.example.networkingapp.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<PostsClass> postsList;
    public MyAdapter(Context context, List<PostsClass> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(postsList.get(position).getPostImage()).into(holder.recImage);
        holder.recText.setText(postsList.get(position).getPostText());
        setAuthorNameAdapter(postsList.get(position).getAuthorID(), holder.recAuthor);
        int pos = holder.getAdapterPosition();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Object timestamp = postsList.get(position).getTimestamp();
        if(timestamp instanceof Long){
            long timestampLong = (long) timestamp;
            Date date = new Date(timestampLong);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
            String formattedDate = sdf.format(date);
            holder.recDate.setText(formattedDate);
        }
        holder.recAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(postsList.get(pos).getAuthorID(), currentUser.getUid())){
                    StartupDashboardActivity activity = (StartupDashboardActivity) context;
                    activity.openMyProfileFragment();
                }else{
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("userId", postsList.get(pos).getAuthorID());
                    context.startActivity(intent);
                }
            }
        });

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.recText.setMaxLines(Integer.MAX_VALUE);
            }
        });

    }
    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public void clear(){
        postsList.clear();
        notifyDataSetChanged(); // Сообщаем RecyclerView, что данные были изменены
    }

    private void setAuthorNameAdapter(String userID, TextView authorTextView){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    if(name!=null){
                        authorTextView.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Posts Adapter", "Error: " + error.getMessage());
                Toast.makeText(context.getApplicationContext(), "Не удалось загрузить данные", Toast.LENGTH_SHORT);
            }
        });
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView recText;
    TextView recAuthor;
    TextView recDate;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.postImage);
        recText = itemView.findViewById(R.id.postText);
        recCard = itemView.findViewById(R.id.postCard);
        recDate = itemView.findViewById(R.id.postDate);
        recAuthor = itemView.findViewById(R.id.postAuthor);
    }
}
