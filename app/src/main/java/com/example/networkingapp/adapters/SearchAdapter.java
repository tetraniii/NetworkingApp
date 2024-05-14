package com.example.networkingapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networkingapp.R;
import com.example.networkingapp.StartupDashboardActivity;
import com.example.networkingapp.UserProfileActivity;
import com.example.networkingapp.classes.UserClass;
import com.example.networkingapp.fragments.MyProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private Context context;
    private List<UserClass> usersList;
    public SearchAdapter(Context context, List<UserClass> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {


        String uid = usersList.get(position).getUserID();

        holder.recName.setText(usersList.get(position).getUserName());
        holder.recDesc.setText(usersList.get(position).getUserDesc());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        holder.recName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(uid, currentUser.getUid())){
                    Activity activity = (Activity) context;
                    activity.finish();
                    Intent intent = new Intent(context, StartupDashboardActivity.class);
                    intent.putExtra("openMyProfileFragment", true);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("userId", uid);
                    context.startActivity(intent);
                }
            }
        });
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.recDesc.setMaxLines(Integer.MAX_VALUE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
    public void clear(){
        usersList.clear();
        notifyDataSetChanged();
    }
}
class SearchViewHolder extends RecyclerView.ViewHolder{

    CardView recCard;
    TextView recName, recDesc;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        recCard = itemView.findViewById(R.id.searchCard);
        recName = itemView.findViewById(R.id.userNameTextView);
        recDesc = itemView.findViewById(R.id.descTextView);
    }
}
