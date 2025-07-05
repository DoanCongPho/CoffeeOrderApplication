package com.example.coffeeordering;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyOrdersPagerAdapter extends FragmentStateAdapter {

    public MyOrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? OrdersListFragment.newInstance("transported_orders.json")
                : OrdersListFragment.newInstance("history_orders.json");
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
