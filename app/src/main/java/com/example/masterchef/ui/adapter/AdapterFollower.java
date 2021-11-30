package com.example.masterchef.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masterchef.R;
import com.example.masterchef.services.model.ModelUser;
import com.example.masterchef.services.model.ModelVideo;
import com.google.android.material.transition.Hold;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterFollower extends RecyclerView.Adapter<AdapterFollower.FollowerViewHolder> {

    private Context context;
    private ArrayList<ModelUser> userList;
    private LayoutInflater layoutInflater;
    private Boolean mFollow = false;

    private DatabaseReference followRef;
    private String myUid;

    public AdapterFollower(Context context, ArrayList<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        followRef = FirebaseDatabase.getInstance().getReference().child("Followers");
        myUid = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context);
        }
        View view = layoutInflater.inflate(R.layout.row_follower, parent, false);
        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {

        ModelUser modelUser = userList.get(position);

        //getData
        String userId = "" + modelUser.getUid();
        String userName = "" + modelUser.getName();
        String userEmail = "" + modelUser.getEmail();
        String userImage = "" + modelUser.getProfileImage();
        String onlineStatus = "" + modelUser.getOnline();

        //setData
        holder.userName.setText(userName);
        holder.userEmail.setText(userEmail);
        try {
            Picasso.get().load(userImage).into(holder.profileImage);
        } catch (Exception e) {
            holder.profileImage.setImageResource(R.drawable.ic_baseline_person_24);
        }
        if (onlineStatus.equals("false")) {
            holder.online_status.setImageResource(R.drawable.offline_status_background);
        } else {
            holder.online_status.setImageResource(R.drawable.online_status_background);
        }

        setFollowImage(holder, userId);


        holder.followIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFollow = true;

                followRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mFollow) {
                            if (snapshot.child(userId).hasChild(myUid)) {
                                //already follow, so remove follow
                                followRef.child(userId).removeValue();
                            } else {
                                followRef.child(userId).child(myUid).setValue("Follow");
                            }
                            mFollow = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }

    private void setFollowImage(FollowerViewHolder holder, String userId) {

        followRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userId).hasChild(myUid)) {
                    holder.followIv.setImageResource(R.drawable.ic_baseline_person_24);
                    holder.followIv.setColorFilter(Color.GREEN);
                } else {
                    holder.followIv.setImageResource(R.drawable.ic_baseline_person_add_24);
                    holder.followIv.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class FollowerViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImage, online_status, followIv;
        private TextView userName, userEmail;


        public FollowerViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.userImage);
            online_status = itemView.findViewById(R.id.onlineStatus);
            followIv = itemView.findViewById(R.id.followUser);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);

        }
    }
}
