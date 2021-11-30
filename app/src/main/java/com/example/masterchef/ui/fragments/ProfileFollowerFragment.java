package com.example.masterchef.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.masterchef.R;
import com.example.masterchef.services.model.ModelUser;
import com.example.masterchef.services.model.ModelVideo;
import com.example.masterchef.ui.adapter.AdapterFollower;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProfileFollowerFragment extends Fragment {

   private String title;
   private RecyclerView recyclerView;
   private TextView emptyState;
   private AdapterFollower adapterFollower;
   private ArrayList<ModelUser> followerList;
   private FirebaseAuth firebaseAuth;
   private String myUid;
   private ProgressBar progressBar;



    public ProfileFollowerFragment(String mTitle) {
        // Required empty public constructor
        title = mTitle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_followe, container, false);

        //view init
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.emptyState);

        //firebase initialization
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getCurrentUser().getUid();

        //recyclerview setup
        recyclerView = view.findViewById(R.id.recyclerviewFollower);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //show newest post first
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        loadFollowerId();
        return view;
    }

    private void loadFollowerId() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Followers");
        reference.child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              for (DataSnapshot ds: snapshot.getChildren()){
                  String id = ""+ds.getRef().getKey();
                  loadFollowerInfo(id);
              }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFollowerInfo(String id) {

        followerList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelFollower = ds.getValue(ModelUser.class);
                    followerList.add(modelFollower);
                }
                if (followerList.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);

                    adapterFollower = new AdapterFollower(getContext(),followerList);
                    recyclerView.setAdapter(adapterFollower);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}