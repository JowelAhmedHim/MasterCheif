package com.example.masterchef.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.masterchef.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.nio.file.Path;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class UploadVideo extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    //permission code
    public static final int CAMERA_REQUEST_CODE = 200;
    public static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    public static final int IMAGE_PICK_GALLERY_CODE = 400;
    public static final int IMAGE_PICK_CAMERA_CODE = 500;

    public static final int VIDEO_REQUEST_CODE = 100;

    private String[] cameraPermission;
    private String[] storagePermission;
    Toolbar toolbar;
    private EditText videoNameEt,videoDescriptionEt;
    private Spinner spinner ;
    private TextView imageTv,videoTv;
    private Button upload,videoBtn,thumbnailBtn;



    String videoTitle;
    String videoDescription;
    String videoCategory;

    Uri videoUri,thumbnailUri;

    String currentUid;
    StorageReference mStorageReference;
    StorageTask mUploadTask;
    DatabaseReference referenceVideos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Video Upload");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        init();
        initPermission();
        spinnerFunction();
        videoBtn.setOnClickListener(this);
        thumbnailBtn.setOnClickListener(this);

        videoTitle = videoNameEt.getText().toString();
        videoDescription = videoDescriptionEt.getText().toString();
        upload.setOnClickListener(this);

        dataValidation();


    }

    public void init(){
        videoNameEt = findViewById(R.id.video_title);
        videoDescriptionEt = findViewById(R.id.video_description);
        spinner = findViewById(R.id.spinner);
        videoTv = findViewById(R.id.video_tv);
        imageTv = findViewById(R.id.image_tv);
        upload = findViewById(R.id.upload);
        videoBtn = findViewById(R.id.vide_btn);
        thumbnailBtn = findViewById(R.id.image_btn);



    }

    private void initPermission() {
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    //spinner function generate
    private void spinnerFunction() {
        List<String> categoryList  = new ArrayList<>();
        categoryList.add("Pizzas");
        categoryList.add("Pastas");
        categoryList.add("Noodles");
        categoryList.add("Burgers");
        categoryList.add("Desserts");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        videoCategory = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        videoCategory = adapterView.getItemAtPosition(0).toString();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.vide_btn:
                openVideoFile();
                break;
            case R.id.image_btn:
                imagePicker();
                break;
            case R.id.upload:
                dataValidation();
                break;
        }

    }

    public void openVideoFile(){
        startActivityForResult(Intent.createChooser(
                new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("video/*"), "Choose Video"),VIDEO_REQUEST_CODE);

    }


    // function for image picker dialog
    private void imagePicker() {

        //Option to show in dialog
        String [] options = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            //camera clicked
                            if (checkCameraPermission()){
                                //camera permission allowed
                                pickFromCamera();
                            }else{
                                requestCameraPermission();
                            }
                        }else if (i==1){
                            if (checkStoragePermission()){
                                //storage permission granted
                                pickFromGallery();
                            }else {
                                // storage permission not granted
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();

    }

    //request storage permission
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);

    }

    //check storage permission
    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return result;
    }

    //request Camera Permission
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);

    }

    //check Camera Permission
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }


    //picking image function from camera
    private void pickFromCamera() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image description");

        thumbnailUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,thumbnailUri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);

    }

    //picking image function from gallery
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }


    private void dataValidation() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0 ){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //permission granted
                        pickFromCamera();
                    }else {
                        //permission denied
                        Toast.makeText(this,"Camera permission needed",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "Storage Permission needed", Toast.LENGTH_SHORT).show();
                    }
                }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case VIDEO_REQUEST_CODE:
                    if (data.getData() != null)
                    {

                        //get video uri , realPath, fileName
                        videoUri = data.getData();
                        String realPath = videoUri.getPath();
                        Toast.makeText(getApplicationContext(), ""+realPath, Toast.LENGTH_SHORT).show();

                        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                        ContentResolver cr = getApplicationContext().getContentResolver();
                        Cursor metaCursor = cr.query(videoUri, projection, null, null, null);
                        if (metaCursor != null) {
                            try {
                                if (metaCursor.moveToFirst()) {
                                   String fileName = metaCursor.getString(0);
                                    Toast.makeText(getApplicationContext(), ""+fileName, Toast.LENGTH_SHORT).show();
                                    videoTv.setText(fileName);
                                }
                            } finally {
                                metaCursor.close();
                            }
                        }

                    }
                    break;
                case IMAGE_PICK_CAMERA_CODE:
                    getImageFileName(thumbnailUri);
                    break;
                case IMAGE_PICK_GALLERY_CODE:
                    thumbnailUri = data.getData();
                    getImageFileName(thumbnailUri);
                    break;

            }
        }
    }

    private void getImageFileName(Uri thumbnailUri) {
        String imageName = "";
        Cursor cursor = getContentResolver().query(thumbnailUri,null,null,null,null);
        try {
            int name = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToNext();
            imageName = cursor.getString(name);
            imageTv.setText(imageName);
        }catch (Exception e){
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
        }finally {
            cursor.close();
        }
    }


}