package com.example.coffeeordering;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffeeordering.DetailActivity.OrderItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class OrdersListFragment extends Fragment {

    private static final String ARG_FILENAME = "filename";
    private String filename;
    private RecyclerView recyclerView;

    public static OrdersListFragment newInstance(String filename) {
        OrdersListFragment fragment = new OrdersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILENAME, filename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filename = getArguments().getString(ARG_FILENAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<OrderItem> orders = loadOrdersFromFile(filename);
        recyclerView.setAdapter(new OrdersAdapter(orders));

        return view;
    }

    private List<OrderItem> loadOrdersFromFile(String filename) {
        List<OrderItem> list = new ArrayList<>();
        try {
            FileInputStream fis = requireContext().openFileInput(filename);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            String json = new String(buffer, "UTF-8");
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                list.add(OrderItem.fromJson(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
