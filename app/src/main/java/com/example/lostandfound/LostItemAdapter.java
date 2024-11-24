package com.example.lostandfound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfound.databinding.LostItemBinding;

import java.util.List;

public class LostItemAdapter extends RecyclerView.Adapter<LostItemViewHolder> {

    private Context ctx;
    private List<LostItem> itemList;

    public LostItemAdapter(Context ctx, List<LostItem> itemList){
        this.ctx = ctx;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public LostItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LostItemViewHolder(LayoutInflater.from(ctx).inflate(R.layout.lost_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LostItemViewHolder holder, int position) {
        LostItem item = itemList.get(position);

        String matchesString = String.valueOf(item.getMatches()) + " matches found";
        holder.getBinding().matches.setText(matchesString);

        holder.getBinding().name.setText(item.getName());

        String idString = "#" + String.valueOf(item.getId());
        holder.getBinding().id.setText(idString);

        String timeReportedString = String.valueOf(item.getTimeReported()) + " days ago";
        holder.getBinding().timeReported.setText(timeReportedString);

        String timeLostString = "Date: " + String.valueOf(item.getTimeLost());
        holder.getBinding().timeLost.setText(timeLostString);

        String categoryString = "Category: " + item.getCategory();
        holder.getBinding().category.setText(categoryString);

        String locationString = "Location: " + item.getLocation();
        holder.getBinding().location.setText(locationString);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
