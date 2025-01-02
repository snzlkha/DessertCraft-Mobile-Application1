package com.yourapp.desertcraft.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.FragmentPreviewBinding;
import com.yourapp.desertcraft.model.CartItem;
import com.yourapp.desertcraft.model.ShopData;
import com.yourapp.desertcraft.response.CallResponse;
import com.yourapp.desertcraft.response.ShopResponse;
import com.yourapp.desertcraft.storage.DatabaseHelper;
import com.yourapp.desertcraft.storage.DatabaseManager;
import com.yourapp.desertcraft.utils.FileUtils;
import com.yourapp.desertcraft.utils.PrefManager;
import com.yourapp.desertcraft.utils.ReplaceFragmentListener;
import com.yourapp.desertcraft.utils.Val;
import com.yourapp.desertcraft.view.FadeOutDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviewFragment extends Fragment {
    private FragmentPreviewBinding binding;
    int selectedId;
    DatabaseManager databaseManager;
    PrefManager prefManager;
    private List<ShopData> shopData = new ArrayList<>();
    private ReplaceFragmentListener replaceFragmentListener;
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    this::handleSelectedFile);
    Uri fileUri;
    int pointsAvailable = 0;
    int pointUsed = 0;
    double discountedPrice = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ReplaceFragmentListener) {
            replaceFragmentListener = (ReplaceFragmentListener) context;
        } else {
            throw new ClassCastException(context + " must implement ClickListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPreviewBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedId = bundle.getInt("selectedItemId");
            pointsAvailable = bundle.getInt("availablePoints");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(requireContext());
        databaseManager = new DatabaseManager(requireContext());
        databaseManager.open();
        if (selectedId != 0) {
            getRecordById(selectedId);
        } else {
            showToast("Unable to get your cart data.");
        }

        getShopLocation();

        binding.swipeRefresh.setOnRefreshListener(this::getShopLocation);
        binding.tvChooseDesign.setOnClickListener(view1 -> openFilePicker());
        binding.btnBack.setOnClickListener(view12 -> {
            if (replaceFragmentListener != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void openFilePicker() {
        filePickerLauncher.launch("image/*");
    }

    private void handleSelectedFile(Uri uri) {
        if (uri != null) {
            String fileType = requireActivity().getContentResolver().getType(uri);
            if (fileType != null) {
                fileUri = uri;
                String fileName = getFileName(uri);
                binding.tvChooseDesign.setText(fileName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = requireActivity().getContentResolver().query(uri, null,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }

    private void getRecordById(long id) {
        Cursor cursor = databaseManager.getRecordById(id);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int uidIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_UID);
            int didIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DID);
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
            int descIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESC);
            int imageIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE);
            int customizeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMIZE);
            int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);
            int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE);
            int quantityIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY);
            int createdOnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_ON);

            CartItem item = new CartItem(
                    cursor.getInt(idIndex),
                    cursor.getString(uidIndex),
                    cursor.getString(didIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(descIndex),
                    cursor.getString(imageIndex),
                    cursor.getString(customizeIndex),
                    cursor.getString(dateIndex),
                    cursor.getDouble(priceIndex),
                    cursor.getInt(quantityIndex),
                    cursor.getString(createdOnIndex)
            );

            updateView(item);

            cursor.close();
        }
    }

    private void updateView(CartItem item) {
        if (item.getDate().isEmpty()) {
            binding.tvPickupDate.setText(R.string.today);
        } else {
            binding.tvPickupDate.setText(item.getDate());
        }

        binding.tvProductId.setText(String.format("Product ID: %s", item.getDid()));
        binding.tvDesertName.setText(item.getName());
        binding.tvPrice.setText("Price: RM" + String.valueOf(item.getPrice()).replace(".0", ""));

        if (pointsAvailable != 0) {
            discountedPrice = calculateDiscountedPrice(pointsAvailable, (int) item.getPrice(), Val.POINT_VALUE);
            if (discountedPrice != item.getPrice()) {
                binding.tvDiscountedPrice.setVisibility(View.VISIBLE);
                binding.tvDiscountedPrice.setText("RM" + discountedPrice);
                binding.tvPrice.setPaintFlags(binding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                pointUsed = calculatePointsToUse(pointsAvailable, (int) item.getPrice(), Val.POINT_VALUE);
            }
        }

        if (!item.getImage().isEmpty()) {
            Picasso.get().load(Val.IMG_URL + item.getImage()).into(binding.imgDesert);
        }
        if (!item.getCustomization().isEmpty()) {
            binding.tvCustomizations.setText(item.getCustomization());
        }

        binding.imgDelete.setOnClickListener(view -> {
            int result = databaseManager.deleteRecord(item.getId());
            if (result > 0) {
                showToast("Item removed from Cart.");
                getParentFragmentManager().popBackStack();
            } else {
                showToast("Failed to delete item!");
            }
        });

        binding.imgEdit.setOnClickListener(view -> {
            if (replaceFragmentListener != null) {
                replaceFragmentListener.onEditItemClick(item.getId(), item.getDid());
            }
        });

        binding.btnSubmitOrder.setOnClickListener(view -> {
            String instruction = binding.etInstruction.getText().toString().trim();
            if (!binding.cbNote.isChecked()) {
                showToast("ACCEPT NOTE TO PLACE ORDER");
            } else {
                saveOrder(item, instruction);
            }
        });
    }

    public double calculateDiscountedPrice(int availablePoints, int itemPrice, double pointsValue) {
        int pointsToUse = (availablePoints / 1000) * 1000;
        if (pointsToUse < 1000) {
            pointsToUse = 0;  // Minimum points to use is 1000
        }

        double maxDiscount = itemPrice * 0.5; // 50% of the item's price
        double discountFromPoints = pointsToUse * pointsValue;
        double actualDiscount = Math.min(maxDiscount, discountFromPoints);

        return itemPrice - actualDiscount;
    }

    public static int calculatePointsToUse(int availablePoints, int itemPrice, double pointsValue) {
        int pointsToUse = (availablePoints / 1000) * 1000;
        if (pointsToUse < 1000) {
            pointsToUse = 0;
        }

        double maxDiscount = itemPrice * 0.5; // 50% of the item's price
        double discountFromPoints = pointsToUse * pointsValue;
        double actualDiscount = Math.min(maxDiscount, discountFromPoints);

        return (int) (actualDiscount / pointsValue);
    }

    private void getShopLocation() {
        ApiCall.getApiService().getShopLocation().enqueue(new Callback<ShopResponse>() {
            @Override
            public void onResponse(Call<ShopResponse> call, Response<ShopResponse> response) {
                if (response.isSuccessful()) {
                    ShopResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        ShopData shopData1 = response1.getData();
                        shopData.add(shopData1);
                        updateUI(shopData1);
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
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ShopResponse> call, Throwable t) {
                showToast("Some error occurred");
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void updateUI(ShopData data) {
        if (!data.getImage().isEmpty()) {
            Picasso.get().load(Val.IMG_URL + data.getImage()).into(binding.imgShop);
        }
        binding.tvShopName.setText(data.getName());
        String location = data.getLocation();
        SpannableString spannableString = new SpannableString(location);
        int firstLineMargin = (int) (22 * getResources().getDisplayMetrics().density);
        LeadingMarginSpan leadingMarginSpan = new LeadingMarginSpan.Standard(firstLineMargin, 0);
        spannableString.setSpan(leadingMarginSpan, 0, location.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvShopLocation.setText(spannableString);
        if (location.isEmpty()) {
            binding.tvShopLocation.setVisibility(View.GONE);
        }
    }

    private void saveOrder(CartItem item, String instruction) {
        String price;
        if (discountedPrice != 0) {
            price = String.valueOf(discountedPrice);
        } else {
            price = String.valueOf(item.getPrice());
        }
        String qty = String.valueOf(item.getQuantity());

        if (fileUri != null) {
            RequestBody uidB = RequestBody.create(MediaType.parse("text/plain"), prefManager.getUid());
            RequestBody didB = RequestBody.create(MediaType.parse("text/plain"), item.getDid());
            RequestBody imageB = RequestBody.create(MediaType.parse("text/plain"), item.getImage());
            RequestBody nameB = RequestBody.create(MediaType.parse("text/plain"), item.getName());
            RequestBody descB = RequestBody.create(MediaType.parse("text/plain"), item.getDesc());
            RequestBody custB = RequestBody.create(MediaType.parse("text/plain"), item.getCustomization());
            RequestBody priceB = RequestBody.create(MediaType.parse("text/plain"), price);
            RequestBody qtyB = RequestBody.create(MediaType.parse("text/plain"), qty);
            RequestBody dateB = RequestBody.create(MediaType.parse("text/plain"), item.getDate());
            RequestBody instB = RequestBody.create(MediaType.parse("text/plain"), instruction);
            RequestBody pointB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(pointUsed));

            String filePath = FileUtils.getRealPathFromURI(fileUri, requireContext());
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("design", file.getName(), requestFile);

            ApiCall.getApiService().saveOrderWithDesign(uidB, didB, imageB, nameB, descB, custB, priceB, qtyB, dateB,
                    pointB, instB, filePart).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse response1 = response.body();
                        if (response1 != null && response1.isSuccess()) {

                            int result = databaseManager.deleteRecord(selectedId);
                            if (result > 0) {
                                binding.imgDelete.setVisibility(View.GONE);
                            } else {
                                showToast("Failed to remove from cart!");
                            }

                            openThanksDialog();
                        } else {
                            if (response1 != null) {
                                showToast(response1.getMessage());
                            } else {
                                showToast("Some error occurred.");
                            }
                        }
                    } else {
                        showToast("Some error occurred.");
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    showToast("Some error occurred.");
                }
            });
        } else {
            ApiCall.getApiService().saveOrder(prefManager.getUid(), item.getDid(), item.getImage(), item.getName(), item.getDesc(), item.getCustomization(), price, qty, item.getDate(), String.valueOf(pointUsed), instruction).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse response1 = response.body();
                        if (response1 != null && response1.isSuccess()) {

                            int result = databaseManager.deleteRecord(selectedId);
                            if (result > 0) {
                                binding.imgDelete.setVisibility(View.GONE);
                            } else {
                                showToast("Failed to remove from cart!");
                            }

                            openThanksDialog();
                        } else {
                            if (response1 != null) {
                                showToast(response1.getMessage());
                            } else {
                                showToast("Some error occurred.");
                            }
                        }
                    } else {
                        showToast("Some error occurred.");
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    showToast("Some error occurred.");
                }
            });
        }
    }

    private void openThanksDialog() {
        FadeOutDialog dialog = new FadeOutDialog(requireContext());
        dialog.setContentView(R.layout.dialog_thankyou);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().getAttributes().windowAnimations = R.style.FadeInDialogAnimation;
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
        }
        dialog.setCancelable(false);
        Button button = dialog.findViewById(R.id.btn_back_to_order);

        button.setOnClickListener(view -> {
            dialog.dismiss();
            openOrdersFragment();
        });

        dialog.show();
    }

    private void openOrdersFragment() {
        if (replaceFragmentListener != null) {
            replaceFragmentListener.onTrackNowClick();
        }
    }

    private void showToast(String text) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        replaceFragmentListener = null;
    }
}