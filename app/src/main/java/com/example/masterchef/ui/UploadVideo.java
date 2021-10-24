package com.example.masterchef.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import java.util.ArrayList;
import java.util.List;

public class UploadVideo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int VIDEO_REQUEST_CODE = 100;
    Toolbar toolbar;
    private EditText videoNameEt,videoDescriptionEt;
    private Spinner spinner ;
    private TextView imageTv,videoTv;
    private Button upload,videoBtn;

    Uri videoUri;
    String videoCategory;
    String videoTitle;
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
        spinnerFunction();
        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideoFile();
            }
        });




    }

    public void init(){
        videoNameEt = findViewById(R.id.video_title);
        videoDescriptionEt = findViewById(R.id.video_description);
        spinner = findViewById(R.id.spinner);
        videoTv = findViewById(R.id.video_tv);
        imageTv = findViewById(R.id.image_tv);
        upload = findViewById(R.id.upload);
        videoBtn = findViewById(R.id.vide_btn);


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

    public void openVideoFile(){
        
        startActivityForResult(Intent.createChooser(
                new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("video/*"), "Choose Video"),VIDEO_REQUEST_CODE);

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

            }
        }
    }




}