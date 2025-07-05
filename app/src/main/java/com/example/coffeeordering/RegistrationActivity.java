package com.example.coffeeordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    private EditText fullNameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fullNameEditText = findViewById(R.id.fullName);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
    }

    public void onRegisterClick(View view) {
        String name = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Registered: " + name, Toast.LENGTH_LONG).show();

            // 1) Create the Intent to go to WelcomeActivity
            Intent intent = new Intent(RegistrationActivity.this, WelcomeActivity.class);

            // 2) (Optional) Clear the back stack so user can't press Back to return here
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            // 3) Start WelcomeActivity
            startActivity(intent);

            // 4) Finish RegistrationActivity so it’s removed from the stack
            finish();
        }
    }

}
