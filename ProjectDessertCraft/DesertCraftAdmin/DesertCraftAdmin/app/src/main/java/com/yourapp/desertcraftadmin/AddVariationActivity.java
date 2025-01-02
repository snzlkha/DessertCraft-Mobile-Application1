package com.yourapp.desertcraftadmin;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityAddVariationBinding;
import com.yourapp.desertcraftadmin.productlist.Variation;
import com.yourapp.desertcraftadmin.response.CallResponse;
import com.yourapp.desertcraftadmin.utils.Constant;
import com.yourapp.desertcraftadmin.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVariationActivity extends AppCompatActivity {
    private ActivityAddVariationBinding binding;
    private static final String TAG = "AddVariationActivityLog";
    private LinearLayout container;
    private CardView btnAddVariation;
    private int variationCount = 0;
    private int optionCount = 0;

    String productName;
    String productDesc;
    String productCat;
    String productBasePrice;
    String productOfferPrice;
    String productImg;
    String parsedVariationJson;
    String parsedPriceJson;
    String productId;

    List<Variation> parsedVariation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVariationBinding.inflate(getLayoutInflater());
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
        parsedVariationJson = intent.getStringExtra("parsedVariation");
        parsedPriceJson = intent.getStringExtra("parsedPrice");

        container = findViewById(R.id.container);
        btnAddVariation = findViewById(R.id.btn_add_et_variation);

        if (parsedVariationJson != null && !parsedVariationJson.isEmpty()) {
            binding.toolbar.setTitle("Update Product Variation");
            Gson gson = new Gson();
            Type variationListType = new TypeToken<List<Variation>>() {
            }.getType();
            parsedVariation = gson.fromJson(parsedVariationJson, variationListType);

            Log.d(TAG, "parsedVariationGot: " + parsedVariationJson);

            // Populate the parsed variation data
            populateVariations();
        }

        btnAddVariation.setOnClickListener(v -> addVariationCard(null, null));

        binding.btnSubmitVariation.setOnClickListener(view -> {
            try {
                submitAllVariations();
            } catch (JSONException e) {
                e.printStackTrace();
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

    private void populateVariations() {
        for (Variation variation : parsedVariation) {
            List<String> options = Arrays.asList(variation.getOption().split(", "));
            addVariationCard(variation.getVariation(), options);
        }
    }

    private void addVariationCard(String variationName, List<String> options) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View variationCard = inflater.inflate(R.layout.card_variation, container, false);

        EditText editTextVariation = variationCard.findViewById(R.id.edittext_variation);
        ImageView imgDeleteVariation = variationCard.findViewById(R.id.img_delete_variation);
        ImageView imgEditVariation = variationCard.findViewById(R.id.img_edit_variation);
        LinearLayout optionContainer = variationCard.findViewById(R.id.option_container); // Container for options
        CardView btnAddOption = createAddOptionButton(optionContainer); // Create the "Add Option" button

        // Add the "Add Option" button as a child of the variation card
        optionContainer.addView(btnAddOption);

        // Reset optionCount to 0 for each new variation
        optionCount = 0;

        variationCount++;
        editTextVariation.setHint("Variation " + variationCount);
        if (variationName != null) {
            editTextVariation.setText(variationName);
        }

        imgEditVariation.setOnClickListener(view -> Constant.showSoftInputKeyboardWithFocus(AddVariationActivity.this, editTextVariation));

        imgDeleteVariation.setOnClickListener(v -> {
            // Count the number of options in this variation
            int optionsInThisVariation = 0;
            for (int i = 0; i < optionContainer.getChildCount(); i++) {
                View child = optionContainer.getChildAt(i);
                if (child.findViewById(R.id.edittext_option) != null) {
                    optionsInThisVariation++;
                }
            }
            optionCount -= optionsInThisVariation;

            container.removeView(variationCard);
            variationCount--;
        });

        // Add the variation card above the "Add Variation" button
        container.addView(variationCard, container.indexOfChild(btnAddVariation));

        // If options are provided, add them to the option container
        if (options != null) {
            for (String option : options) {
                addOptionCard(optionContainer, option);
            }
        }
    }


    private CardView createAddOptionButton(final LinearLayout optionContainer) {
        LayoutInflater inflater = LayoutInflater.from(this);
        CardView btnAddOption = (CardView) inflater.inflate(R.layout.btn_add_et_option, container, false);

        btnAddOption.setOnClickListener(v -> addOptionCard(optionContainer, null));

        return btnAddOption;
    }

    private void addOptionCard(LinearLayout optionContainer, String optionName) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View optionCard = inflater.inflate(R.layout.card_option, container, false);

        EditText editTextOption = optionCard.findViewById(R.id.edittext_option);
        ImageView imgDeleteOption = optionCard.findViewById(R.id.img_delete_option);
        ImageView imgEditOption = optionCard.findViewById(R.id.img_edit_option);

        optionCount++;
        editTextOption.setHint("Option " + optionCount);
        if (optionName != null) {
            editTextOption.setText(optionName);
        }

        imgEditOption.setOnClickListener(view -> Constant.showSoftInputKeyboardWithFocus(AddVariationActivity.this, editTextOption));

        imgDeleteOption.setOnClickListener(v -> {
            optionContainer.removeView(optionCard);
            optionCount--;
            // Update the option numbering
            updateOptionNumbering(optionContainer);
        });

        // Add the option card to the option container within the variation card
        optionContainer.addView(optionCard, optionContainer.indexOfChild(optionContainer.getChildAt(optionContainer.getChildCount() - 1)));
    }

    private void updateOptionNumbering(LinearLayout optionContainer) {
        // Iterate through the option container to update option numbering
        for (int i = 0; i < optionContainer.getChildCount(); i++) {
            View optionView = optionContainer.getChildAt(i);
            EditText editTextOption = optionView.findViewById(R.id.edittext_option);
            if (editTextOption != null) {
                editTextOption.setHint("Option " + (i)); // Update the hint to reflect the new numbering
            }
        }
    }


    private void submitAllVariations() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        boolean hasEmptyField = false;

        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof CardView) {
                EditText editTextVariation = view.findViewById(R.id.edittext_variation);
                if (editTextVariation != null) {
                    String variationText = editTextVariation.getText().toString().trim();
                    if (variationText.isEmpty()) {
                        hasEmptyField = true;
                        break;
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("variation", variationText);

                    LinearLayout optionContainer = view.findViewById(R.id.option_container);
                    List<String> options = new ArrayList<>();

                    for (int j = 0; j < optionContainer.getChildCount(); j++) {
                        View optionView = optionContainer.getChildAt(j);
                        EditText editTextOption = optionView.findViewById(R.id.edittext_option);
                        if (editTextOption != null) {
                            String optionText = editTextOption.getText().toString().trim();
                            if (optionText.isEmpty()) {
                                hasEmptyField = true;
                                break;
                            }
                            options.add(optionText);
                        }
                    }

                    if (hasEmptyField) {
                        break;
                    }

                    jsonObject.put("option", String.join(", ", options));
                    jsonArray.put(jsonObject);
                }
            }
        }

        if (jsonArray.length() > 0) {
            if (hasEmptyField) {
                Toast.makeText(this, "Please fill all the fields or delete empty fields!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(AddVariationActivity.this, AddPriceActivity.class);
                intent.putExtra("product_name", productName);
                intent.putExtra("product_desc", productDesc);
                intent.putExtra("product_cat", productCat);
                intent.putExtra("product_base_price", productBasePrice);
                intent.putExtra("product_offer_price", productOfferPrice);
                intent.putExtra("product_img", productImg);
                intent.putExtra("product_variation", jsonArray.toString());
                if (parsedPriceJson != null && !parsedPriceJson.isEmpty()) {
                    intent.putExtra("parsedPrice", parsedPriceJson);
                }
                if (productId != null && !productId.isEmpty()) {
                    intent.putExtra("pid", productId);
                }
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "No Variation Created! Saving Product.", Toast.LENGTH_SHORT).show();
            // Save here only and finish
            if (productId == null || productId.isEmpty()) {
                sendProductToServer();
            } else {
                updateProduct();
            }
        }
    }

    private void sendProductToServer() {
        binding.progressBar.setVisibility(View.VISIBLE);

        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), productName);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), productDesc);
        RequestBody catBody = RequestBody.create(MediaType.parse("text/plain"), productCat);
        RequestBody basePriceBody = RequestBody.create(MediaType.parse("text/plain"), productBasePrice);
        RequestBody offerPriceBody = RequestBody.create(MediaType.parse("text/plain"), productOfferPrice);
        RequestBody variationBody = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), "");

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
                            startActivity(new Intent(AddVariationActivity.this, ProductActivity.class));
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

    private void updateProduct() {
        binding.progressBar.setVisibility(View.VISIBLE);

        RequestBody pidBody = RequestBody.create(MediaType.parse("text/plain"), productId);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), productName);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), productDesc);
        RequestBody catBody = RequestBody.create(MediaType.parse("text/plain"), productCat);
        RequestBody basePriceBody = RequestBody.create(MediaType.parse("text/plain"), productBasePrice);
        RequestBody offerPriceBody = RequestBody.create(MediaType.parse("text/plain"), productOfferPrice);
        RequestBody variationBody = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), "");

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
                            startActivity(new Intent(AddVariationActivity.this, ProductActivity.class));
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
            ApiCall.getApiService().updateProductWithoutFile(productId, productName, productDesc, productCat, "", "", productBasePrice, productOfferPrice).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(@NonNull Call<CallResponse> call, @NonNull Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse callResponse = response.body();
                        if (callResponse != null && callResponse.isSuccess()) {
                            showToast(callResponse.getMessage());
                            startActivity(new Intent(AddVariationActivity.this, ProductActivity.class));
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

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}