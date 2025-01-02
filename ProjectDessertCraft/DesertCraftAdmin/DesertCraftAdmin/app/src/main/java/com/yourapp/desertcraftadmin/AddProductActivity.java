package com.yourapp.desertcraftadmin;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yourapp.desertcraftadmin.adapter.CategoryAdapter;
import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityAddProductBinding;
import com.yourapp.desertcraftadmin.model.CatData;
import com.yourapp.desertcraftadmin.product.ProductSingle;
import com.yourapp.desertcraftadmin.product.ApiSingleResponse;
import com.yourapp.desertcraftadmin.response.CatResponse;
import com.yourapp.desertcraftadmin.utils.Val;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    private static final String TAG = "AddProductActivityLog";
    private ActivityAddProductBinding binding;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    this::handleSelectedFile);
    Uri fileUri;
    private final List<CatData> catList = new ArrayList<>();
    private CategoryAdapter adapter;
    String catId;
    String catName;

    String productId;

    ProductSingle product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
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
        productId = intent.getStringExtra("pid");

        getCategory();

        if (productId != null && !productId.isEmpty()) {
            binding.toolbar.setTitle("Update Product");
        }

        adapter = new CategoryAdapter(getApplicationContext(), catList);
        binding.etProductCategory.setAdapter(adapter);
        binding.etProductCategory.setThreshold(-1);

        binding.etProductCategory.setOnItemClickListener((adapterView, view, i, l) -> {
            CatData catData = adapter.getItem(i);
            if (catData != null) {
                catId = catData.getCid();
                catName = catData.getC_name();
                binding.etProductCategory.setText(catName);
            }
        });

        binding.imgDesertSelected.setOnClickListener(view -> openFilePicker());

        binding.layoutVariation.setOnClickListener(view -> {
            validateInput();
        });

        binding.btnAddVariation.setOnClickListener(view -> {
            validateInput();
        });
    }

    private void validateInput() {
        String name = binding.etProductName.getText().toString().trim();
        String desc = binding.etProductDescription.getText().toString().trim();
        String category = binding.etProductCategory.getText().toString().trim();
        String price = binding.etBasePrice.getText().toString().trim();
        String offerPrice = binding.etOfferPrice.getText().toString().trim();

        if (name.isEmpty()) {
            showToast("Enter Product Name!");
        } else if (desc.isEmpty()) {
            showToast("Enter Product Description!");
        } else if (category.isEmpty()) {
            showToast("Select Product Category!");
        } else if (price.isEmpty()) {
            showToast("Enter Base Price!");
        } else if ((productId == null || productId.isEmpty()) && fileUri == null) {
            showToast("Select Product Image!");
        } else {
            Intent intent = new Intent(AddProductActivity.this, AddVariationActivity.class);
            intent.putExtra("product_name", name);
            intent.putExtra("product_desc", desc);
            intent.putExtra("product_cat", catId);
            intent.putExtra("product_base_price", price);
            intent.putExtra("product_offer_price", offerPrice);
            if (productId == null || productId.isEmpty()) {
                intent.putExtra("product_img", fileUri.toString());
            } else {
                Gson gson = new Gson();
                String parsedVariationJson = gson.toJson(product.getParsedVariation());
                String parsedPriceJson = gson.toJson(product.getParsedPrice());

                Log.d(TAG, "parsedVariationValidate: " + parsedVariationJson);

                intent.putExtra("parsedVariation", parsedVariationJson);
                intent.putExtra("parsedPrice", parsedPriceJson);
                intent.putExtra("pid", productId);

                if (fileUri != null) {
                    intent.putExtra("product_img", fileUri.toString());
                }
            }
            startActivity(intent);
        }
    }

    private void openFilePicker() {
        filePickerLauncher.launch("image/*");
    }

    private void handleSelectedFile(Uri uri) {
        if (uri != null) {
            String fileType = getContentResolver().getType(uri);
            if (fileType != null) {
                if (fileType.startsWith("image/")) {
                    Glide.with(this)
                            .load(uri)
                            .into(binding.imgDesertSelected);
                } else {
                    Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
                }
                fileUri = uri;
                binding.tvAddReplaceImg.setText("- Replace Photo");
                binding.tvAddReplaceImg.setTextColor(ContextCompat.getColor(this, R.color.hollow_red));
            }
        }
    }

    private void getCategory() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiCall.getApiService().getCategory().enqueue(new Callback<CatResponse>() {
            @Override
            public void onResponse(Call<CatResponse> call, Response<CatResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    CatResponse catResponse = response.body();
                    if (catResponse != null && catResponse.isSuccess()) {
                        catList.addAll(catResponse.getData());
                        adapter.notifyDataSetChanged();

                        if (productId != null && !productId.isEmpty()) {
                            getProduct();
                        }
                    } else {
                        showToast("Category Error! Returned.");
                        finish();
                    }
                } else {
                    showToast("Category Error! Returned.");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CatResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showToast("Category Error! Returned.");
                finish();
            }
        });
    }

    private void getProduct() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiCall.getApiService().getProduct(productId).enqueue(new Callback<ApiSingleResponse>() {
            @Override
            public void onResponse(Call<ApiSingleResponse> call, Response<ApiSingleResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ApiSingleResponse newResponse = response.body();
                    if (newResponse != null && newResponse.isSuccess()) {
                        product = newResponse.getData();
                        initView(product);
                    } else {
                        showToast("Couldn't get data! Returned.");
                    }
                } else {
                    showToast("Couldn't get data! Returned.");
                }
            }

            @Override
            public void onFailure(Call<ApiSingleResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showToast("Couldn't get data! Returned.");
            }
        });
    }

    private void initView(ProductSingle data) {
        catId = data.getCid();
        catName = getCatNameFromId(catId);
        binding.etProductName.setText(data.getP_name());
        binding.etProductDescription.setText(data.getDescription());
        binding.etProductCategory.setText(catName);
        binding.etBasePrice.setText(data.getBase_price());
        binding.etOfferPrice.setText(data.getOffer_price());

        if (!data.getP_image().isEmpty()) {
            Picasso.get().load(Val.IMG_URL + data.getP_image()).into(binding.imgDesertSelected);
            binding.tvAddReplaceImg.setText("- Replace Photo");
            binding.tvAddReplaceImg.setTextColor(ContextCompat.getColor(this, R.color.hollow_red));
        }

        binding.btnAddVariation.setOnClickListener(view -> validateInput());
        binding.layoutVariation.setOnClickListener(view -> validateInput());
    }

    public String getCatNameFromId(String catId) {
        for (CatData catData : catList) {
            if (catData.getCid().equals(catId)) {
                catName = catData.getC_name();
                break;
            }
        }
        return catName;
    }

    private void showToast(String text) {
        Toast.makeText(AddProductActivity.this, text, Toast.LENGTH_SHORT).show();
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