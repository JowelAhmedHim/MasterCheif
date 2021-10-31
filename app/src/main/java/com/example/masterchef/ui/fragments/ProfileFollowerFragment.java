package com.example.masterchef.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.masterchef.R;


public class ProfileFollowerFragment extends Fragment {

   String title;

    public ProfileFollowerFragment(String mTitle) {
        // Required empty public constructor
        title = mTitle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_followe, container, false);
    }
}