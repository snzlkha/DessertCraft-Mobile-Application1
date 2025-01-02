package com.yourapp.desertcraftadmin;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.yourapp.desertcraftadmin.adapter.OrderListAdapter;
import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityOrdersBinding;
import com.yourapp.desertcraftadmin.model.OrderData;
import com.yourapp.desertcraftadmin.response.OrderResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {
    ActivityOrdersBinding binding;
    OrderListAdapter orderListAdapter;
    private ArrayList<OrderData> dataList = new ArrayList<>();
    private boolean isNavigatingToNextActivity = false;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Drawable navigationIcon = binding.toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(ContextCompat.getColor(this, R.color.coco), PorterDuff.Mode.SRC_ATOP);
        }

        Intent getIntent = getIntent();
        int getTrackingStatus = getIntent.getIntExtra("trackingStatus", 0);

        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrders.setHasFixedSize(true);

        orderListAdapter = new OrderListAdapter(this, dataList, this::goToNextActivity);

        binding.rvOrders.setAdapter(orderListAdapter);

        if (getTrackingStatus == 123) {
            getOrderList();
        } else {
            getOrders(String.valueOf(getTrackingStatus));
        }

        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (getTrackingStatus == 123) {
                getOrderList();
            } else {
                getOrders(String.valueOf(getTrackingStatus));
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (getTrackingStatus == 123) {
                    Log.d("TAG", "activityResultLauncher: All Orders");
                    getOrderList();
                } else {
                    getOrders(String.valueOf(getTrackingStatus));
                    Log.d("TAG", "activityResultLauncher: Listed Orders" + getTrackingStatus);
                }
            }
        });

        binding.etSearchWithId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchQuery = charSequence.toString().trim();
                orderListAdapter.getFilter().filter(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.tvSearchWithDate.setOnClickListener(view -> {

            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog picker = new DatePickerDialog(OrdersActivity.this, (datePicker, year1, month1, dayOfMonth) -> {

                String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;

                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

                try {
                    Date date = inputFormat.parse(selectedDate);

                    String formattedDate;
                    if (date != null) {
                        formattedDate = outputFormat.format(date).toUpperCase();
                        binding.tvSearchWithDate.setText(formattedDate);
                        orderListAdapter.filterList(formattedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, year, month, day);
            picker.show();
            orderListAdapter.clearFilter();
        });

        setBackPressListener();
    }

    private void getOrderList() {
        ApiCall.getApiService().getOrderList().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        dataList.clear();
                        dataList.addAll(response1.getData());
                        orderListAdapter.notifyDataSetChanged();
                    } else {
                        if (response1 != null) {
                            showToast(response1.getMessage());
                        } else {
                            showToast("Error while fetching orders");
                        }
                    }
                } else {
                    showToast("Error while fetching orders");
                }
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showToast("Some error occurred!");
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void getOrders(String status) {
        ApiCall.getApiService().getOrders(status).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        dataList.clear();
                        if (response1.getData() != null) {
                            dataList.addAll(response1.getData());
                        }
                        orderListAdapter.notifyDataSetChanged();
                    } else {
                        if (response1 != null) {
                            showToast(response1.getMessage());
                        } else {
                            showToast("Error while fetching orders");
                        }
                    }
                } else {
                    showToast("Error while fetching orders");
                }
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showToast("Some error occurred!");
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(OrdersActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void setBackPressListener() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBackToResultActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goBackToResultActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBackToResultActivity() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if (!isNavigatingToNextActivity) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingToNextActivity = false;
    }

    private void goToNextActivity(OrderData item) {
        isNavigatingToNextActivity = true;
        Intent intent = new Intent(OrdersActivity.this, OrderUpdateActivity.class);
        intent.putExtra("trackingId", item.getId());
        intent.putExtra("orderStatus", item.getOrder_status());
        intent.putExtra("trackingStatus", item.getTracking_status());
        intent.putExtra("uid", item.getUid());
        activityResultLauncher.launch(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}