package com.example.masterchef.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.masterchef.R;
import com.example.masterchef.services.model.ModelVideo;
import com.example.masterchef.ui.adapter.AdapterVideo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProfileVideoFragment extends Fragment {


    private String title;
    private RecyclerView recyclerViewUserVideos;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelVideo> videoArrayList;
    private AdapterVideo adapterVideo;
    private ProgressDialog progressDialog;

    private TextView emptyState;

    public ProfileVideoFragment(String mTitle) {
        // Required empty public constructor
        this.title = mTitle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyState = view.findViewById(R.id.emptyState);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerViewUserVideos = view.findViewById(R.id.recyclerViewUserVideos);
        loadAllVideos();

    }

    private void loadAllVideos() {
        videoArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("VideoPosts");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    videoArrayList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        ModelVideo modelVideo = ds.getValue(ModelVideo.class);
                        videoArrayList.add(modelVideo);
                    }
                    if (videoArrayList.isEmpty()){
                        recyclerViewUserVideos.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    }else {
                        recyclerViewUserVideos.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.GONE);
                        //setup adapter
                        adapterVideo = new AdapterVideo(getContext(),videoArrayList);
                        recyclerViewUserVideos.setAdapter(adapterVideo);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }
}