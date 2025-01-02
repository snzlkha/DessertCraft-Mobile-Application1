package com.yourapp.desertcraftadmin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yourapp.desertcraftadmin.api.ApiCall;
import com.yourapp.desertcraftadmin.databinding.ActivityUploadBannerBinding;
import com.yourapp.desertcraftadmin.model.ShopData;
import com.yourapp.desertcraftadmin.response.CallResponse;
import com.yourapp.desertcraftadmin.response.ShopResponse;
import com.yourapp.desertcraftadmin.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadBannerActivity extends AppCompatActivity {
    private ActivityUploadBannerBinding binding;

    ShopData shopData;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    this::handleSelectedFile);
    Uri fileUriShop;
    Uri fileUriBanner;
    boolean selectedShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getShopLocation();
        binding.tvShopBanner.setOnClickListener(view -> {
            selectedShop = true;
            openFilePicker();
        });

        binding.btnSaveShop.setOnClickListener(view -> {
            String name = binding.etShopName.getText().toString().trim();
            String location = binding.etShopLocation.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                showToast("Enter Shop Name");
            } else if (TextUtils.isEmpty(location)) {
                showToast("Enter shop location");
            } else {
                if (shopData != null) {
                    updateShop(shopData.getId(), name, location);
                } else {
                    if (fileUriShop == null) {
                        showToast("Select shop banner");
                    } else {
                        uploadShop(name, location);
                    }
                }
            }
        });

        binding.tvPromoBanner.setOnClickListener(view -> {
            selectedShop = false;
            openFilePicker();
        });

        binding.btnSavePromo.setOnClickListener(view -> {
            if (fileUriBanner == null) {
                showToast("Select promotion banner");
            } else {
                uploadBanner();
            }
        });
    }

    private void getShopLocation() {
        ApiCall.getApiService().getShopLocation().enqueue(new Callback<ShopResponse>() {
            @Override
            public void onResponse(Call<ShopResponse> call, Response<ShopResponse> response) {
                if (response.isSuccessful()) {
                    ShopResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        shopData = response1.getData();
                        binding.tvShopBanner.setText(shopData.getImage());
                        binding.etShopName.setText(shopData.getName());
                        binding.etShopLocation.setText(shopData.getLocation());
                    } else {
                        if (response1 != null) {
                            showToast(response1.getMessage());
                        } else {
                            showToast("Some error occurred");
                        }
                    }
                } else {
                    showToast("Some error occurred");
                }
            }

            @Override
            public void onFailure(Call<ShopResponse> call, Throwable t) {
                showToast("Some error occurred");
            }
        });
    }


    private void openFilePicker() {
        filePickerLauncher.launch("image/*");
    }

    private void handleSelectedFile(Uri uri) {
        if (uri != null) {
            String fileType = getContentResolver().getType(uri);
            if (fileType != null) {
                if (selectedShop) {
                    fileUriShop = uri;
                    String fileName = getFileName(uri);
                    binding.tvShopBanner.setText(fileName);
                } else {
                    fileUriBanner = uri;
                    String fileName = getFileName(uri);
                    binding.tvPromoBanner.setText(fileName);
                    Glide.with(this)
                            .load(uri)
                            .into(binding.imgBannerSelected);
                }

            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }

    private void uploadShop(String name, String location) {

        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody locationBody = RequestBody.create(MediaType.parse("text/plain"), location);

        if (fileUriShop != null) {
            String filePath = FileUtils.getRealPathFromURI(fileUriShop, this);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            ApiCall.getApiService().uploadShopWithFile(nameBody, locationBody, filePart).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse callResponse = response.body();
                        if (callResponse != null && callResponse.isSuccess()) {
                            showToast(callResponse.getMessage());
                            finish();
                        } else {
                            showToast("Some error occurred.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    showToast("Some error occurred.");
                }
            });
        }
    }

    private void updateShop(String id, String name, String location) {

        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody locationBody = RequestBody.create(MediaType.parse("text/plain"), location);

        if (fileUriShop != null) {
            String filePath = FileUtils.getRealPathFromURI(fileUriShop, this);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            ApiCall.getApiService().updateShopWithFile(idBody, nameBody, locationBody, filePart).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse callResponse = response.body();
                        if (callResponse != null && callResponse.isSuccess()) {
                            showToast(callResponse.getMessage());
                            finish();
                        } else {
                            showToast("Some error occurred.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    showToast("Some error occurred.");
                }
            });
        } else {
            ApiCall.getApiService().updateShopWithoutFile(id, name, location).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse callResponse = response.body();
                        if (callResponse != null && callResponse.isSuccess()) {
                            showToast(callResponse.getMessage());
                            finish();
                        } else {
                            showToast("Some error occurred.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    showToast("Some error occurred.");
                }
            });
        }
    }

    private void uploadBanner() {

        if (fileUriBanner != null) {
            String filePath = FileUtils.getRealPathFromURI(fileUriBanner, this);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            ApiCall.getApiService().updateBannerWithFile(filePart).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse callResponse = response.body();
                        if (callResponse != null && callResponse.isSuccess()) {
                            showToast(callResponse.getMessage());
                            finish();
                        } else {
                            showToast("Some error occurred.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    showToast("Some error occurred.");
                }
            });
        }
    }

    private void showToast(String text) {
        Toast.makeText(UploadBannerActivity.this, text, Toast.LENGTH_SHORT).show();
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