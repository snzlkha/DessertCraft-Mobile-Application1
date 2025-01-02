package com.yourapp.desertcraft;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.ActivityLoginBinding;
import com.yourapp.desertcraft.model.UserData;
import com.yourapp.desertcraft.response.LoginResponse;
import com.yourapp.desertcraft.utils.PrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private PrefManager prefManager;
    private boolean isNavigatingToNextActivity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prefManager = new PrefManager(this);

        binding.buttonLogin.setOnClickListener(view -> checkLoginCredential());

        setBackPressListener();
    }

    private void checkLoginCredential() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            showToast("Enter your registered email!");
        } else if (password.isEmpty()) {
            showToast("Enter your password!");
        } else {
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiCall.getApiService().getLoginData(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.isSuccess()) {
                        showToast(loginResponse.getMessage());

                        UserData data = loginResponse.getData();
                        prefManager.setUserData(data.getUid(), data.getName(), data.getEmail(), data.getDob(), data.getGender(), data.getMobile(), data.getCreated_on(), data.getUpdated_on());
                        prefManager.setLoggedIn(true);

                        goToNextActivity();
                    } else {
                        if (loginResponse != null) {
                            showToast(loginResponse.getMessage());
                        } else {
                            showToast("Some error occurred!");
                        }
                    }
                } else {
                    showToast("Some error occurred!");
                    Log.d("TAG", "LogonResponse1: " + response.body());
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showToast("Some error occurred!");
                binding.progressBar.setVisibility(View.GONE);
                Log.d("TAG", "LogonResponse2: " + t.getLocalizedMessage());
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setBackPressListener() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        if (!isNavigatingToNextActivity) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void goToNextActivity() {
        isNavigatingToNextActivity = true;
        startActivity(new Intent(LoginActivity.this, ContainerActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finishAffinity();
    }
}