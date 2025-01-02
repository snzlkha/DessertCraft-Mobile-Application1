package com.yourapp.desertcraftadmin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityMainBinding;
import com.yourapp.desertcraftadmin.model.CountData;
import com.yourapp.desertcraftadmin.response.CountResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    int trackingStatus;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnMyProducts.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ProductActivity.class)));

        getStatusCount();

        binding.swipeRefresh.setOnRefreshListener(this::getStatusCount);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getStatusCount();
            }
        });

        binding.tvApproval.setOnClickListener(view -> {
            trackingStatus = 0;
            openOrderActivity(trackingStatus);
        });

        binding.tvProcess.setOnClickListener(view -> {
            trackingStatus = 1;
            openOrderActivity(trackingStatus);
        });

        binding.tvUnpaid.setOnClickListener(view -> {
            trackingStatus = 2;
            openOrderActivity(trackingStatus);
        });

        binding.tvKitchen.setOnClickListener(view -> {
            trackingStatus = 3;
            openOrderActivity(trackingStatus);
        });

        binding.tvPickup.setOnClickListener(view -> {
            trackingStatus = 4;
            openOrderActivity(trackingStatus);
        });

        binding.tvCompleted.setOnClickListener(view -> {
            trackingStatus = 5;
            openOrderActivity(trackingStatus);
        });

        binding.btnAllOrders.setOnClickListener(view -> {
            trackingStatus = 123;
            openOrderActivity(trackingStatus);
        });

        binding.btnPerformance.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PerformanceActivity.class)));

        binding.btnAddShop.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, UploadBannerActivity.class));
        });
    }

    private void openOrderActivity(int status) {
        Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
        intent.putExtra("trackingStatus", status);
        activityResultLauncher.launch(intent);
    }

    private void getStatusCount() {
        Log.d("TAG", "getStatusCount: Refreshing");
        if (!binding.swipeRefresh.isRefreshing()) {
            binding.swipeRefresh.setRefreshing(true);
        }
        ApiCall.getApiService().getStatusCount().enqueue(new Callback<CountResponse>() {
            @Override
            public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                if (response.isSuccessful()) {
                    CountResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        CountData data = response1.getCounts();
                        binding.approval.setText(data.getTotal_pending_approval());
                        binding.unpaid.setText(data.getTotal_need_pay());
                        binding.process.setText(data.getTotal_accepted());
                        binding.kitchen.setText(data.getTotal_in_kitchen());
                        binding.pickup.setText(data.getTotal_ready_pickup());
                        binding.completed.setText(data.getTotal_completed());
                        binding.cancelled.setText(data.getTotal_cancelled());
                        binding.review.setText(data.getTotal_review());
                    } else {
                        showToast("Unable to fetch same data");
                    }
                } else {
                    showToast("Unable to fetch same data");
                }
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<CountResponse> call, Throwable t) {
                showToast("Failed to fetch same data");
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void showToast(String string) {
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
    }
}