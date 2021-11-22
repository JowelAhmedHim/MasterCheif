package com.example.masterchef.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.masterchef.R;
import com.example.masterchef.ui.Validation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    //permission constants
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;

    //image pick constants
    public static final int IMAGE_PICK_FROM_CAMERA = 300;
    public static final int IMAGE_PICK_FROM_GALLERY = 400;

    private String[] cameraPermission;
    private String[] storagePermission;

    private ImageButton changeProfile;
    private TextInputLayout lay_username,lay_email,lay_bio;
    private TextInputEditText usernameEt,emailEt,userBioEt;
    private ImageView profile_image,imageBack;
    private Button update;


    private String userName,userEmail,userImage;

    private Validation validation;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        //Create instance of validation class
        validation = new Validation(this);

        ///firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //setUp progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);

        //initialization view
        init();
        initPermission();

        checkUser();

        //set listener
        imageBack.setOnClickListener(this);
        changeProfile.setOnClickListener(this);
        update.setOnClickListener(this);

    }

    private void init(){
        imageBack = findViewById(R.id.imageBack);
        profile_image = findViewById(R.id.profile_image);
        changeProfile = findViewById(R.id.changeProfile);
        lay_username = findViewById(R.id.layoutUsername);
        lay_email = findViewById(R.id.layoutEmail);
        usernameEt = findViewById(R.id.username_et);
        emailEt = findViewById(R.id.email_et);
        update = findViewById(R.id.userUpdate);

    }
    private void initPermission() {
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageBack:
                onBackPressed();
                break;
            case R.id.changeProfile:
                imagePickerDialog();
                break;
            case R.id.userUpdate:
                inputValidation();
                break;
        }
    }

    private void imagePickerDialog() {
        String [] options = {"Camera","gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            //camera option clicked
                            if (checkCameraPermission()){
                                //camera permission granted
                                pickFromCamera();
                            }else{
                                //camera permission denied
                                requestCameraPermission();
                            }
                        }else if (i==1){
                            if(checkStoragePermission()){
                                //storage permission granted
                                pickFromGallery();
                            }else {
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,IMAGE_PICK_FROM_CAMERA);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_FROM_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //permission granted
                        pickFromCamera();
                    }else{
                        Toast.makeText(this, "Camera & storage Permission Needed", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0 ){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permission granted
                        pickFromGallery();
                    }else {
                        //permission denied
                        Toast.makeText(this,"Storage permission necessary..",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_FROM_GALLERY){
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }else if (requestCode == IMAGE_PICK_FROM_CAMERA){
                profile_image.setImageURI(imageUri);
            }
        }
    }

    private void inputValidation() {
        userName = usernameEt.getText().toString();
        userEmail = emailEt.getText().toString();

        if (!validation.nameValidation(usernameEt,lay_username)){
            return;
        }
        if (!validation.emailValidation(emailEt,lay_email))
        {
            return;
        }
        else {
           updateInfo();
        }
    }

    //check user signIn details
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(this, SignInActivity.class));
        }else {
            loadUserInfo();
        }
    }


    //get user info from firebase
    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get user data
                             userName = ""+ds.child("name").getValue();
                             userEmail = ""+ds.child("email").getValue();
                             userImage = ""+ds.child("profileImage").getValue();
                             String accountType = ""+ds.child("accountType").getValue();

                            //set user data to view
                            usernameEt.setText(userName);
                            emailEt.setText(userEmail);
                            try {
                                Picasso.get().load(userImage).into(profile_image);

                            }catch (Exception e){
                                profile_image.setImageResource(R.drawable.ic_baseline_person_24);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateInfo() {

        progressDialog.setMessage("Updating Profile...");
        progressDialog.show();

        if (!userImage.equals("")){
            //with image
            updateWasWithImage();
        }else if (profile_image.getDrawable() != null){
            //with image
            updateWithNowImage();
        }else {
            //without image
            updateWithoutImage();
        }


    }

    private void updateWithoutImage() {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        //save info without image
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("email",""+userEmail);
        hashMap.put("name",""+userName);
//                                hashMap.put("popularity","0");
        hashMap.put("online","true");
        hashMap.put("timestamp",""+timeStamp);
        hashMap.put("accountType","user");
        hashMap.put("profileImage","");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        //user data updated successfully
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // user info not updated
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWithNowImage() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        //save info with image
        String filePathName = "profile_image/"+""+firebaseAuth.getUid();

        // get image from bitmap
        Bitmap bitmap = ((BitmapDrawable)profile_image.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //image compress
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        StorageReference reference = FirebaseStorage.getInstance().getReference().child(filePathName);
        reference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Image uploaded get its url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString();
                        if (uriTask.isSuccessful()){
                            // uri received upload user info database
                            //save info with image
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("uid",""+firebaseAuth.getUid());
                            hashMap.put("email",""+userEmail);
                            hashMap.put("name",""+userName);
//                                hashMap.put("popularity","0");
                            hashMap.put("online","true");
                            hashMap.put("timestamp",""+timeStamp);
                            hashMap.put("accountType","user");
                            hashMap.put("profileImage",""+downloadUri);

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                            databaseReference.child(firebaseAuth.getUid())
                                    .updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            //user data updated successfully
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // user info not updated
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        //image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWasWithImage() {
        // delete previous image
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(userImage);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //imageDeleted, upload new image
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        //save info with image
                        String filePathName = "profile_image/"+""+firebaseAuth.getUid();

                        // get image from bitmap
                        Bitmap bitmap = ((BitmapDrawable)profile_image.getDrawable()).getBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        //image compress
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                        byte[] data = byteArrayOutputStream.toByteArray();

                        StorageReference reference = FirebaseStorage.getInstance().getReference().child(filePathName);
                        reference.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //Image uploaded get its url
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());

                                        String downloadUri = uriTask.getResult().toString();
                                        if (uriTask.isSuccessful()){
                                            // uri received upload user info database
                                            //save info with image
                                            HashMap<String,Object> hashMap = new HashMap<>();
                                            hashMap.put("uid",""+firebaseAuth.getUid());
                                            hashMap.put("email",""+userEmail);
                                            hashMap.put("name",""+userName);
//                                hashMap.put("popularity","0");
                                            hashMap.put("online","true");
                                            hashMap.put("timestamp",""+timeStamp);
                                            hashMap.put("accountType","user");
                                            hashMap.put("profileImage",""+downloadUri);

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                            databaseReference.child(firebaseAuth.getUid())
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                            //user data updated successfully
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // user info not updated
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //image not uploaded
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //image not deleted
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}