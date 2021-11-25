package com.example.masterchef.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.masterchef.R;
import com.example.masterchef.services.model.ModelVideo;
import com.example.masterchef.ui.adapter.AdapterUserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private String name,email,image,status;

    private ImageView userImage,onlineStatus,followUser,imageBack;
    private TextView userName,userEmail,userDetails;

    private RecyclerView recyclerView;
    private AdapterUserProfile adapterUserProfile;
    private ArrayList<ModelVideo> modelVideos;

    private TextView emptyState;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();

        //show newest post first
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        //get userId
        Intent intent = getIntent();
        if (intent != null){
            String userId = intent.getStringExtra("userId");
            if (!userId.equals(firebaseAuth.getCurrentUser().getUid())) {
                loadUserDetail(userId);
                loadVideoInfo(userId);
            }else {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }

        }

        followUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFollowing();
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private void init(){
        userImage =findViewById(R.id.userImage);
        userName =findViewById(R.id.userName);
        userEmail =findViewById(R.id.userEmail);
        onlineStatus = findViewById(R.id.onlineStatus);
        followUser = findViewById(R.id.followUser);
        recyclerView = findViewById(R.id.recyclerview);
        emptyState = findViewById(R.id.emptyState);
        imageBack = findViewById(R.id.imageBack);
    }


    private void loadVideoInfo(String userId) {

        modelVideos = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("VideoPosts");
        databaseReference.orderByChild("uid").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelVideos.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelVideo modelVideo = ds.getValue(ModelVideo.class);
                            modelVideos.add(modelVideo);
                        }

                        if (modelVideos.isEmpty()){
                            recyclerView.setVisibility(View.GONE);
                            emptyState.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyState.setVisibility(View.GONE);
                            //setup Adapter
                            adapterUserProfile = new AdapterUserProfile(getApplicationContext(),modelVideos);
                            recyclerView.setAdapter(adapterUserProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void loadUserDetail(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            name = ""+ds.child("name").getValue();
                            email= ""+ds.child("email").getValue();
                            image = ""+ds.child("profileImage").getValue();
                            status = ""+ds.child("online").getValue();

                            //setData
                            userName.setText(name);
                            userEmail.setText(email);

                            try {
                                Picasso.get().load(image).into(userImage);
                            }catch (Exception e){
                                userImage.setImageResource(R.drawable.ic_baseline_person_24);
                            }

                            if (status.equals("false")){
                                onlineStatus.setImageResource(R.drawable.offline_status_background);
                            }else {
                                onlineStatus.setImageResource(R.drawable.online_status_background);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void addFollowing() {
    }

}