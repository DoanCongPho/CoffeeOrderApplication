// File: RedeemActivity.java
package com.example.coffeeordering;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class RedeemActivity extends AppCompatActivity {

    private RecyclerView redeemRecycler;
    private TextView pointsText;
    private int totalPoints;
    private final int REQUIRED_POINTS = 1340;

    private final String[] orderFiles = new String[]{
            "transported_orders.json",
            "history_orders.json",
            "redeemed_orders.json" // we store redeemed (negative) here
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        redeemRecycler = findViewById(R.id.redeemRecycler);
        pointsText = findViewById(R.id.pointsText);
        redeemRecycler.setLayoutManager(new LinearLayoutManager(this));

        totalPoints = getIntent().getIntExtra("totalPoints", -1);
        pointsText.setText(totalPoints + " pts");

        List<RewardAdapter.RewardItem> redeemItems = new ArrayList<>();
        redeemItems.add(new RewardAdapter.RewardItem("Cafe Latte", "Valid until 04.07.21", REQUIRED_POINTS));
        redeemItems.add(new RewardAdapter.RewardItem("Flat White", "Valid until 04.07.21", REQUIRED_POINTS));
        redeemItems.add(new RewardAdapter.RewardItem("Cappuccino", "Valid until 04.07.21", REQUIRED_POINTS));
        redeemItems.add(new RewardAdapter.RewardItem("Mocha", "Valid until 04.07.21", REQUIRED_POINTS));



        RedeemAdapter adapter = new RedeemAdapter(redeemItems, this::attemptRedeem);
        redeemRecycler.setAdapter(adapter);
        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(v -> {
            finish();
        });

    }



    private void attemptRedeem(RewardAdapter.RewardItem item) {
        if (totalPoints < REQUIRED_POINTS) {
            new AlertDialog.Builder(this)
                    .setTitle("Not Enough Points")
                    .setMessage("You need at least " + REQUIRED_POINTS + " pts to redeem.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Decrease points
        saveRedemption(item);
        totalPoints -= REQUIRED_POINTS;
        pointsText.setText(totalPoints + " pts");
    }

    private void saveRedemption(RewardAdapter.RewardItem item) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("coffeeName", item.name);
            obj.put("quantity", 1);
            obj.put("sizePriceScale", 1);
            obj.put("shotPriceScale", 1);

            String formattedDate = new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(new Date());
            obj.put("dateTime", formattedDate);

            JSONArray arr = new JSONArray();
            try {
                FileInputStream fis = openFileInput("redeemed_orders.json");
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                arr = new JSONArray(new String(buffer));
            } catch (Exception ignored) {}

            arr.put(obj);

            FileOutputStream fos = openFileOutput("redeemed_orders.json", Context.MODE_PRIVATE);
            fos.write(arr.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
