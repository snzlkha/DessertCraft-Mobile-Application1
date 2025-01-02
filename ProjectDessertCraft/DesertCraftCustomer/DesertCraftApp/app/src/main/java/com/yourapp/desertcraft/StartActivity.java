package com.yourapp.desertcraft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.yourapp.desertcraft.utils.PrefManager;
import com.yourapp.desertcraft.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStartBinding binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PrefManager prefManager = new PrefManager(this);

        if (prefManager.isLoggedIn()) {
            Intent intent = new Intent(StartActivity.this, ContainerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {
            binding.buttonLogin.setOnClickListener(view -> {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
            binding.buttonRegister.setOnClickListener(view -> {
                Intent intent = new Intent(StartActivity.this, SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }
}