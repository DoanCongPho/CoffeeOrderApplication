package com.example.coffeeordering;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<DetailActivity.OrderItem> items;
    private final Runnable onCartChanged;

    public CartAdapter(List<DetailActivity.OrderItem> items, Runnable onCartChanged) {
        this.items = items;
        this.onCartChanged = onCartChanged;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        DetailActivity.OrderItem item = items.get(position);

        holder.name.setText(item.coffeeName);
        holder.price.setText(String.format("$%.2f", item.getTotal()));
        holder.details.setText(item.Shot + " | " + item.Type + " | " + item.size + " | " + item.Ice + "\nx" + item.quantity);

        // Set image based on coffee name
        switch (item.coffeeName.toLowerCase()) {
            case "americano":
                holder.imageView.setImageResource(R.drawable.ic_americano); break;
            case "cappuccino":
                holder.imageView.setImageResource(R.drawable.ic_cappuccino); break;
            case "flat white":
                holder.imageView.setImageResource(R.drawable.ic_flatwhite); break;
            case "mocha":
                holder.imageView.setImageResource(R.drawable.ic_mocha); break;
            default:
                holder.imageView.setImageResource(R.drawable.ic_cup); break;
        }

        // Handle delete icon tap
        holder.deleteIcon.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                items.remove(pos);
                notifyItemRemoved(pos);
                onCartChanged.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, details;
        ImageView imageView, deleteIcon;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.coffeeName);
            price = itemView.findViewById(R.id.coffeePrice);
            details = itemView.findViewById(R.id.coffeeDetails);
            imageView = itemView.findViewById(R.id.coffeeImage);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
