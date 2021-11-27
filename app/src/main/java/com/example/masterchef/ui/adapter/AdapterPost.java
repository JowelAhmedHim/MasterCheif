package com.example.masterchef.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masterchef.R;

import com.example.masterchef.services.listener.PostListener;
import com.example.masterchef.services.model.ModelVideo;
import com.example.masterchef.ui.activities.MoviePlayerActivity;
import com.example.masterchef.ui.activities.ProfileActivity;
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

    private LayoutInflater layoutInflater;


    String myUid;

    private DatabaseReference likeRef;
    private DatabaseReference userRef;
    private DatabaseReference postRef;
    private DatabaseReference favRef;
    private DatabaseReference rankRef;

    boolean mVideoLike = false;
    boolean mVideoFab = false;


    public AdapterPost(Context context, ArrayList<ModelVideo> postList) {
        this.context = context;
        this.postList = postList;

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("VideoPosts");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        favRef = FirebaseDatabase.getInstance().getReference().child("Favourite");
        rankRef = FirebaseDatabase.getInstance().getReference().child("Ranks");


    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context);
        }
        View view = layoutInflater.inflate(R.layout.row_timeline_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ModelVideo modelPosts = postList.get(i);

        //get data
        String videoId = modelPosts.getPostId();
        String videoTitle = modelPosts.getVideoTitle();
        String videoDescription = modelPosts.getVideoDescription();
        String videoCategory = modelPosts.getVideoCategory();
        String videoUri = modelPosts.getVideoUrl();
        String videLike = modelPosts.getVideoLike();
        String userId = modelPosts.getUid();
        String userName = modelPosts.getUserName();
        String userEmail = modelPosts.getUserEmail();
        String userPopularity = modelPosts.getUserPopularity();
        String videoThumbnailUri = modelPosts.getVideoThumbnail();
        String userImageUri = modelPosts.getUserImage();
        String videTimeStamp = modelPosts.getTimeStamp();

        //convert timestamp to dd/mm/yyyy hh:mm ap/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(videTimeStamp));
        String time = DateFormat.format("dd/MM/yyy hh:mm aa", calendar).toString();

        //set data
        myViewHolder.videTitle.setText(videoTitle);
        myViewHolder.videoDescription.setText(videoDescription);
        myViewHolder.videoLikeTxt.setText(videLike);
        myViewHolder.videoTime.setText(time);
        myViewHolder.userName.setText(userName);
        myViewHolder.userCity.setText(userEmail);

        try {
            Picasso.get().load(videoThumbnailUri).into(myViewHolder.videoThumbnail);
            Picasso.get().load(userImageUri).into(myViewHolder.userImage);
        } catch (Exception e) {
            myViewHolder.videoThumbnail.setImageResource(R.drawable.food);
            myViewHolder.userImage.setImageResource(R.drawable.ic_baseline_person_24);
        }

        //function for like & favourite button view colour
        setLikes(myViewHolder, videoId);
        setFav(myViewHolder, videoId);


        //add like for this video in firebase
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
                        if (mVideoLike) {
                            if (dataSnapshot.child(videoPostId).hasChild(myUid)) {

                                int updatePostLike = videoPostLike -1;
                                //already liked, so remove like
                                postRef.child(videoPostId).child("videoLike").setValue("" +updatePostLike);
                                likeRef.child(videoPostId).child("score").setValue(1);


                            } else {

                                int updatePostLike = videoPostLike +1;
                                postRef.child(videoPostId).child("videoLike").setValue("" + updatePostLike);
                                likeRef.child(videoPostId).child(myUid).setValue("Liked");

                                rankRef.child(userId).setValue(""+myUid);

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

        //add this post as favourite video in firebase
        myViewHolder.favouritePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoFab = true;
                favRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mVideoFab) {
                            if (snapshot.child(myUid).hasChild(videoId)) {
                                //already in favourite list ,remove it
                                favRef.child(myUid).child(videoId).removeValue();
                            } else {
                                // add in favourite list
                                favRef.child(myUid).child(videoId).setValue("Favourite");
                            }
                            mVideoFab = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        myViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDetail(userId);
            }
        });

        myViewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDetail(userId);
            }
        });

        //item clicked listener,send to exoplayer to show video
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MoviePlayerActivity.class);
                intent.putExtra("videoUrl", "" + videoUri);
                context.startActivity(intent);
            }
        });


    }

    private void showUserDetail(String userId) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("userId",""+userId);
        context.startActivity(intent);
    }

    //change like btn colour by checking video like
    private void setLikes(MyViewHolder myViewHolder, String videoId) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(videoId).hasChild(myUid)) {
                    myViewHolder.videoLikeBtn.setColorFilter(Color.GREEN);
                } else {
                    myViewHolder.videoLikeBtn.setColorFilter(Color.BLACK);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //change favourite btn colour by checking favourite video
    private void setFav(MyViewHolder myViewHolder, String videoId) {

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(myUid).hasChild(videoId)) {
                    myViewHolder.favouritePostBtn.setColorFilter(Color.RED);
                } else {
                    myViewHolder.favouritePostBtn.setColorFilter(Color.BLACK);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView videTitle, videoDescription, videoLikeTxt, videoTime, userName, userCity;
        private ImageView videoThumbnail, userImage, videoLikeBtn, favouritePostBtn;

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
            favouritePostBtn = itemView.findViewById(R.id.favouritePost);

        }



    }

}
