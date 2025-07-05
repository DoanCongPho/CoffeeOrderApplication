package com.example.coffeeordering;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        String navigateTo = getIntent().getStringExtra("navigateTo");
        Fragment initialFragment = new HomeFragment();

        if (navigateTo != null) {
            if (navigateTo.equals("bag")) {
                initialFragment = new CartFragment();
                bottomNavigationView.setSelectedItemId(R.id.nav_bag);
            } else if (navigateTo.equals("gift")) {
                initialFragment = new GiftFragment();
                bottomNavigationView.setSelectedItemId(R.id.nav_gift);
            } else {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, initialFragment)
                .commit();

        // Navigation logic cho BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selected = new HomeFragment();
            } else if (id == R.id.nav_gift) {
                selected = new GiftFragment();
            } else if (id == R.id.nav_bag) {
                selected = new CartFragment();
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selected)
                        .commit();
                return true;
            }
            return false;
        });
    }
}
