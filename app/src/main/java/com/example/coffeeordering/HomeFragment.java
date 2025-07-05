package com.example.coffeeordering;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;




public class HomeFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        View rootView = getView();
        if (rootView != null) {
            loadUsername(rootView);
        }
    }

    private void loadUsername(View rootView) {
        Context context = getContext();
        if (context == null) return;

        TextView usernameText = rootView.findViewById(R.id.usernameText);

        try {
            FileInputStream fis = context.openFileInput("user_profile.json");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);

            String username = obj.optString("name", "Guest");
            usernameText.setText(username);

        } catch (Exception e) {
            e.printStackTrace();
            usernameText.setText("Guest");
        }
    }

    public static class Coffee {
        String name;
        int imageResId;

        public Coffee(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }

        public String getName() { return name; }
        public int getImageResId() { return imageResId; }
    }

    private void applyPressEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
                v.postDelayed(() -> v.setAlpha(1.0f), 100);
            }
            return false;
        });
    }

    private TextView stampCountText;
    private LinearLayout cupContainer;
    private final int STAMP_LIMIT = 8;

    private final String[] jsonFiles = new String[]{
            "transported_orders.json",
            "history_orders.json"
    };
    private void loadStamps() {
        Context context = getContext();
        if (context == null) return;

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
                    DetailActivity.OrderItem item = DetailActivity.OrderItem.fromJson(obj);
                    totalStamps += item.quantity;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int cappedStamps = totalStamps % STAMP_LIMIT;
        stampCountText.setText(cappedStamps + " / " + STAMP_LIMIT);
        updateCupUI(cappedStamps);
    }
    private void updateCupUI(int stampCount) {
        cupContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        int imgSize = dpToPx(getContext(), 32); // hoặc 40 nếu muốn to hơn

        for (int i = 0; i < STAMP_LIMIT; i++) {
            ImageView cup = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgSize, imgSize);
            params.setMargins(8, 0, 8, 0);
            cup.setLayoutParams(params);
            cup.setImageResource(i < stampCount ? R.drawable.ic_cup_filled : R.drawable.ic_cup_empty);
            cupContainer.addView(cup);
        }
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Cart and profile icons
        ImageView cartIcon = rootView.findViewById(R.id.cartIcon);
        ImageView profileIcon = rootView.findViewById(R.id.profileIcon);

        applyPressEffect(cartIcon);
        applyPressEffect(profileIcon);

        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CartActivity.class);
            startActivity(intent);
        });
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProfileActivity.class);
            startActivity(intent);
        });


        RecyclerView recyclerView = rootView.findViewById(R.id.coffeeRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        List<Coffee> coffeeList = new ArrayList<>();
        coffeeList.add(new Coffee("Americano", R.drawable.ic_americano));
        coffeeList.add(new Coffee("Cappuccino", R.drawable.ic_cappuccino));
        coffeeList.add(new Coffee("Mocha", R.drawable.ic_mocha));
        coffeeList.add(new Coffee("Flat White", R.drawable.ic_flatwhite));

        CoffeeAdapter adapter = new CoffeeAdapter(requireContext(), coffeeList);
        recyclerView.setAdapter(adapter);


        stampCountText = rootView.findViewById(R.id.stampCountText);
        cupContainer = rootView.findViewById(R.id.cupContainer);

        loadStamps();

        loadUsername(rootView);
        return rootView;
    }
}
