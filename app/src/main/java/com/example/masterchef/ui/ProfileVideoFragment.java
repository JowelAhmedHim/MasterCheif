package com.example.masterchef.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.masterchef.R;


public class ProfileVideoFragment extends Fragment {


    private String title;

    public ProfileVideoFragment(String mTitle) {
        // Required empty public constructor
        this.title = mTitle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_video, container, false);
    }
}