package com.example.coffeeordering;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffeeordering.DetailActivity.OrderItem;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final List<OrderItem> orders;

    public OrdersAdapter(List<OrderItem> orders) {
        this.orders = orders;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, time;

        public OrderViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemName);
            price = view.findViewById(R.id.itemPrice);
            time = view.findViewById(R.id.itemTime);
        }
    }

    @Override
    public OrdersAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_view, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        OrderItem item = orders.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(String.format("$%.2f", item.getTotal()));
        if (item.dateTime != null && !item.dateTime.isEmpty()) {
            holder.time.setText(item.dateTime);
        } else {
            holder.time.setText("Unknown time");
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
