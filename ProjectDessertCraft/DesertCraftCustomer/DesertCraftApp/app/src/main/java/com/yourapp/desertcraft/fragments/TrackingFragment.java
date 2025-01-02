package com.yourapp.desertcraft.fragments;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.FragmentTrackingBinding;
import com.yourapp.desertcraft.model.OrderData;
import com.yourapp.desertcraft.response.CallResponse;
import com.yourapp.desertcraft.response.TrackResponse;
import com.yourapp.desertcraft.utils.Constant;
import com.yourapp.desertcraft.utils.FileUtils;
import com.yourapp.desertcraft.utils.PrefManager;
import com.yourapp.desertcraft.utils.Val;
import com.yourapp.desertcraft.view.FadeOutDialog;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingFragment extends Fragment {
    private FragmentTrackingBinding binding;
    PrefManager prefManager;
    private float currentRating = 0;
    int trackingId;
    int confirmation = 0;
    float baseP = 0;
    float addP = 0;
    float totalP = 0;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    this::handleSelectedFile);
    Uri fileUri;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackingBinding.inflate(inflater, container, false);
        prefManager = new PrefManager(requireContext());
        Bundle bundle = getArguments();
        if (bundle != null) {
            trackingId = bundle.getInt("orderId");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trackOrder();

        binding.swipeRefresh.setOnRefreshListener(this::trackOrder);
    }

    private void trackOrder() {
        if (trackingId != 0) {
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

        binding.btnBack.setOnClickListener(view -> getParentFragmentManager().popBackStack());
    }

    private void updateUI(OrderData data) {
        if (data.getOrder_status() == 0) {
            binding.tvOrderStatus.setText(Val.status0);
            binding.imgOrderStatus.setImageResource(R.drawable.clock);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(requireContext(), R.color.coco), PorterDuff.Mode.SRC_IN);
            binding.layoutBtn.setVisibility(View.VISIBLE);
        } else if (data.getOrder_status() == 1) {
            binding.tvOrderStatus.setText(Val.status2);
            binding.imgOrderStatus.setImageResource(R.drawable.saving);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green), PorterDuff.Mode.SRC_IN);
            binding.layoutBtn.setVisibility(View.VISIBLE);
            binding.btnConfirmOrder.setVisibility(View.VISIBLE);
            binding.layoutPaymentAccount.setVisibility(View.VISIBLE);
        } else if (data.getOrder_status() == 2) {
            binding.tvOrderStatus.setText(Val.status1);
            binding.imgOrderStatus.setImageResource(R.drawable.checked);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(requireContext(), R.color.coco), PorterDuff.Mode.SRC_IN);
            binding.layoutBtn.setVisibility(View.GONE);
            binding.layoutPaymentAccount.setVisibility(View.GONE);
        } else if (data.getOrder_status() == 3) {
            binding.layoutBtn.setVisibility(View.GONE);
            binding.tvOrderStatus.setText(Val.status3);
            binding.imgOrderStatus.setImageResource(R.drawable.chef);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), PorterDuff.Mode.SRC_IN);
            binding.layoutBtn.setVisibility(View.GONE);
            binding.layoutPaymentAccount.setVisibility(View.GONE);
        } else if (data.getOrder_status() == 4) {
            binding.tvOrderStatus.setText(Val.status4);
            binding.imgOrderStatus.setImageResource(R.drawable.pickup);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(requireContext(), R.color.coco), PorterDuff.Mode.SRC_IN);
            binding.layoutBtn.setVisibility(View.GONE);
            binding.layoutPaymentAccount.setVisibility(View.GONE);
        } else if (data.getOrder_status() == 5) {
            binding.tvOrderStatus.setText(Val.status5);
            binding.imgOrderStatus.setImageResource(R.drawable.checked);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green), PorterDuff.Mode.SRC_IN);
            binding.layoutBtn.setVisibility(View.GONE);
            binding.layoutPaymentAccount.setVisibility(View.GONE);
            showReviewOption(data.getId(), data.getDesert_id());

        } else if (data.getOrder_status() == 6) {
            binding.tvOrderStatus.setText(Val.status6);
            binding.imgOrderStatus.setImageResource(R.drawable.info_alt);
            binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light), PorterDuff.Mode.SRC_IN);

            binding.text2.setText(Val.status6);
            binding.text2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
            binding.text6.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
            binding.img2.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light), PorterDuff.Mode.SRC_IN);
            binding.img6.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light), PorterDuff.Mode.SRC_IN);
            binding.layoutBtn.setVisibility(View.GONE);
            binding.layoutPaymentAccount.setVisibility(View.GONE);
        }

        if (data.getTracking_status() == 0) {
            binding.img1.setImageResource(R.drawable.radio_filled);
            binding.text1.setTypeface(binding.text1.getTypeface(), Typeface.BOLD);
        } else if (data.getTracking_status() == 1) {
            binding.img2.setImageResource(R.drawable.radio_filled);
            binding.text2.setTypeface(binding.text2.getTypeface(), Typeface.BOLD);
        } else if (data.getTracking_status() == 2) {
            binding.img3.setImageResource(R.drawable.radio_filled);
            binding.text3.setTypeface(binding.text3.getTypeface(), Typeface.BOLD);
        } else if (data.getTracking_status() == 3) {
            binding.img4.setImageResource(R.drawable.radio_filled);
            binding.text4.setTypeface(binding.text4.getTypeface(), Typeface.BOLD);
        } else if (data.getTracking_status() == 4) {
            binding.img5.setImageResource(R.drawable.radio_filled);
            binding.text5.setTypeface(binding.text5.getTypeface(), Typeface.BOLD);
        } else if (data.getTracking_status() == 5) {
            binding.img6.setImageResource(R.drawable.radio_filled);
            binding.text6.setTypeface(binding.text6.getTypeface(), Typeface.BOLD);
        }

        binding.tvOrderId.setText(String.format("Order ID: %s", data.getId()));
        binding.tvDesertName.setText(data.getDesert_name());

        if (!data.getImage().isEmpty()) {
            Picasso.get().load(Val.IMG_URL + data.getImage()).into(binding.imgDesert);
        }

        binding.tvPrice.setText("Price: RM" + data.getPrice());
        String custom = Constant.cleanUpString(data.getCustomization());
        binding.tvCustomizations.setText(custom);
        binding.tvOrderQty.setText("Qty: " + data.getQuantity());

        baseP = Float.parseFloat(data.getPrice());
        if (data.getAdd_on_price() != null && !data.getAdd_on_price().isEmpty()) {
            addP = Float.parseFloat(data.getAdd_on_price());
            totalP = baseP + addP;
        } else {
            totalP = baseP;
            binding.layoutAddonPrice.setVisibility(View.GONE);
        }

        binding.tvBasicPrice.setText("RM" + baseP);
        binding.tvAddonPrice.setText("RM" + addP);
        binding.tvTotalPrice.setText("RM" + totalP);

        if (!data.getRating().isEmpty()) {
            binding.layoutSubmitReview.setVisibility(View.VISIBLE);
            binding.tvRatingStatus.setVisibility(View.VISIBLE);
            currentRating = Float.parseFloat(data.getRating());
            binding.btnSubmitReview.setVisibility(View.GONE);
            binding.ratingBar.setRating(currentRating);
            binding.ratingBar.setIsIndicator(true);
            binding.tvRatingStatus.setText("Thankyou For Rating");
            binding.etComments.setOnFocusChangeListener((view, b) -> {
                binding.etComments.clearFocus();
                binding.etComments.setCursorVisible(false);
            });
        }
        if (!data.getComments().isEmpty()) {
            binding.etComments.setText(data.getComments());
        }

        binding.btnConfirmOrder.setOnClickListener(view -> forwardOrder(String.valueOf(trackingId)));

        binding.btnWithdrawOrder.setOnClickListener(view -> withdrawOrder(String.valueOf(trackingId)));

        binding.btnSelectReceipt.setOnClickListener(view -> openFilePicker());
    }

    private void openFilePicker() {
        filePickerLauncher.launch("application/pdf");
    }

    private void handleSelectedFile(Uri uri) {
        if (uri != null) {
            String fileType = requireActivity().getContentResolver().getType(uri);
            if (fileType != null) {
                fileUri = uri;
                String fileName = getFileName(uri);
                binding.btnSelectReceipt.setText(fileName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }

    private void withdrawOrder(String orderId) {
        ApiCall.getApiService().withdrawOrder(prefManager.getUid(), orderId).enqueue(new Callback<CallResponse>() {
            @Override
            public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                if (response.isSuccessful()) {
                    CallResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        confirmation = 2;
                        openThanksDialog();
                    } else {
                        if (response1 != null) {
                            showToast(response1.getMessage());
                        } else {
                            showToast("Couldn't Withdraw!");
                        }
                    }
                } else {
                    showToast("Couldn't Withdraw!");
                }
            }

            @Override
            public void onFailure(Call<CallResponse> call, Throwable t) {
                showToast("Couldn't Withdraw!");
            }
        });
    }

    private void forwardOrder(String trackId) {
        if (fileUri != null) {
            RequestBody oStatus = RequestBody.create(MediaType.parse("text/plain"), "2");
            RequestBody tStatus = RequestBody.create(MediaType.parse("text/plain"), "2");
            RequestBody tId = RequestBody.create(MediaType.parse("text/plain"), trackId);
            RequestBody uId = RequestBody.create(MediaType.parse("text/plain"), prefManager.getUid());

            String filePath = FileUtils.getRealPathFromURI(fileUri, requireContext());
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("receipt", file.getName(), requestFile);
            ApiCall.getApiService().forwardOrderWithReceipt(oStatus, tStatus, tId, uId, filePart).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CallResponse response1 = response.body();
                        if (response1.isSuccess()) {
                            confirmation = 1;
                            openThanksDialog();
                        } else {
                            showToast(response1.getMessage());
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
        } else {
            showToast("Please Select Valid Payment Receipt!");
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
        TextView thanks = dialog.findViewById(R.id.tv_thankyou);

        if (confirmation == 1) {
            thanks.setText("Thankyou for Your\nConfirmation!");
        } else if (confirmation == 2) {
            thanks.setText("Order Withdrawn\nSuccessfully!");
        }

        button.setOnClickListener(view -> {
            dialog.dismiss();
            getParentFragmentManager().popBackStack();
        });

        dialog.show();
    }

    private void showToast(String text) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void showReviewOption(int orderId, int desertId) {
        binding.layoutSubmitReview.setVisibility(View.VISIBLE);
        binding.tvRatingStatus.setVisibility(View.VISIBLE);

        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, b) -> currentRating = rating);

        binding.btnSubmitReview.setOnClickListener(view -> {
            String comments = binding.etComments.getText().toString().trim();
            ApiCall.getApiService().saveRating(orderId, desertId, currentRating, comments).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful()) {
                        CallResponse response1 = response.body();
                        if (response1 != null && response1.isSuccess()) {
                            Toast.makeText(requireContext(), response1.getMessage(), Toast.LENGTH_SHORT).show();
                            binding.ratingBar.setRating(currentRating);
                            binding.ratingBar.setIsIndicator(true);
                            binding.btnSubmitReview.setVisibility(View.GONE);
                            binding.tvRatingStatus.setText("Thankyou For Rating");
                            binding.etComments.clearFocus();
                            binding.etComments.setOnFocusChangeListener((view, b) -> {
                                binding.etComments.clearFocus();
                                binding.etComments.setCursorVisible(false);
                            });
                        } else {
                            Toast.makeText(requireContext(), "Failed to save rating!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to save rating!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    Toast.makeText(requireContext(), "Some error occurred!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}