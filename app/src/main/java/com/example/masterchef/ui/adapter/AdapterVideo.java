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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.MyViewHolder> {

    private Context context;
    private ArrayList<ModelVideo> videoList;

    public AdapterVideo(Context context, ArrayList<ModelVideo> videoList) {
        this.context = context;
        this.videoList = videoList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user_video_item,parent,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //get data
        ModelVideo modelVideo = videoList.get(position);
        String videoId = modelVideo.getVideoId();
        String uid = modelVideo.getUid();
        String videTitle = modelVideo.getVideoTitle();
        String videDescription = modelVideo.getVideDescription();
        String videoCategory = modelVideo.getVideoCategory();
        String videoThumbnail = modelVideo.getVideoThumbnail();

        //set data
        holder.videoTitle.setText(videTitle);
        try {
            Picasso.get().load(videoThumbnail).into(holder.videoThumbnail);
        }catch (Exception e){
            holder.videoThumbnail.setImageResource(R.drawable.food);
        }



    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView videoThumbnail;
        private TextView videoTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            videoTitle = itemView.findViewById(R.id.video_title);

        }
    }

}
