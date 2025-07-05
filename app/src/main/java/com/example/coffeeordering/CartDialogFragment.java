// File: CartDialogFragment.java
package com.example.coffeeordering;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.coffeeordering.DetailActivity.OrderItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.Locale;

public class CartDialogFragment extends DialogFragment {

    private static final String filename = "orders.json";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_cart, null);
        LinearLayout itemsContainer = view.findViewById(R.id.itemsContainer);
        TextView totalText = view.findViewById(R.id.totalText);

        float total = 0f;

        try {
            FileInputStream fis = requireActivity().openFileInput(filename);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            String jsonString = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(jsonString);

            if (jsonArray.length() == 0) {
                addEmptyText(itemsContainer);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    OrderItem item = OrderItem.fromJson(obj);
                    total += item.getTotal();

                    View itemView = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_cart_row, itemsContainer, false);

                    TextView nameText = itemView.findViewById(R.id.nameText);
                    TextView quantityText = itemView.findViewById(R.id.quantityText);
                    TextView priceText = itemView.findViewById(R.id.priceText);

                    nameText.setText(item.coffeeName);
                    quantityText.setText(String.format("x%d (%s, %s)", item.quantity,
                            item.size != null ? item.size : "Size",
                            item.Shot != null ? item.Shot : "Shot"));

                    priceText.setText(String.format(Locale.US, "$%.2f", item.getTotal()));
                    itemsContainer.addView(itemView);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            addEmptyText(itemsContainer);
        }

        totalText.setText(String.format(Locale.US, "Total: $%.2f", total));

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle("My Cart")
                .setPositiveButton("Close", (dialog, which) -> dismiss())
                .create();
    }

    private void addEmptyText(LinearLayout container) {
        TextView empty = new TextView(getContext());
        empty.setText("Your cart is empty.");
        empty.setPadding(20, 30, 20, 30);
        container.addView(empty);
    }
}
