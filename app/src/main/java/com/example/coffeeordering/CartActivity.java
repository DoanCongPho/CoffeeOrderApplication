package com.example.coffeeordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeordering.DetailActivity.OrderItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalPriceText;
    private View checkoutButton;

    private final String filename = "orders.json";
    private final String transportedFilename = "transported_orders.json";

    private List<OrderItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.recyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        checkoutButton = findViewById(R.id.checkoutButton);

        cartItems = loadOrderItemsFromFile(filename);
        adapter = new CartAdapter(cartItems, this::updateTotalPrice);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateTotalPrice();

        // Swipe to remove
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                cartItems.remove(pos);
                saveOrderItemsToFile(cartItems, filename);
                adapter.notifyItemRemoved(pos);
                updateTotalPrice();
            }
        };
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView);

        // Checkout
        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            String now = new SimpleDateFormat("dd MMMM | hh:mm a", Locale.US).format(new Date());
            for (OrderItem item : cartItems) {
                item.dateTime = now;
            }

            // Ghi toàn bộ cartItems vào transported_orders.json
            appendOrdersToFile(cartItems, transportedFilename);

            // Clear cart
            cartItems.clear();
            saveOrderItemsToFile(cartItems, filename);

            adapter.notifyDataSetChanged();
            updateTotalPrice();

            // Chuyển sang màn hình thành công
            Intent intent = new Intent(CartActivity.this, Order_successful.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.backIcon).setOnClickListener(v -> finish());
    }

    private void updateTotalPrice() {
        float total = 0;
        for (OrderItem item : cartItems) {
            total += item.getTotal();
        }
        totalPriceText.setText(String.format("$%.2f", total));
    }

    private List<OrderItem> loadOrderItemsFromFile(String filename) {
        List<OrderItem> list = new ArrayList<>();

        try {
            FileInputStream fis = openFileInput(filename);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            String jsonString = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                list.add(OrderItem.fromJson(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private void saveOrderItemsToFile(List<OrderItem> list, String filename) {
        JSONArray array = new JSONArray();
        try {
            for (OrderItem item : list) {
                array.put(item.toJson());
            }

            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            fos.write(array.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendOrdersToFile(List<OrderItem> newOrders, String filename) {
        try {
            JSONArray existingOrders = loadOrdersFromJsonFile(filename);
            for (OrderItem item : newOrders) {
                existingOrders.put(item.toJson());
            }

            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            fos.write(existingOrders.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray loadOrdersFromJsonFile(String filename) {
        JSONArray orders = new JSONArray();
        try {
            FileInputStream fis = openFileInput(filename);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            String json = new String(buffer, "UTF-8");
            orders = new JSONArray(json);
        } catch (Exception e) {
            // File not found or corrupted, just return empty
        }
        return orders;
    }
}
