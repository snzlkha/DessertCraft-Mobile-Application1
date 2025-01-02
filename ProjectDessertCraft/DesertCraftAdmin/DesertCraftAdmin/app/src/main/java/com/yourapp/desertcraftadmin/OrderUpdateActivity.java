package com.yourapp.desertcraftadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityOrderUpdateBinding;
import com.yourapp.desertcraftadmin.model.OrderData;
import com.yourapp.desertcraftadmin.response.CallResponse;
import com.yourapp.desertcraftadmin.response.TrackResponse;
import com.yourapp.desertcraftadmin.utils.Constant;
import com.yourapp.desertcraftadmin.utils.PDFDownloader;
import com.yourapp.desertcraftadmin.utils.Val;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderUpdateActivity extends AppCompatActivity {

    private ActivityOrderUpdateBinding binding;

    String trackingId, uid;
    int getOrderStatus, newOrderStatus;
    int getTrackingStatus, newTrackingStatus;
    float addOnPrice = 0;
    float totalPrice = 0;
    int newPoint = 0;
    private PDFDownloader pdfDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Drawable navigationIcon = binding.toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(ContextCompat.getColor(this, R.color.coco), PorterDuff.Mode.SRC_ATOP);
        }

        Intent intent = getIntent();
        trackingId = intent.getStringExtra("trackingId");
        uid = intent.getStringExtra("uid");
        getOrderStatus = intent.getIntExtra("orderStatus", 0);
        getTrackingStatus = intent.getIntExtra("trackingStatus", 0);

        if (getTrackingStatus == 0) {
            newTrackingStatus = 1;
            newOrderStatus = 1;
            binding.btnReject.setVisibility(View.VISIBLE);
            binding.btnApprove.setText("Accept Order");

            binding.etAddonPrice.setVisibility(View.VISIBLE);
            binding.tvAddonPrice.setVisibility(View.GONE);
        } else if (getTrackingStatus == 1) {
            binding.btnApprove.setVisibility(View.GONE);
        } else if (getTrackingStatus == 2) {
            newTrackingStatus = 3;
            newOrderStatus = 3;
        } else if (getTrackingStatus == 3) {
            newTrackingStatus = 4;
            newOrderStatus = 4;
        } else if (getTrackingStatus == 4) {
            newTrackingStatus = 5;
            newOrderStatus = 5;
            binding.btnApprove.setText("Complete Order");
        } else if (getTrackingStatus == 5) {
            newTrackingStatus = 5;
            newOrderStatus = 5;
            binding.btnApprove.setVisibility(View.GONE);
            binding.tvOrderCompleted.setVisibility(View.VISIBLE);
        }

        if (getOrderStatus == 6) {
            binding.tvOrderCompleted.setText("Order has been rejected.");
            binding.tvOrderCompleted.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            binding.tvOrderCompleted.setVisibility(View.VISIBLE);
            binding.btnApprove.setVisibility(View.GONE);
        }

        trackOrder();

        binding.swipeRefresh.setOnRefreshListener(this::trackOrder);

        binding.btnReject.setOnClickListener(view -> {
            newOrderStatus = 6;
            newTrackingStatus = 5;
            forwardOrder("", String.valueOf(newOrderStatus), String.valueOf(newTrackingStatus), trackingId);
        });

        binding.btnApprove.setOnClickListener(view -> {
            String addOn = binding.etAddonPrice.getText().toString().trim();
            forwardOrder(addOn, String.valueOf(newOrderStatus), String.valueOf(newTrackingStatus), trackingId);
        });

        pdfDownloader = new PDFDownloader();
    }

    private void forwardOrder(String addOn, String orderStatus, String trackingStatus, String trackId) {
        ApiCall.getApiService().forwardOrder(addOn, orderStatus, trackingStatus, trackId, uid, newPoint).enqueue(new Callback<CallResponse>() {
            @Override
            public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                if (response.isSuccessful()) {
                    CallResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        showToast(response1.getMessage());
                        goBackToResultActivity();
                    } else {
                        if (response1 != null) {
                            showToast(response1.getMessage());
                        } else {
                            showToast("Couldn't update status!");
                        }
                    }
                } else {
                    showToast("Couldn't update status!");
                }
            }

            @Override
            public void onFailure(Call<CallResponse> call, Throwable t) {
                showToast("Some error occurred!");
            }
        });
    }


    private void trackOrder() {
        if (trackingId != null) {
            ApiCall.getApiService().trackOrder(trackingId).enqueue(new Callback<TrackResponse>() {
                @Override
                public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                    if (response.isSuccessful()) {
                        TrackResponse response1 = response.body();
                        if (response1 != null && response1.isSuccess()) {
                            OrderData data = response1.getData();
                            updateUI(data);
                        } else {
                            if (response1 != null) {
                                showToast(response1.getMessage());
                            } else {
                                showToast("Couldn't get tracking data.");
                            }
                        }
                    } else {
                        showToast("Couldn't get tracking data.");
                    }
                    if (binding.swipeRefresh.isRefreshing()) {
                        binding.swipeRefresh.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<TrackResponse> call, Throwable t) {
                    showToast("Some error occurred.");
                    if (binding.swipeRefresh.isRefreshing()) {
                        binding.swipeRefresh.setRefreshing(false);
                    }
                }
            });
        } else {
            showToast("Tracking ID missing.");
            if (binding.swipeRefresh.isRefreshing()) {
                binding.swipeRefresh.setRefreshing(false);
            }
        }
    }

    private void updateUI(OrderData data) {
        if (data.getOrder_status() == 0) {
            binding.tvOrderStatus.setText(Val.status0);
            binding.imgOrderStatus.setImageResource(R.drawable.clock);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(this, R.color.coco), PorterDuff.Mode.SRC_IN);
        } else if (data.getOrder_status() == 1) {
            binding.tvOrderStatus.setText(Val.status2);
            binding.imgOrderStatus.setImageResource(R.drawable.saving);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_IN);
        } else if (data.getOrder_status() == 2) {
            binding.tvOrderStatus.setText(Val.status1);
            binding.imgOrderStatus.setImageResource(R.drawable.checked);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(this, R.color.coco), PorterDuff.Mode.SRC_IN);
            if (data.getReceipt() == null || data.getReceipt().isEmpty()) {
                binding.btnApprove.setVisibility(View.GONE);
            }
        } else if (data.getOrder_status() == 3) {
            binding.tvOrderStatus.setText(Val.status3);
            binding.imgOrderStatus.setImageResource(R.drawable.chef);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(this, R.color.yellow), PorterDuff.Mode.SRC_IN);
        } else if (data.getOrder_status() == 4) {
            binding.tvOrderStatus.setText(Val.status4);
            binding.imgOrderStatus.setImageResource(R.drawable.pickup);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(this, R.color.coco), PorterDuff.Mode.SRC_IN);
        } else if (data.getOrder_status() == 5) {
            binding.tvOrderStatus.setText(Val.status5);
            binding.imgOrderStatus.setImageResource(R.drawable.checked);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_IN);
        } else if (data.getOrder_status() == 6) {
            binding.tvOrderStatus.setText(Val.status6);
            binding.tvOrderStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            binding.imgOrderStatus.setImageResource(R.drawable.info_alt);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light), PorterDuff.Mode.SRC_IN);
        }

        binding.tvCustomerId.setText("Customer ID: " + data.getUid());
        binding.tvOrderId.setText("Order ID: " + data.getId());
        binding.tvOrderDate.setText("Order Date: " + data.getCreated_on());
        if (data.getOn_date().isEmpty()) {
            binding.tvPickupDate.setText("Pickup Date: Today");
        } else {
            binding.tvPickupDate.setText("Pickup Date: " + data.getOn_date());
        }

        if (!data.getImage().isEmpty()) {
            Picasso.get().load(Val.IMG_URL + data.getImage()).into(binding.imgDesert);
        }
        binding.tvDesertName.setText(data.getDesert_name());
        binding.tvPrice.setText("Price: RM" + data.getPrice());
        binding.tvCustomizations.setText(Constant.cleanUpString(data.getCustomization()));
        binding.tvBasicPrice.setText("RM" + data.getPrice());
        binding.tvOrderId.setText("Order ID: " + data.getId());
        binding.tvOrderQty.setText("Qty: " + data.getQuantity());

        if (getTrackingStatus != 0 && data.getAdd_on_price() != null && !data.getAdd_on_price().isEmpty()) {
            addOnPrice = Float.parseFloat(data.getAdd_on_price());
            binding.tvAddonPrice.setVisibility(View.VISIBLE);
        }

        binding.tvAddonPrice.setText("RM" + data.getAdd_on_price());

        binding.etAddonPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String addPrice = binding.etAddonPrice.getText().toString().trim();
                if (!addPrice.isEmpty()) {
                    addOnPrice = Float.parseFloat(addPrice);
                } else {
                    addOnPrice = 0;
                }
                totalPrice = Float.parseFloat(data.getPrice()) + addOnPrice;
                binding.tvTotalPrice.setText(String.valueOf(totalPrice));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        totalPrice = Float.parseFloat(data.getPrice());
        if (data.getAdd_on_price() != null && !data.getAdd_on_price().isEmpty()) {
            totalPrice = Float.parseFloat(data.getPrice()) + Float.parseFloat(data.getAdd_on_price());
        }
        binding.tvTotalPrice.setText("RM" + totalPrice);

        if (data.getInstruction() != null && !data.getInstruction().isEmpty()) {
            binding.layoutDesign.setVisibility(View.VISIBLE);
            binding.tvInstruction.setText(data.getInstruction());
        }

        binding.tvDesign.setOnClickListener(view -> {
            if (data.getDesign() != null && !data.getDesign().isEmpty()) {
                Picasso.get().load(Val.IMG_URL + data.getDesign()).into(binding.imgFullScreen);
                binding.cardFullScreen.setVisibility(View.VISIBLE);
            } else {
                showToast("No Design Available!");
            }
        });

        newPoint = (int) totalPrice;

        binding.imgClose.setOnClickListener(view -> binding.cardFullScreen.setVisibility(View.GONE));

        if (data.getReceipt() != null && !data.getReceipt().isEmpty()) {
            binding.btnViewReceipt.setVisibility(View.VISIBLE);
        }
        binding.btnViewReceipt.setOnClickListener(view -> {
            downloadReceipt(data.getReceipt());
        });
    }

    private void downloadReceipt(String receipt) {
        pdfDownloader.downloadAndOpenPDF(this, Val.IMG_URL + receipt);
    }


    private void showToast(String text) {
        Toast.makeText(OrderUpdateActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}