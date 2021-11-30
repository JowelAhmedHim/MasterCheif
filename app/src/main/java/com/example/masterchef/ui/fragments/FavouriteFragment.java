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
import com.example.masterchef.services.model.ModelVideo;
import com.example.masterchef.ui.adapter.AdapterPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FavouriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterPost adapterPost;
    private ArrayList<ModelVideo> postList;
    private TextView emptyState;
    private ProgressBar progressBar;

    private String uid;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favourite, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
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

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Favourite");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    String postId = ""+ds.getRef().getKey();
                    getUserInfo(postId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;

    }



    private void getUserInfo(String postId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("VideoPosts");
        reference.orderByChild("postId").equalTo(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){

                            ModelVideo modelPost = ds.getValue(ModelVideo.class);

                            //getting data from firebase add data in array list
                            postList.add(modelPost);
                            if (postList.isEmpty()){
                                Toast.makeText(getContext(), "Empty", Toast.LENGTH_SHORT).show();
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}