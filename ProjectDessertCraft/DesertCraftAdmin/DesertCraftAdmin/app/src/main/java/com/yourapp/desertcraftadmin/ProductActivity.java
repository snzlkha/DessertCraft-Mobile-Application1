package com.yourapp.desertcraftadmin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityProductBinding;
import com.yourapp.desertcraftadmin.productlist.ApiResponse;
import com.yourapp.desertcraftadmin.productlist.Price;
import com.yourapp.desertcraftadmin.productlist.Product;
import com.yourapp.desertcraftadmin.productlist.ProductAdapter;
import com.yourapp.desertcraftadmin.productlist.Variation;
import com.yourapp.desertcraftadmin.response.CallResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {
    private static final String TAG = "ProductActivity";
    private ActivityProductBinding binding;

    private List<Product> productList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Drawable navigationIcon = binding.toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(ContextCompat.getColor(this, R.color.coco), PorterDuff.Mode.SRC_ATOP);
        }

        productAdapter = new ProductAdapter(productList, new ProductAdapter.OnButtonClickListener() {
            @Override
            public void onEditClick(Product product) {
                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
                intent.putExtra("pid", product.getPid());
                activityResultLauncher.launch(intent);
                Log.d(TAG, "onEditClick: " + product.getPid());
            }

            @Override
            public void onDeleteClick(Product product) {
                String productId = product.getPid();
                ApiCall.getApiService().deleteProduct(productId).enqueue(new Callback<CallResponse>() {
                    @Override
                    public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                        if (response.isSuccessful()) {
                            CallResponse callResponse = response.body();
                            if (callResponse != null && callResponse.isSuccess()) {
                                int position = productList.indexOf(product);
                                productList.remove(position);
                                productAdapter.notifyItemRemoved(position);
                                showToast(callResponse.getMessage());
                                if (position == 0) {
                                    binding.tvBlankInfo.setVisibility(View.VISIBLE);
                                } else {
                                    binding.tvBlankInfo.setVisibility(View.GONE);
                                }
                            } else {
                                showToast("Failed to delete.");
                            }
                        } else {
                            showToast("Failed to delete");
                        }
                    }

                    @Override
                    public void onFailure(Call<CallResponse> call, Throwable t) {
                        showToast("Some error occurred.");
                    }
                });
            }
        });
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProducts.setAdapter(productAdapter);
        getProductList();

        binding.buttonAddDesert.setOnClickListener(view -> {
            Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
            activityResultLauncher.launch(intent);
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getProductList();
            }
        });

        binding.swipeRefresh.setOnRefreshListener(this::getProductList);

        binding.buttonAddCategory.setOnClickListener(view -> openCreateCategory());

        binding.etSearchWithId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchQuery = charSequence.toString().trim();
                productAdapter.getFilter().filter(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.etSearchWithCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchQuery = charSequence.toString().trim();
                if (!searchQuery.isEmpty()) {
                    productAdapter.filterById(searchQuery);
                } else {
                    productAdapter.clearFilter();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void openCreateCategory() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_add_category);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
        }
        dialog.setCancelable(true);

        EditText editText = dialog.findViewById(R.id.et_category);
        Button button = dialog.findViewById(R.id.button_add_category);

        button.setOnClickListener(view -> {
            String category = editText.getText().toString().trim();
            if (category.isEmpty()) {
                showToast("Enter Product Category!");
            } else {
                ApiCall.getApiService().createCategory(category).enqueue(new Callback<CallResponse>() {
                    @Override
                    public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                        if (response.isSuccessful()) {
                            CallResponse callResponse = response.body();
                            if (callResponse != null && callResponse.isSuccess()) {
                                showToast(callResponse.getMessage());
                                dialog.dismiss();
                            } else {
                                showToast("Failed to add.");
                            }
                        } else {
                            showToast("Failed to add.");
                        }
                    }

                    @Override
                    public void onFailure(Call<CallResponse> call, Throwable t) {
                        showToast("Some error occurred.");
                    }
                });
            }
        });

        dialog.show();
    }

    private void getProductList() {
        if (!binding.swipeRefresh.isRefreshing()) {
            binding.swipeRefresh.setRefreshing(true);
        }
        binding.tvBlankInfo.setVisibility(View.GONE);
        ApiCall.getApiService().getProductList().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        productList.clear();
                        productList.addAll(apiResponse.getData());

                        if (productList.isEmpty()) {
                            binding.tvBlankInfo.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvBlankInfo.setVisibility(View.GONE);
                        }

                        // Parse nested JSON strings
                        Gson gson = new Gson();
                        Type variationListType = new TypeToken<List<Variation>>() {
                        }.getType();
                        Type priceListType = new TypeToken<List<Price>>() {
                        }.getType();

                        for (Product product : productList) {
                            List<Variation> variations = gson.fromJson(product.getVariation(), variationListType);
                            product.setParsedVariation(variations);

                            List<Price> prices = gson.fromJson(product.getPrice(), priceListType);
                            product.setParsedPrice(prices);
                        }

                        // Notify the adapter that the data set has changed
                        productAdapter.notifyDataSetChanged();
                    } else {
                        handleApiError(response.message());
                        binding.tvBlankInfo.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Handle the error
                    handleApiError(response.message());
                    binding.tvBlankInfo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle the failure
                handleApiFailure(t);
                binding.tvBlankInfo.setVisibility(View.VISIBLE);
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void handleApiError(String message) {
        showToast(message);
    }

    private void handleApiFailure(Throwable t) {
        // Handle API failure
    }


    private void showToast(String text) {
        Toast.makeText(ProductActivity.this, text, Toast.LENGTH_SHORT).show();
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