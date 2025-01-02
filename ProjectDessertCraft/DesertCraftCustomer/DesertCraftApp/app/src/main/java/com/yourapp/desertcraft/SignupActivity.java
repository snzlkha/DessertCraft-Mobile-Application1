package com.yourapp.desertcraft;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.ActivitySignupBinding;
import com.yourapp.desertcraft.response.CallResponse;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private boolean isNavigatingToNextActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.etDob.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog picker = new DatePickerDialog(SignupActivity.this, (view1, year1, month1, dayOfMonth) -> binding.etDob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            picker.show();
        });

        binding.buttonRegister.setOnClickListener(view -> {
            String name = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String dob = binding.etDob.getText().toString().trim();
            int selectedRadio = binding.radioGender.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedRadio);
            String gender = "";
            if (selectedRadioButton != null) {
                gender = selectedRadioButton.getText().toString();
            }
            String mobile = binding.etMobile.getText().toString().trim();
            String mobileRegex = "^[1-9][0-9]{9}$";
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            Matcher mobileMatcher = mobilePattern.matcher(mobile);
            String password = binding.etPassword.getText().toString().trim();
            String cPassword = binding.etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                showToast("Please enter your full name");
                binding.etFullName.requestFocus();
            } else if (TextUtils.isEmpty(email)) {
                showToast("Please enter your email");
                binding.etEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Please enter your valid email");
                binding.etEmail.requestFocus();
            } else if (TextUtils.isEmpty(dob)) {
                showToast("Date of birth is required!");
                binding.etDob.requestFocus();
            } else if (selectedRadio == -1) {
                showToast("Please select your gender");
            } else if (TextUtils.isEmpty(mobile)) {
                showToast("Mobile number is required!");
                binding.etMobile.requestFocus();
            } else if (mobile.length() != 10) {
                showToast("Please 10 digit mobile number!");
                binding.etMobile.requestFocus();
            } else if (!mobileMatcher.find()) {
                showToast("Please enter correct mobile number!");
                binding.etMobile.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                showToast("Please enter your password");
                binding.etPassword.requestFocus();
            } else if (password.length() < 6) {
                showToast("Password must be at least 6 character long!");
                binding.etPassword.requestFocus();
            } else if (TextUtils.isEmpty(cPassword)) {
                showToast("Please confirm your password");
                binding.etConfirmPassword.requestFocus();
            } else if (!cPassword.equals(password)) {
                showToast("Password does not match!");
                binding.etConfirmPassword.requestFocus();
            } else {
                registerUser(name, email, dob, gender, mobile, password);
            }
        });

        setBackPressListener();
    }

    private void registerUser(String name, String email, String dob, String gender, String mobile, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiCall.getApiService().registerUser(name, email, dob, gender, mobile, password).enqueue(new Callback<CallResponse>() {
            @Override
            public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                if (response.isSuccessful()) {
                    CallResponse callResponse = response.body();
                    if (callResponse != null && callResponse.isSuccess()) {
                        showToast(callResponse.getMessage());
                        goToNextActivity();
                    } else {
                        if (callResponse != null) {
                            showToast(callResponse.getMessage());
                        } else {
                            showToast("Failed to register! Try again.");
                        }
                    }
                } else {
                    showToast("Failed to register! Try again.");
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override

            public void onFailure(Call<CallResponse> call, Throwable t) {
                showToast("Some error occurred! Try again.");
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String text) {
        Toast.makeText(SignupActivity.this, text, Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}