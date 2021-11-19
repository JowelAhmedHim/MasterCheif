package com.example.masterchef.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.masterchef.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressBar progressBar;
    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.Pbar);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                goMethod();
            }
        });
        thread.start();


    }

    private void doWork() {
        for (progress = 20; progress <= 100; progress = progress + 20) {

            try {
                Thread.sleep(1000);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void goMethod() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null){
            //user not logged in , start login activity
            startActivity(new Intent(SplashScreen.this, AppInfo.class));
        }else{
            //user is logged in,check user
           startActivity(new Intent(SplashScreen.this,MainActivity.class));
        }
        finish();

    }
}