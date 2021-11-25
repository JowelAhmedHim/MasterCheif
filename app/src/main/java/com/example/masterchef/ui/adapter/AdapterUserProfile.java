package com.example.masterchef.ui.adapter;

import android.content.Context;
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
import java.util.List;
import java.util.Locale;

public class AdapterUserProfile extends RecyclerView.Adapter<AdapterUserProfile.UserProfileViewHolder> {

    private Context context;
    private ArrayList<ModelVideo> postList;
    private LayoutInflater layoutInflater;


    String myUid;

    private DatabaseReference likeRef;
    private DatabaseReference userRef;
    private DatabaseReference postRef;
    private DatabaseReference favRef;

    boolean mVideoLike = false;
    boolean mVideoFab = false;


    public AdapterUserProfile(Context context, ArrayList<ModelVideo> postList) {
        this.context = context;
        this.postList = postList;

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("VideoPosts");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        favRef = FirebaseDatabase.getInstance().getReference().child("Favourite");
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context);
        }
        View view = layoutInflater.inflate(R.layout.row_profile_video, parent, false);
        return new UserProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {

        ModelVideo modelPosts = postList.get(position);

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
        holder.videTitle.setText(videoTitle);
        holder.videoDescription.setText(videoDescription);
        holder.videoLikeTxt.setText(videLike);
        holder.videoTime.setText(time);

        try {
            Picasso.get().load(videoThumbnailUri).into(holder.videoThumbnail);

        } catch (Exception e) {
            holder.videoThumbnail.setImageResource(R.drawable.food);

        }


        //function for like & favourite button view colour
        setLikes(holder, videoId);
        setFav(holder, videoId);

        //add like for this video in firebase
        holder.videoLikeBtn.setOnClickListener(new View.OnClickListener() {
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
                                //already liked, so remove like
                                postRef.child(videoPostId).child("videoLike").setValue("" + (videoPostLike - 1));
//                                postRef.child(videoPostId).child("userPopularity").setValue(""+(p-1));
                                likeRef.child(videoPostId).child(myUid).removeValue();
//                                userRef.child(modelPosts.getUid()).child("popularity").setValue(""+(p-1));

                            } else {
                                postRef.child(videoPostId).child("videoLike").setValue("" + (videoPostLike + 1));
//                                postRef.child(videoPostId).child("userPopularity").setValue(""+(p+1));
//                                userRef.child(modelPosts.getUid()).child("popularity").setValue(""+(p+1));
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


        //add this post as favourite video in firebase
        holder.favouritePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
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

    }

    //change like btn colour by checking video like
    private void setLikes(AdapterUserProfile.UserProfileViewHolder myViewHolder, String videoId) {
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
    private void setFav(AdapterUserProfile.UserProfileViewHolder myViewHolder, String videoId) {

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

    public class UserProfileViewHolder extends RecyclerView.ViewHolder {

        private TextView videTitle, videoDescription, videoLikeTxt, videoTime;
        private ImageView videoThumbnail, videoLikeBtn, favouritePostBtn;

        public UserProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            videTitle = itemView.findViewById(R.id.video_title);
            videoDescription = itemView.findViewById(R.id.video_description);
            videoLikeTxt = itemView.findViewById(R.id.video_like_txt);
            videoTime = itemView.findViewById(R.id.videoTime);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            videoLikeBtn = itemView.findViewById(R.id.video_like_btn);
            favouritePostBtn = itemView.findViewById(R.id.favouritePost);
        }
    }
}
