package com.yourapp.desertcraftadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yourapp.desertcraftadmin.adapter.RatingAdapter;
import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityPerformanceBinding;
import com.yourapp.desertcraftadmin.model.InsightData;
import com.yourapp.desertcraftadmin.model.RateData;
import com.yourapp.desertcraftadmin.response.InsightResponse;
import com.yourapp.desertcraftadmin.response.RateResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformanceActivity extends AppCompatActivity {
    private ActivityPerformanceBinding binding;
    String startDate;
    String endDate;

    private List<RateData> dataList = new ArrayList<>();
    private RatingAdapter ratingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerformanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Drawable navigationIcon = binding.toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(ContextCompat.getColor(this, R.color.coco), PorterDuff.Mode.SRC_ATOP);
        }

        binding.cardRatings.setOnClickListener(view -> {
            binding.layoutInsight.setVisibility(View.GONE);
            binding.layoutRating.setVisibility(View.VISIBLE);
            getRatings();
        });

        binding.cardInsight.setOnClickListener(view -> {
            binding.layoutRating.setVisibility(View.GONE);
            binding.layoutInsight.setVisibility(View.VISIBLE);
        });

        binding.tvStartDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog picker = new DatePickerDialog(PerformanceActivity.this, (datePicker, year1, month1, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;

                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                try {
                    Date date = inputFormat.parse(selectedDate);

                    String formattedDate;
                    if (date != null) {
                        formattedDate = outputFormat.format(date).toUpperCase();
                        startDate = outputFormat1.format(date);
                        binding.tvStartDate.setText(formattedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, year, month, day);
            picker.show();
        });

        binding.tvEndDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog picker = new DatePickerDialog(PerformanceActivity.this, (datePicker, year1, month1, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;

                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                try {
                    Date date = inputFormat.parse(selectedDate);

                    String formattedDate;
                    if (date != null) {
                        formattedDate = outputFormat.format(date).toUpperCase();
                        endDate = outputFormat1.format(date);
                        binding.tvEndDate.setText(formattedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, year, month, day);
            picker.show();
        });


        binding.btnSearch.setOnClickListener(view -> {
            if (startDate == null || startDate.isEmpty()) {
                showToast("Select Start Date");
            } else if (endDate == null || endDate.isEmpty()) {
                showToast("Select End Date");
            } else {
                getInsight();
            }
        });

        binding.rvRatings.setLayoutManager(new LinearLayoutManager(this));
        ratingAdapter = new RatingAdapter(dataList);
        binding.rvRatings.setAdapter(ratingAdapter);
    }

    private void getInsight() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiCall.getApiService().getInsights(startDate, endDate).enqueue(new Callback<InsightResponse>() {
            @Override
            public void onResponse(Call<InsightResponse> call, Response<InsightResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InsightResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        InsightData data = apiResponse.getCounts();
                        binding.tvTotalUsers.setText(data.getTotal_users());
                        binding.tvTotalOrders.setText(data.getTotal_orders());
                        binding.tvTotalSales.setText(data.getTotal_sales());

                        if (data.getTotal_users() != null && !data.getTotal_users().isEmpty() && !data.getTotal_users().equals("0")) {
                            binding.imgAngleUsers.setImageResource(R.drawable.up_green);
                        } else {
                            binding.imgAngleUsers.setImageResource(R.drawable.down_red);
                        }

                        if (data.getTotal_orders() != null && !data.getTotal_orders().isEmpty() && !data.getTotal_orders().equals("0")) {
                            binding.imgAngleOrders.setImageResource(R.drawable.up_green);
                        } else {
                            binding.imgAngleOrders.setImageResource(R.drawable.down_red);
                        }

                        if (data.getTotal_sales() != null && !data.getTotal_sales().isEmpty() && !data.getTotal_sales().equals("0")) {
                            binding.imgAngleSales.setImageResource(R.drawable.up_green);
                        } else {
                            binding.imgAngleSales.setImageResource(R.drawable.down_red);
                        }
                    } else {
                        showToast("No Data Available");
                    }
                } else {
                    showToast("Some Error Occurred!");
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<InsightResponse> call, Throwable t) {
                showToast("Some Error Occurred!");
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getRatings() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiCall.getApiService().getRatings().enqueue(new Callback<RateResponse>() {
            @Override
            public void onResponse(Call<RateResponse> call, Response<RateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RateResponse response1 = response.body();
                    if (response1.isSuccess()) {
                        dataList.clear();
                        dataList.addAll(response1.getData());
                        ratingAdapter.notifyDataSetChanged();
                    } else {
                        showToast("No Rating Available.");
                    }
                } else {
                    showToast("Some error occurred");
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RateResponse> call, Throwable t) {
                showToast("Some error occurred");
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(PerformanceActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}