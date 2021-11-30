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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.masterchef.R;
import com.example.masterchef.ui.Validation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //permission constants
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;

    //image pick constants
    public static final int IMAGE_PICK_FROM_CAMERA = 300;
    public static final int IMAGE_PICK_FROM_GALLERY = 400;

    private String[] cameraPermission;
    private String[] storagePermission;

    //widget
    private TextInputLayout lay_username,lay_email,lay_password,lay_confirmPassword;
    private TextInputEditText usernameEt,emailEt,passwordEt,confirmPassEt;
    private MaterialButton signUpBtn,signInBtn;
    private ImageView profileImage,changeProfile;

    //strings
    private String username,email,password;

    private Validation validation;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        validation = new Validation(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //setUp progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);


        initPermission();
        init();

        signUpBtn.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        changeProfile.setOnClickListener(this);



    }

    private void initPermission() {
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void init(){

        lay_username = findViewById(R.id.layoutUsername);
        lay_email = findViewById(R.id.layoutEmail);
        lay_password = findViewById(R.id.layoutPassword);
        lay_confirmPassword = findViewById(R.id.layoutConfirmPassword);
        usernameEt = findViewById(R.id.username_et);
        emailEt = findViewById(R.id.email_et);
        passwordEt = findViewById(R.id.password_et);
        confirmPassEt = findViewById(R.id.confirmPassword_et);
        profileImage = findViewById(R.id.profile_image);
        signUpBtn = findViewById(R.id.signUp_btn);
        changeProfile = findViewById(R.id.changeProfile);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signUp_btn:
                inputValidation();
                break;
            case R.id.profile_image:
            case R.id.changeProfile:
                imagePickerDialog();
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
                profileImage.setImageURI(imageUri);
            }else if (requestCode == IMAGE_PICK_FROM_CAMERA){
                profileImage.setImageURI(imageUri);
            }
        }
    }

    private void inputValidation() {
        username = usernameEt.getText().toString();
        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();

        if (!validation.nameValidation(usernameEt,lay_username)){
            return;
        }
        if (!validation.emailValidation(emailEt,lay_email))
        {
            return;
        }

        if (!validation.passwordValidation(passwordEt,lay_password)){
            return;
        }

        if (!validation.passwordConfirmationValidation(passwordEt,confirmPassEt,lay_confirmPassword)){
            return;
        }
        else {
           createAccount();
        }
    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        //creating account using email and password
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(SignUpActivity.this, "Successfully SignUp", Toast.LENGTH_SHORT).show();
                        saveProfileIntoDatabase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

    }

    private void saveProfileIntoDatabase() {

        progressDialog.setMessage("Saving profile...");
        progressDialog.show();

        String timeStamp = ""+System.currentTimeMillis();

        if (imageUri == null){

            //save info without image
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("name",""+username);
            hashMap.put("online","true");
            hashMap.put("popularity","0");
            hashMap.put("timestamp",""+timeStamp);
            hashMap.put("accountType","user");
            hashMap.put("profileImage","");

            //save data into database

            DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }else{

            //save info with image
            String filePathName = "profile_image/"+""+firebaseAuth.getUid();

            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get url from upload image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                //save info with image
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+email);
                                hashMap.put("name",""+username);
                                hashMap.put("popularity","0");
                                hashMap.put("online","true");
                                hashMap.put("timestamp",""+timeStamp);
                                hashMap.put("accountType","user");
                                hashMap.put("profileImage",""+downloadUri);

                                //save data into database
                                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
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
                            progressDialog.dismiss();
                         }
                    });

        }
    }



}