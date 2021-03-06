package com.example.masterchef.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masterchef.R;
import com.example.masterchef.services.model.ModelVideo;
import com.example.masterchef.ui.activities.MoviePlayerActivity;
import com.example.masterchef.ui.activities.UploadVideo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.MyViewHolder> {

    private Context context;
    private ArrayList<ModelVideo> videoList;

    String myUid;

    public AdapterVideo(Context context, ArrayList<ModelVideo> videoList) {
        this.context = context;
        this.videoList = videoList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        String videoId = modelVideo.getPostId();
        String videTitle = modelVideo.getVideoTitle();
        String videDescription = modelVideo.getVideoDescription();
        String videoCategory = modelVideo.getVideoCategory();
        String videoThumbnailUri = modelVideo.getVideoThumbnail();
        String videoUri = modelVideo.getVideoUrl();
        String videoTimestamp = modelVideo.getTimeStamp();
        String uid = modelVideo.getUid();

        //convert timestamp to dd/mm/yyyy hh:mm ap/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(videoTimestamp));
        String time = DateFormat.format("dd/MM/yyy hh:mm aa", calendar).toString();


        //set data
        holder.videoTitle.setText(videTitle);
        holder.videoCategory.setText(videoCategory);
        holder.videoDescription.setText(videDescription);
        holder.videoTime.setText(time);
        try {
            Picasso.get().load(videoThumbnailUri).into(holder.videoThumbnail);
        }catch (Exception e){
            holder.videoThumbnail.setImageResource(R.drawable.food);
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOption(holder,holder.moreBtn,uid,videoId,videoThumbnailUri,videoUri);
            }
        });
        //item clicked listener,send to exoplayer to show video
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MoviePlayerActivity.class);
                intent.putExtra("videoUrl", "" + videoUri);
                context.startActivity(intent);
            }
        });

    }



    private void showMoreOption(MyViewHolder holder, ImageView moreBtn, String uid, String videoId, String videoThumbnail, String videoUri) {

        //create popup menu
        PopupMenu popupMenu = new PopupMenu(context,moreBtn, Gravity.END);

        //add item in menu
        popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if (id == 0){
                    //delete option clicked
                    beginDelete(holder,videoId,videoThumbnail,videoUri);
                }
                return false;
            }
        });
        popupMenu.show();

    }

    private void beginDelete(MyViewHolder holder, String videoId, String videoThumbnail, String videoUri) {
        //progress dialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        //delete image using url
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(videoThumbnail);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        // image deleted, now Delete video
                       StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(videoUri);
                       storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {

                               //video deleted, now deleted data from database
                               Query query = FirebaseDatabase.getInstance().getReference("VideoPosts").orderByChild("postId").equalTo(videoId);
                               query.addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       for (DataSnapshot ds: dataSnapshot.getChildren()){
                                           //delete value from database where videoId match
                                           ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void unused) {
                                                   progressDialog.dismiss();
                                                   notifyItemRemoved(holder.getAdapterPosition());
                                                   Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                                               }
                                           }).addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {
                                                   progressDialog.dismiss();
                                                   Toast.makeText(context, "Deleted Failled", Toast.LENGTH_SHORT).show();
                                               }
                                           });
                                       }

                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {
                                       Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView videoTitle, videoDescription,videoTime,videoCategory;
        private ImageView videoThumbnail,moreBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            videoTitle = itemView.findViewById(R.id.video_title);
            moreBtn = itemView.findViewById(R.id.more_btn);
            videoDescription = itemView.findViewById(R.id.video_description);
            videoCategory = itemView.findViewById(R.id.video_category);
            videoTime = itemView.findViewById(R.id.videoTime);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);

        }
    }

}
