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

import com.example.masterchef.services.listener.PostListener;
import com.example.masterchef.services.model.ModelVideo;
import com.example.masterchef.ui.adapter.AdapterPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RecipesFragment extends Fragment {


    private RecyclerView recyclerView;
    private AdapterPost adapterPost;
    private ArrayList<ModelVideo> postList;
    private ProgressBar progressBar;
    private TextView emptyState;


    public RecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_recipes, container, false);

        //view init
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);

        //recyclerview view its property
        recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //show newest post first
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        //init post list
        postList = new ArrayList<>();

        // load all video from database
        loadVideoPost();

        return view;
    }

    private void loadVideoPost() {
        progressBar.setVisibility(View.VISIBLE);
        //path of all videos
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("VideoPosts");

        //get all data from reference
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                }else {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelVideo modelPost = ds.getValue(ModelVideo.class);
                        //getting data from firebase add data in array list
                        postList.add(modelPost);
                        if (postList.isEmpty()){
                            progressBar.setVisibility(View.GONE);
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            emptyState.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        //adapter
                        adapterPost = new AdapterPost(getActivity(),postList);

                        //setAdapter to recyclerview
                        recyclerView.setAdapter(adapterPost);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}