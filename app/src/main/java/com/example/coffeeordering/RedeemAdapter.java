// File: RedeemAdapter.java
package com.example.coffeeordering;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RedeemAdapter extends RecyclerView.Adapter<RedeemAdapter.ViewHolder> {

    public interface OnRedeemClickListener {
        void onRedeemClick(RewardAdapter.RewardItem item);
    }

    private final List<RewardAdapter.RewardItem> items;
    private final OnRedeemClickListener listener;

    public RedeemAdapter(List<RewardAdapter.RewardItem> items, OnRedeemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_redeem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RewardAdapter.RewardItem item = items.get(position);
        holder.nameText.setText(item.name);
        holder.dateText.setText(item.dateTime);
        holder.pointsButton.setText(item.points + " pts");
        holder.pointsButton.setOnClickListener(v -> listener.onRedeemClick(item));

        // Optionally: use name to pick image
        if (item.name.toLowerCase().contains("latte")) {
            holder.image.setImageResource(R.drawable.ic_latte);
        } else if (item.name.toLowerCase().contains("flat")) {
            holder.image.setImageResource(R.drawable.ic_flatwhite);
        } else if (item.name.toLowerCase().contains("cappuccino")) {
            holder.image.setImageResource(R.drawable.ic_cappuccino);
        } else if (item.name.toLowerCase().contains("mocha")) {
            holder.image.setImageResource(R.drawable.ic_mocha);
        } else {
            holder.image.setImageResource(R.drawable.ic_coffee);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nameText, dateText;
        Button pointsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.coffeeImage);
            nameText = itemView.findViewById(R.id.coffeeName);
            dateText = itemView.findViewById(R.id.validDate);
            pointsButton = itemView.findViewById(R.id.redeemButton);
        }
    }
}