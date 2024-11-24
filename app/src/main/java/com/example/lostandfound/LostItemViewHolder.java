package com.example.lostandfound;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfound.databinding.FragmentHomeBinding;
import com.example.lostandfound.databinding.LostItemBinding;

public class LostItemViewHolder extends RecyclerView.ViewHolder {

    private LostItemBinding binding;

    public LostItemViewHolder(@NonNull View itemView) {
        super(itemView);

        binding = LostItemBinding.bind(itemView);
    }

    public LostItemBinding getBinding(){
        return this.binding;
    }
}
