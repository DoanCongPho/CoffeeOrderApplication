package com.example.coffeeordering;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;




public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.ViewHolder> {
    List<HomeFragment.Coffee> coffeeList;
    private final Context context;
    public CoffeeAdapter(Context context, List<HomeFragment.Coffee> coffeeList) {
        this.context = context;
        this.coffeeList = coffeeList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coffeeImage;
        TextView coffeeName;

        public ViewHolder(View view) {
            super(view);
            coffeeImage = view.findViewById(R.id.coffeeImage);
            coffeeName = view.findViewById(R.id.coffeeName);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coffee_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeFragment.Coffee coffee = coffeeList.get(position);
        holder.coffeeImage.setImageResource(coffee.getImageResId());
        holder.coffeeName.setText(coffee.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("coffeeName", coffee.getName());
            intent.putExtra("imageResId", coffee.getImageResId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return coffeeList.size();
    }
}
