package com.example.masterchef.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masterchef.R;

import com.example.masterchef.services.model.ModelVideo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyViewHolder> {

    private Context context;
    private ArrayList<ModelVideo> postList;


    String myUid;

    private DatabaseReference likeRef;
    private DatabaseReference postRef;

    boolean mVideoLike = false;




    public AdapterPost(Context context, ArrayList<ModelVideo> postList) {
        this.context = context;
        this.postList = postList;

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("VideoPosts");

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_timeline_item,viewGroup,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ModelVideo modelPosts = postList.get(i);

        //get data
        String videoId = modelPosts.getPostId();
        String videoTitle = modelPosts.getVideoTitle();
        String videoDescription = modelPosts.getVideDescription();
        String videLike = modelPosts.getVideoLike();
        String userId = modelPosts.getUid();
        String userName = modelPosts.getUserName();
        String userEmail = modelPosts.getUserEmail();
        String videoThumbnail = modelPosts.getVideoThumbnail();
        String userImage = modelPosts.getUserImage();
        String videTimeStamp = modelPosts.getTimeStamp();

//        //convert timestamp to dd/mm/yyyy hh:mm ap/pm
//        Calendar calendar = Calendar.getInstance(Locale.getDefault());
//        calendar.setTimeInMillis(Long.parseLong(videTimeStamp));
//        String time = DateFormat.format("dd/MM/yyy hh:mm aa",calendar).toString();

        //set data
        myViewHolder.videTitle.setText(videoTitle);
        myViewHolder.videoDescription.setText(videoDescription);
        myViewHolder.videoLikeTxt.setText(videLike);
//        myViewHolder.videoTime.setText(time);
        myViewHolder.userName.setText(userName);
        myViewHolder.userCity.setText(userEmail);

        setLikes(myViewHolder,videoId);

        try {
            Picasso.get().load(videoThumbnail).into(myViewHolder.videoThumbnail);
            Picasso.get().load(userImage).into(myViewHolder.userImage);
        }catch (Exception e){
            myViewHolder.videoThumbnail.setImageResource(R.drawable.food);
            myViewHolder.userImage.setImageResource(R.drawable.ic_baseline_person_24);
        }

        myViewHolder.videoLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int videoPostLike = Integer.parseInt(videLike);
                mVideoLike = true;

                //get id of post clicked
                final String videoPostId = videoId;
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mVideoLike){
                            if (dataSnapshot.child(videoPostId).hasChild(myUid)){
                                //already liked, so remove like
                                postRef.child(videoPostId).child("videoLike").setValue(""+(videoPostLike-1));
                                likeRef.child(videoPostId).child(myUid).removeValue();
                            }else {
                                postRef.child(videoPostId).child("videoLike").setValue(""+(videoPostLike+1));
                                likeRef.child(videoPostId).child(myUid).setValue("Liked");
                            }
                            mVideoLike = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void setLikes(MyViewHolder myViewHolder, String videoId) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.child(videoId).hasChild(myUid)){
                   myViewHolder.videoLikeBtn.setColorFilter(Color.GREEN);
               }else {
                   myViewHolder.videoLikeBtn.setColorFilter(Color.BLACK);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView videTitle,videoDescription,videoLikeTxt,videoTime,userName,userCity;
        private ImageView videoThumbnail,userImage,videoLikeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            videTitle = itemView.findViewById(R.id.video_title);
            videoDescription = itemView.findViewById(R.id.video_description);
            videoLikeTxt = itemView.findViewById(R.id.video_like_txt);
            userName = itemView.findViewById(R.id.userName);
            userCity = itemView.findViewById(R.id.userCity);
            videoTime = itemView.findViewById(R.id.videoTime);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            userImage = itemView.findViewById(R.id.userImage);
            videoLikeBtn = itemView.findViewById(R.id.video_like_btn);

        }
    }

}
