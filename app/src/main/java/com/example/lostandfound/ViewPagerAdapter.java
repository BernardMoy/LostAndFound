package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lostandfound.ui.MatchingItems.MatchingItems;
import com.example.lostandfound.ui.Messages.Messages;

/**
 * For handling fragment switching in the tab view in Navigation Activity
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new Messages();
        }
        return new MatchingItems();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
