package com.example.masterchef.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masterchef.R;
import com.example.masterchef.services.model.ModelVideo;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder> {

    private Context context;
    private List<ModelVideo> modelVideos;

    public LeaderboardAdapter(Context context, List<ModelVideo> modelVideos) {
        this.context = context;
        this.modelVideos = modelVideos;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leaderboard_item,viewGroup,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ModelVideo modelVideo = modelVideos.get(i);

        myViewHolder.userName.setText(modelVideo.getUserName());
        myViewHolder.like.setText(modelVideo.getVideoLike());


    }

    @Override
    public int getItemCount() {
        return modelVideos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView profileImage;
        private TextView userName;
        private TextView like;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userName);
            like = itemView.findViewById(R.id.likeCount);
        }
    }
}
