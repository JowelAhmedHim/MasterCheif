package com.example.masterchef.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.masterchef.R;

import java.util.ArrayList;
import java.util.List;

public class UploadVideo extends AppCompatActivity {

    Toolbar toolbar;
    private EditText videoNameEt;
    private Spinner spinner ;
    private TextView imageTv,videoTv,videoCategoryEt;
    private Button upload;

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




    }

    public void init(){
        videoNameEt = findViewById(R.id.video_title);
        videoCategoryEt = findViewById(R.id.video_category);
        videoTv = findViewById(R.id.video_tv);
        imageTv = findViewById(R.id.image_tv);
        upload = findViewById(R.id.upload);
        spinner = findViewById(R.id.spinner);

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



}