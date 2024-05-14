package com.example.networkingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;
import java.util.Objects;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubViewHolder>{
    private Context context;
    private List<String> subscriptionsList;
    public SubscriptionsAdapter(Context context, List<String> subscriptionsList) {
        this.context = context;
        this.subscriptionsList = subscriptionsList;
    }
    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subsriptions_item, parent, false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubViewHolder holder, int position) {
        String uid = subscriptionsList.get(position);

        setAuthorNameAdapter(uid, holder.recName);

        int pos = holder.getAdapterPosition();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        holder.recName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(subscriptionsList.get(pos), currentUser.getUid())){
                    StartupDashboardActivity activity = (StartupDashboardActivity) context;
                    activity.openMyProfileFragment();
                }else{
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("userId", subscriptionsList.get(pos));
                    context.startActivity(intent);
                }
            }
        });

        holder.recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSubscription(uid);
            }
        });

    }

    @Override
    public int getItemCount() {
        return subscriptionsList.size();
    }

    public void clear(){
        subscriptionsList.clear();
        notifyDataSetChanged(); // Сообщаем RecyclerView, что данные были изменены
    }

    private void removeSubscription(String userId){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        usersRef.child("subscriptions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    List<String> subscriptions = (List<String>) snapshot.getValue();
                    if(subscriptions.contains(userId)){
                        subscriptions.remove(userId);
                        usersRef.child("subscriptions").setValue(subscriptions);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(), "Ошибка при попытке отписаться", Toast.LENGTH_SHORT).show();
                Log.e("Firebase Subs", "Error: " + error.getMessage());
            }
        });
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
                Toast.makeText(context.getApplicationContext(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
class SubViewHolder extends RecyclerView.ViewHolder{
    CardView recCard;
    TextView recName;
    Button recButton;

    public SubViewHolder(@NonNull View itemView) {
        super(itemView);

        recCard = itemView.findViewById(R.id.subCard);
        recButton = itemView.findViewById(R.id.subButton);
        recName = itemView.findViewById(R.id.subNameTextView);
    }
}

