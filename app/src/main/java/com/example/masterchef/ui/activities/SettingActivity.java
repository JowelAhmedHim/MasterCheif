package com.example.masterchef.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.masterchef.R;

public class SettingActivity extends AppCompatActivity {
Switch aSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){

            setTheme(R.style.DarkTheme);
        }else{

            setTheme(R.style.Theme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        aSwitch=findViewById(R.id.mode);
       if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){

           aSwitch.setChecked(true);
       }
       aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){

                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//reset();
               }else{

AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                  // reset();
               }
           }
       });
    }

    /*private void reset() {
Intent intent=new Intent(getApplicationContext(),MainActivity.class);
startActivity(intent);
finish();



    }*/
}