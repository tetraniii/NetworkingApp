package com.example.networkingapp;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        holder.recAuthor.setText(postsList.get(position).getAuthorName());
        Object timestamp = postsList.get(position).getTimestamp();
        if(timestamp instanceof Long){
            long timestampLong = (long) timestamp;
            Date date = new Date(timestampLong);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
            String formattedDate = sdf.format(date);
            holder.recDate.setText(formattedDate);
        }

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
