package com.example.lostandfound.ui.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lostandfound.LostItem;
import com.example.lostandfound.LostItemAdapter;
import com.example.lostandfound.LostItemViewHolder;
import com.example.lostandfound.databinding.FragmentHomeBinding;
import com.example.lostandfound.ui.NewFound.NewFoundActivity;
import com.example.lostandfound.ui.NewLost.NewLostActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // buttons for i have lost and i have found
        binding.lostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NewLostActivity.class);
                startActivity(i);
            }
        });

        binding.foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NewFoundActivity.class);
                startActivity(i);
            }
        });

        // temporary item list
        LostItem item1 = new LostItem(0, 333333, "Bag", 2,2,"Bags", "library");
        LostItem item2 = new LostItem(0, 33334, "Earbuds", 9, 3, "Eelectronics", "Bus stop");
        ArrayList<LostItem> lostItemList = new ArrayList<>();
        lostItemList.add(item1);
        lostItemList.add(item2);
        lostItemList.add(item2);
        lostItemList.add(item2);

        // set up the recycler view for displaying recently lost items
        binding.recentlyLostItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recentlyLostItemsRecyclerView.setAdapter(new LostItemAdapter(getActivity(), lostItemList));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}