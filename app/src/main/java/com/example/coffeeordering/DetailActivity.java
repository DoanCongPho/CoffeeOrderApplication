package com.example.coffeeordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;






public class DetailActivity extends AppCompatActivity {

    public static class OrderItem {
        String coffeeName;
        int quantity = 1;
        float pricePerCup = 3.0f;
        String size = "M";
        String Shot = "Single";
        String Type = "Hot";
        String Ice = "Normal";
        float sizePriceScale = 1.2f;
        float shotPriceScale = 1.0f;

        String dateTime = "";

        public float getTotal() {
            return quantity * pricePerCup * sizePriceScale * shotPriceScale;
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("coffeeName", coffeeName);
                json.put("quantity", quantity);
                json.put("pricePerCup", pricePerCup);
                json.put("size", size);
                json.put("Shot", Shot);
                json.put("Type", Type);
                json.put("Ice", Ice);
                json.put("sizePriceScale", sizePriceScale);
                json.put("shotPriceScale", shotPriceScale);
                json.put("dateTime", dateTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
        public static OrderItem fromJson(JSONObject json) {
            OrderItem item = new OrderItem();
            try {
                item.coffeeName = json.getString("coffeeName");
                item.quantity = json.getInt("quantity");
                item.pricePerCup = (float) json.getDouble("pricePerCup");
                item.size = json.getString("size");
                item.Shot = json.getString("Shot");
                item.Type = json.getString("Type");
                item.Ice = json.getString("Ice");
                item.sizePriceScale = (float) json.getDouble("sizePriceScale");
                item.shotPriceScale = (float) json.getDouble("shotPriceScale");
                item.dateTime = json.getString("dateTime");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return item;
        }

        public String getName() {
            return coffeeName;
        }
    }
    private final String transportedFilename = "transported_orders.json";


    private final String filename = "orders.json";

    // Save current item to JSON file
    private void saveOrderToJsonFile(OrderItem item) {
        try {
            JSONArray orders = loadOrdersFromJsonFile();  // Load existing orders
            orders.put(item.toJson());                    // Add current item

            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            fos.write(orders.toString().getBytes());      // Save updated array
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load existing orders from file
    private JSONArray loadOrdersFromJsonFile() {
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
            // File not found — first run
        }
        return orders;
    }


    public static List<OrderItem> orderList = new ArrayList<>();
    private OrderItem currentItem = new OrderItem();






    private TextView quantityText, totalPriceText;
    private ToggleButton singleShot, doubleShot;
    private ImageButton hotType, icedType;
    private ImageButton sizeS, sizeM, sizeL;
    private ImageButton ice1, ice2, ice3;

    private void update_size(String size) {
        currentItem.size = size;
        switch (size) {
            case "S": currentItem.sizePriceScale = 1.0f; break;
            case "M": currentItem.sizePriceScale = 1.2f; break;
            case "L": currentItem.sizePriceScale = 1.5f; break;
        }
        update_price();
    }

    private void update_quantity(int change) {
        currentItem.quantity = Math.max(1, currentItem.quantity + change);
        quantityText.setText(String.valueOf(currentItem.quantity));
        update_price();
    }

    private void update_type(String type) {
        currentItem.Type = type;
    }

    private void update_shot(String shot) {
        currentItem.Shot = shot;
        currentItem.shotPriceScale = shot.equals("Double") ? 1.5f : 1.0f;
        update_price();
    }

    private void update_ice(String level) {
        currentItem.Ice = level;
    }

    private void update_price() {
        totalPriceText.setText(String.format("$%.2f", currentItem.getTotal()));
    }

    private void setExclusiveSelection(ImageButton[] group, ImageButton selected) {
        for (ImageButton b : group) b.setAlpha(b == selected ? 1f : 0.3f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        String coffeeName = getIntent().getStringExtra("coffeeName");
        int imageResId = getIntent().getIntExtra("imageResId", -1);

        ImageView coffeeImageView = findViewById(R.id.coffeeImage);
        TextView nameTextView = findViewById(R.id.coffeeName);
        quantityText = findViewById(R.id.quantityText);
        totalPriceText = findViewById(R.id.totalPrice);

        nameTextView.setText(coffeeName);
        if (imageResId != -1) coffeeImageView.setImageResource(imageResId);
        currentItem.coffeeName = coffeeName;

        // Quantity
        findViewById(R.id.increaseQty).setOnClickListener(v -> update_quantity(1));
        findViewById(R.id.decreaseQty).setOnClickListener(v -> update_quantity(-1));

        // Shot
        singleShot = findViewById(R.id.singleShot);
        doubleShot = findViewById(R.id.doubleShot);

        singleShot.setOnClickListener(v -> {
            singleShot.setChecked(true);
            doubleShot.setChecked(false);
            singleShot.setAlpha(1f);
            doubleShot.setAlpha(0.3f);
            update_shot("Single");
        });

        doubleShot.setOnClickListener(v -> {
            singleShot.setChecked(false);
            doubleShot.setChecked(true);
            singleShot.setAlpha(0.3f);
            doubleShot.setAlpha(1f);
            update_shot("Double");
        });

        // Type
        hotType = findViewById(R.id.hotType);
        icedType = findViewById(R.id.icedType);
        ImageButton[] typeButtons = {hotType, icedType};

        hotType.setOnClickListener(v -> {
            update_type("Hot");
            setExclusiveSelection(typeButtons, hotType);
        });

        icedType.setOnClickListener(v -> {
            update_type("Iced");
            setExclusiveSelection(typeButtons, icedType);
        });

        // Size
        sizeS = findViewById(R.id.sizeS);
        sizeM = findViewById(R.id.sizeM);
        sizeL = findViewById(R.id.sizeL);

        ImageButton[] sizeButtons = {sizeS, sizeM, sizeL};

        sizeS.setOnClickListener(v -> {
            update_size("S");
            setExclusiveSelection(sizeButtons, sizeS);
        });

        sizeM.setOnClickListener(v -> {
            update_size("M");
            setExclusiveSelection(sizeButtons, sizeM);
        });

        sizeL.setOnClickListener(v -> {
            update_size("L");
            setExclusiveSelection(sizeButtons, sizeL);
        });

        // Ice
        ice1 = findViewById(R.id.ice1);
        ice2 = findViewById(R.id.ice2);
        ice3 = findViewById(R.id.ice3);
        ImageButton[] iceButtons = {ice1, ice2, ice3};
        ice1.setOnClickListener(v -> {
            update_ice("Less ice");
            setExclusiveSelection(iceButtons, ice1);
        });
        ice2.setOnClickListener(v -> {
            update_ice("Normal ice");
            setExclusiveSelection(iceButtons, ice2);
        });
        ice3.setOnClickListener(v -> {
            update_ice("Extra ice");
            setExclusiveSelection(iceButtons, ice3);
        });
        // Add to Cart
        Button addToCart = findViewById(R.id.addToCartButton);
        addToCart.setOnClickListener(v -> {
            saveOrderToJsonFile(currentItem);  // Save to JSON file
            orderList.add(currentItem);        // Still add to memory (optional)
            Intent intent = new Intent(DetailActivity.this, CartActivity.class);
            startActivity(intent);
        });


        // Set defaults
        setExclusiveSelection(typeButtons, hotType);
        setExclusiveSelection(sizeButtons, sizeM);
        setExclusiveSelection(iceButtons, ice2);
        singleShot.setAlpha(1f);
        doubleShot.setAlpha(0.3f);
        update_price();

        View back = findViewById(R.id.backIcon);

        back.setOnClickListener(v -> {
            finish();
        });


        View cart = findViewById(R.id.myCart);
        cart.setOnClickListener(v -> {
            new CartDialogFragment().show(getSupportFragmentManager(), "CartDialog");
        });


        ImageButton unityButton = findViewById(R.id.unityApp);
        unityButton.setOnClickListener(v -> {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.congpho.coffeeapp");
            if (launchIntent != null) {
                startActivity(launchIntent); // Mở app Unity
            } else {
                Toast.makeText(DetailActivity.this, "App doesn't install. Contact with our shop for supporting!", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
