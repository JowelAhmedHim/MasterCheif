package com.example.masterchef.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.masterchef.R;

public class AppInfo extends AppCompatActivity implements View.OnClickListener {

    private Button signUpBtn;
    private TextView signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        init();
        signUpBtn.setOnClickListener(this);
        signInBtn.setOnClickListener(this);

    }

    private void init(){

        signInBtn = findViewById(R.id.signIn_btn);
        signUpBtn = findViewById(R.id.signUp_btn);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.signIn_btn:
                intent = new Intent(AppInfo.this,SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.signUp_btn:
                intent = new Intent(AppInfo.this,SignUpActivity.class);
                startActivity(intent);
                break;
        }

    }
}