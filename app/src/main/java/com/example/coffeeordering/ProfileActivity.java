package com.example.coffeeordering;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameText, phoneText, emailText, addressText;
    private ImageView editNameBtn, editPhoneBtn, editEmailBtn, editAddressBtn;

    private final String fileName = "user_profile.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameText = findViewById(R.id.nameText);
        phoneText = findViewById(R.id.phoneText);
        emailText = findViewById(R.id.emailText);
        addressText = findViewById(R.id.addressText);

        editNameBtn = findViewById(R.id.editNameBtn);
        editPhoneBtn = findViewById(R.id.editPhoneBtn);
        editEmailBtn = findViewById(R.id.editEmailBtn);
        editAddressBtn = findViewById(R.id.editAddressBtn);

        loadProfile();

        editNameBtn.setOnClickListener(v -> showEditDialog("Full Name", nameText.getText().toString(), newText -> {
            nameText.setText(newText);
            saveProfile();
        }));

        editPhoneBtn.setOnClickListener(v -> showEditDialog("Phone Number", phoneText.getText().toString(), newText -> {
            phoneText.setText(newText);
            saveProfile();
        }));

        editEmailBtn.setOnClickListener(v -> showEditDialog("Email", emailText.getText().toString(), newText -> {
            emailText.setText(newText);
            saveProfile();
        }));

        editAddressBtn.setOnClickListener(v -> showEditDialog("Address", addressText.getText().toString(), newText -> {
            addressText.setText(newText);
            saveProfile();
        }));
    }

    private void showEditDialog(String title, String currentValue, OnTextConfirmed listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit " + title);

        final EditText input = new EditText(this);
        input.setText(currentValue);
        input.setSelection(currentValue.length());
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String newText = input.getText().toString().trim();
            listener.onConfirmed(newText);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void loadProfile() {
        try {
            FileInputStream fis = openFileInput(fileName);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();

            JSONObject obj = new JSONObject(new String(data));

            nameText.setText(obj.optString("name", ""));
            phoneText.setText(obj.optString("phone", ""));
            emailText.setText(obj.optString("email", ""));
            addressText.setText(obj.optString("address", ""));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProfile() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", nameText.getText().toString());
            obj.put("phone", phoneText.getText().toString());
            obj.put("email", emailText.getText().toString());
            obj.put("address", addressText.getText().toString());

            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(obj.toString().getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface OnTextConfirmed {
        void onConfirmed(String newText);
    }
}
