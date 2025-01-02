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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yourapp.desertcraftadmin.adapter.PriceAdapter;
import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityAddPriceBinding;
import com.yourapp.desertcraftadmin.model.PriceCombination;
import com.yourapp.desertcraftadmin.productlist.Price;
import com.yourapp.desertcraftadmin.productlist.Variation;
import com.yourapp.desertcraftadmin.response.CallResponse;
import com.yourapp.desertcraftadmin.utils.FileUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPriceActivity extends AppCompatActivity {
    private static final String TAG = "AddPriceActivityLog";
    private ActivityAddPriceBinding binding;

    private PriceAdapter priceAdapter;

    String productName;
    String productDesc;
    String productCat;
    String productBasePrice;
    String productOfferPrice;
    String productImg;
    String productVar;
    String productId;

    String parsedPriceJson;
    List<Price> parsedPrice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPriceBinding.inflate(getLayoutInflater());
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
        productName = intent.getStringExtra("product_name");
        productDesc = intent.getStringExtra("product_desc");
        productCat = intent.getStringExtra("product_cat");
        productBasePrice = intent.getStringExtra("product_base_price");
        productOfferPrice = intent.getStringExtra("product_offer_price");
        productImg = intent.getStringExtra("product_img");
        productVar = intent.getStringExtra("product_variation");
        parsedPriceJson = intent.getStringExtra("parsedPrice");

        Gson gson = new Gson();
        Type listTypeVariation = new TypeToken<List<Variation>>() {
        }.getType();

        if (parsedPriceJson != null && !parsedPriceJson.isEmpty()) {
            binding.toolbar.setTitle("Update Product Price");

            Type listTypePrice = new TypeToken<List<Price>>() {
            }.getType();

            parsedPrice = gson.fromJson(parsedPriceJson, listTypePrice);
            Log.d(TAG, "parsedPriceGot: " + parsedPriceJson);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Variation> variations = gson.fromJson(productVar, listTypeVariation);

        List<String> flavours = new ArrayList<>();
        List<String> sizes = new ArrayList<>();
        List<String> weights = new ArrayList<>();

        for (Variation variation : variations) {
            String normalizedVariation = normalizeVariation(variation.getVariation());
            switch (normalizedVariation) {
                case "flavour":
                    flavours = Arrays.asList(variation.getOption().split(", "));
                    break;
                case "size":
                    sizes = Arrays.asList(variation.getOption().split(", "));
                    break;
                case "weight":
                    weights = Arrays.asList(variation.getOption().split(", "));
                    break;
            }
        }

        List<PriceCombination> combinationList = new ArrayList<>();

        for (String flavour : (flavours.isEmpty() ? Collections.singletonList("") : flavours)) {
            for (String size : (sizes.isEmpty() ? Collections.singletonList("") : sizes)) {
                for (String weight : (weights.isEmpty() ? Collections.singletonList("") : weights)) {
                    combinationList.add(new PriceCombination(flavour, size, weight));
                }
            }
        }

        priceAdapter = new PriceAdapter(combinationList, parsedPrice);
        recyclerView.setAdapter(priceAdapter);

        binding.btnSubmitPrice.setOnClickListener(view -> {
            List<PriceCombination> updatedCombinations = priceAdapter.getCombinationList();
            Gson gson1 = new Gson();
            String jsonResult = gson1.toJson(updatedCombinations);
            if (!jsonResult.isEmpty()) {
                if (productId == null || productId.isEmpty()) {
                    sendProductToServer(jsonResult);
                } else {
                    updateProduct(jsonResult);
                }
            } else {
                showToast("No Priced Created!");
            }
        });
    }

    private void sendProductToServer(String priceData) {
        binding.progressBar.setVisibility(View.VISIBLE);

        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), productName);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), productDesc);
        RequestBody catBody = RequestBody.create(MediaType.parse("text/plain"), productCat);
        RequestBody basePriceBody = RequestBody.create(MediaType.parse("text/plain"), productBasePrice);
        RequestBody offerPriceBody = RequestBody.create(MediaType.parse("text/plain"), productOfferPrice);
        RequestBody variationBody = RequestBody.create(MediaType.parse("text/plain"), productVar);
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), priceData);

        if (productImg != null) {
            String filePath = FileUtils.getRealPathFromURI(Uri.parse(productImg), this);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            ApiCall.getApiService().uploadProduct(nameBody, descBody, catBody, variationBody, priceBody, basePriceBody, offerPriceBody, filePart).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(@NonNull Call<CallResponse> call, @NonNull Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse callResponse = response.body();
                        if (callResponse != null && callResponse.isSuccess()) {
                            showToast(callResponse.getMessage());
                            startActivity(new Intent(AddPriceActivity.this, ProductActivity.class));
                            finish();
                        } else {
                            showToast("Error While Saving Product");
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<CallResponse> call, @NonNull Throwable t) {
                    showToast("Some error occurred.");
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            showToast("Some Data is Missing!");
        }
    }

    private void updateProduct(String priceData) {
        binding.progressBar.setVisibility(View.VISIBLE);

        RequestBody pidBody = RequestBody.create(MediaType.parse("text/plain"), productId);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), productName);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), productDesc);
        RequestBody catBody = RequestBody.create(MediaType.parse("text/plain"), productCat);
        RequestBody basePriceBody = RequestBody.create(MediaType.parse("text/plain"), productBasePrice);
        RequestBody offerPriceBody = RequestBody.create(MediaType.parse("text/plain"), productOfferPrice);
        RequestBody variationBody = RequestBody.create(MediaType.parse("text/plain"), productVar);
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), priceData);

        if (productImg != null) {
            String filePath = FileUtils.getRealPathFromURI(Uri.parse(productImg), this);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            ApiCall.getApiService().updateProductWithFile(pidBody, nameBody, descBody, catBody, variationBody, priceBody, basePriceBody, offerPriceBody, filePart).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(@NonNull Call<CallResponse> call, @NonNull Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse callResponse = response.body();
                        if (callResponse != null && callResponse.isSuccess()) {
                            showToast(callResponse.getMessage());
                            startActivity(new Intent(AddPriceActivity.this, ProductActivity.class));
                            finish();
                        } else {
                            showToast("Error While Saving Product");
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<CallResponse> call, @NonNull Throwable t) {
                    showToast("Some error occurred.");
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            ApiCall.getApiService().updateProductWithoutFile(productId, productName, productDesc, productCat, productVar, priceData, productBasePrice, productOfferPrice).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(@NonNull Call<CallResponse> call, @NonNull Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse callResponse = response.body();
                        if (callResponse != null && callResponse.isSuccess()) {
                            showToast(callResponse.getMessage());
                            startActivity(new Intent(AddPriceActivity.this, ProductActivity.class));
                            finish();
                        } else {
                            showToast("Error While Saving Product");
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<CallResponse> call, @NonNull Throwable t) {
                    showToast("Some error occurred.");
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String normalizeVariation(String variation) {
        String lowerCaseVariation = variation.toLowerCase();
        if (lowerCaseVariation.contains("flavour") || lowerCaseVariation.contains("flavor")) {
            return "flavour";
        }
        if (lowerCaseVariation.contains("size") || lowerCaseVariation.contains("sije")) {
            return "size";
        }
        if (lowerCaseVariation.contains("weight") || lowerCaseVariation.contains("weiht")) {
            return "weight";
        }
        return variation;
    }


//    private static class Variation {
//        private final String variation;
//        private final String option;
//
//        private Variation(String variation, String option) {
//            this.variation = variation;
//            this.option = option;
//        }
//
//        public String getVariation() {
//            return variation;
//        }
//
//        public String getOption() {
//            return option;
//        }
//    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}


// [{"flavour":"Choco","offerPrice":"","price":"100","size":"6 Inch","stock":"5","weight":"500 Gm"},{"flavour":"Choco","offerPrice":"190","price":"200","size":"6 Inch","stock":"10","weight":"1 Kg"},{"flavour":"Choco","price":"150","size":"8 Inch","stock":"3","weight":"500 Gm"},{"flavour":"Choco","offerPrice":"290","price":"300","size":"8 Inch","stock":"10","weight":"1 Kg"},{"flavour":"Vanilla","offerPrice":"","price":"110","size":"6 Inch","stock":"5","weight":"500 Gm"},{"flavour":"Vanilla","offerPrice":"210","price":"220","size":"6 Inch","stock":"10","weight":"1 Kg"},{"flavour":"Vanilla","price":"160","size":"8 Inch","stock":"3","weight":"500 Gm"},{"flavour":"Vanilla","offerPrice":"380","price":"400","size":"8 Inch","stock":"8","weight":"1 Kg"}]