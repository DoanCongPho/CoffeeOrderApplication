package com.example.coffeeordering;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeordering.DetailActivity.OrderItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GiftFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        View rootView = getView();
        if (rootView != null) {
            loadAndDisplayRewards();
        }
    }



    private TextView totalPointsText, stampCountText;
    private LinearLayout cupContainer;
    private RecyclerView historyRecycler;
    private final int STAMP_LIMIT = 8;
    private final int BASE_POINTS = 12;

    private final String[] jsonFiles = new String[]{
            "transported_orders.json",
            "history_orders.json",
            "redeemed_orders.json"
    };
    private int totalPoints = 0; //

    private final List<RewardAdapter.RewardItem> rewardItems = new ArrayList<>();

    public GiftFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gift, container, false);

        totalPointsText = root.findViewById(R.id.totalPointsText);
        stampCountText = root.findViewById(R.id.stampCountText);
        cupContainer = root.findViewById(R.id.cupContainer);
        historyRecycler = root.findViewById(R.id.historyRecycler);

        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadAndDisplayRewards();

        Button redeemButton = root.findViewById(R.id.redeemButton);
        redeemButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RedeemActivity.class);
            intent.putExtra("totalPoints", totalPoints);
            startActivity(intent);
        });
        return root;
    }

    private void loadAndDisplayRewards() {
        Context context = getContext();
        if (context == null) return;

        totalPoints = 0;
        int totalStamps = 0;

        for (String file : jsonFiles) {
            try {
                FileInputStream fis = context.openFileInput(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();

                String json = new String(buffer, "UTF-8");
                JSONArray array = new JSONArray(json);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    OrderItem item = OrderItem.fromJson(obj);

                    int quantity = item.quantity;
                    float sizeMultiplier = item.sizePriceScale;
                    float shotMultiplier = item.shotPriceScale;

                    int itemPoints = Math.round(BASE_POINTS * sizeMultiplier * shotMultiplier * quantity);

                    if (file.equals("redeemed_orders.json")) itemPoints -= 1354;
                    else totalStamps += quantity;

                    totalPoints += itemPoints;

                    rewardItems.add(new RewardAdapter.RewardItem(
                            item.coffeeName,
                            item.dateTime == null ? "Unknown time" : item.dateTime,
                            itemPoints
                    ));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int cappedStamps = totalStamps % STAMP_LIMIT;
        updateCupUI(cappedStamps);

        stampCountText.setText(cappedStamps + " / " + STAMP_LIMIT);
        totalPointsText.setText(String.valueOf(totalPoints));
        historyRecycler.setAdapter(new RewardAdapter(rewardItems));
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private void updateCupUI(int stampCount) {
        cupContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        int imgSize = dpToPx(getContext(), 35);
        for (int i = 0; i < STAMP_LIMIT; i++) {
            ImageView cup = new ImageView(getContext());
            cup.setLayoutParams(new LinearLayout.LayoutParams(imgSize, imgSize));
            cup.setPadding(8, 0, 8, 0);
            cup.setImageResource(i < stampCount ? R.drawable.ic_cup_filled : R.drawable.ic_cup_empty);
            cupContainer.addView(cup);
        }
    }
}
