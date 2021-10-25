package com.example.masterchef.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.masterchef.ui.ProfileFollowerFragment;
import com.example.masterchef.ui.ProfileVideoFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private String [] data = {"Videos","Followers"};
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ProfileVideoFragment(data[position]);
            case 1:
                return new ProfileFollowerFragment(data[position]);

        }
        return new ProfileVideoFragment(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
}
