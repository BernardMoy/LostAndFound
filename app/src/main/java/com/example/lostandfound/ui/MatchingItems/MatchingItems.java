package com.example.lostandfound.ui.MatchingItems;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lostandfound.databinding.FragmentMatchingItemsBinding;

public class MatchingItems extends Fragment {

    private FragmentMatchingItemsBinding binding;

    public MatchingItems() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MatchingItemsViewModel matchingItemsViewModel= new ViewModelProvider(this).get(MatchingItemsViewModel.class);

        binding = FragmentMatchingItemsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}