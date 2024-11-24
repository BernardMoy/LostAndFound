package com.example.lostandfound.ui.Messages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lostandfound.R;
import com.example.lostandfound.databinding.FragmentMatchingItemsBinding;
import com.example.lostandfound.databinding.FragmentMessagesBinding;
import com.example.lostandfound.ui.MatchingItems.MatchingItemsViewModel;


public class Messages extends Fragment {

    private FragmentMessagesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MessagesViewModel messagesViewModel= new ViewModelProvider(this).get(MessagesViewModel.class);

        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}