package com.example.coffeeordering;

import static java.lang.Math.abs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    public static class RewardItem {
        public String name;
        public String dateTime;
        public int points;

        public RewardItem(String name, String dateTime, int points) {
            this.name = name;
            this.dateTime = dateTime;
            this.points = points;
        }
    }

    private final List<RewardItem> rewards;

    public RewardAdapter(List<RewardItem> rewards) {
        this.rewards = rewards;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, points;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemName);
            time = view.findViewById(R.id.itemTime);
            points = view.findViewById(R.id.itemPoints);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RewardItem item = rewards.get(position);
        holder.name.setText(item.name);
        holder.time.setText(item.dateTime);
        String prefix = item.points < 0 ? "-" : "+";
        holder.points.setText(prefix + abs(item.points) + " Pts");
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }
}
