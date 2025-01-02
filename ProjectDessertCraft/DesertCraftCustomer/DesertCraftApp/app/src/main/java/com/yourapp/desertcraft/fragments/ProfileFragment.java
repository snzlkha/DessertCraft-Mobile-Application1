package com.yourapp.desertcraft.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.FragmentProfileBinding;
import com.yourapp.desertcraft.model.UserData;
import com.yourapp.desertcraft.response.CallResponse;
import com.yourapp.desertcraft.response.ProfileResponse;
import com.yourapp.desertcraft.utils.FileUtils;
import com.yourapp.desertcraft.utils.PrefManager;
import com.yourapp.desertcraft.utils.Val;
import com.yourapp.desertcraft.view.FadeOutDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    PrefManager prefManager;
    Context mContext;
    Uri fileUri;
    FadeOutDialog dialog;
    FadeOutDialog dialog1;
    CircleImageView imageProfile;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    this::handleSelectedFile);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(requireContext());
        binding.btnBack.setOnClickListener(view1 -> getParentFragmentManager().popBackStack());

        getProfile();
    }

    private void getProfile() {
        ApiCall.getApiService().getProfile(prefManager.getUid()).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {

                        binding.tvName.setText("Name: " + apiResponse.getData().getName());
                        binding.tvMobile.setText("Mobile: " + apiResponse.getData().getMobile());
                        if (apiResponse.getData().getImage() != null && !apiResponse.getData().getImage().isEmpty()) {
                            Glide.with(mContext).load(Val.IMG_URL + apiResponse.getData().getImage()).into(binding.imgUserProfile);
                        }

                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        try {
                            Date date = inputFormat.parse(apiResponse.getData().getCreated_on());
                            String formattedDate = outputFormat.format(date);
                            binding.tvCreatedOn.setText("Member since: " + formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (apiResponse.getCount().getTotal_orders() != null && !apiResponse.getCount().getTotal_orders().isEmpty()) {
                            binding.tvTotalOrders.setText("Your total orders:  " + apiResponse.getCount().getTotal_orders());
                        }

                        binding.btnEditProfile.setOnClickListener(view -> {
                            openEditDialog(apiResponse.getData());
                        });

                        binding.btnEditPassword.setOnClickListener(v -> openPasswordDialog());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {

            }
        });
    }

    private void openEditDialog(UserData data) {
        dialog = new FadeOutDialog(mContext);
        dialog.setContentView(R.layout.dialog_edit_profile);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().getAttributes().windowAnimations = R.style.FadeInDialogAnimation;
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
        }
        dialog.setCancelable(true);
        imageProfile = dialog.findViewById(R.id.img_user_edit);
        EditText etName = dialog.findViewById(R.id.et_name);
        EditText etEmail = dialog.findViewById(R.id.et_email);
        EditText etMobile = dialog.findViewById(R.id.et_mobile);
        Button buttonUpdate = dialog.findViewById(R.id.btn_update);

        if (data.getImage() != null && !data.getImage().isEmpty()) {
            Glide.with(mContext).load(Val.IMG_URL + data.getImage()).into(imageProfile);
        }
        etName.setText(data.getName());
        etEmail.setText(data.getEmail());
        etMobile.setText(data.getMobile());

        imageProfile.setOnClickListener(view -> openFilePicker());

        buttonUpdate.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();

            String mobileRegex = "^[1-9][0-9]{9}$";
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            Matcher mobileMatcher = mobilePattern.matcher(mobile);

            if (TextUtils.isEmpty(name)) {
                showToast("Name Can't be Empty!");
                etName.requestFocus();
            } else if (TextUtils.isEmpty(email)) {
                showToast("Email Can't be Empty!");
                etEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Invalid Email!");
                etEmail.requestFocus();
            } else if (TextUtils.isEmpty(mobile)) {
                showToast("Mobile Can't be Empty!");
                etMobile.requestFocus();
            } else if (mobile.length() != 10) {
                showToast("Required 10 Digit Mobile!");
                etMobile.requestFocus();
            } else if (!mobileMatcher.find()) {
                showToast("Invalid Mobile Number!");
                etMobile.requestFocus();
            } else {
                updateProfile(name, email, mobile);
            }

        });

        dialog.show();
    }

    private void openPasswordDialog() {
        dialog1 = new FadeOutDialog(mContext);
        dialog1.setContentView(R.layout.dialog_update_password);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.getWindow().getAttributes().windowAnimations = R.style.FadeInDialogAnimation;
            WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog1.getWindow().setAttributes(params);
        }
        dialog1.setCancelable(true);
        EditText etCuPass = dialog1.findViewById(R.id.et_current_password);
        EditText etNewPass = dialog1.findViewById(R.id.et_new_pass);
        EditText etConPass = dialog1.findViewById(R.id.et_confirm_pass);
        Button buttonUpdate = dialog1.findViewById(R.id.btn_update);

        buttonUpdate.setOnClickListener(view -> {
            String cuPass = etCuPass.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();
            String conPass = etConPass.getText().toString().trim();

            if (TextUtils.isEmpty(cuPass)) {
                showToast("Enter current password");
                etCuPass.requestFocus();
            } else if (cuPass.length() < 6) {
                showToast("Current password incorrect");
                etCuPass.requestFocus();
            } else if (TextUtils.isEmpty(newPass)) {
                showToast("Enter new password");
                etNewPass.requestFocus();
            } else if (TextUtils.isEmpty(conPass)) {
                showToast("Confirm new password");
                etConPass.requestFocus();
            } else if (!conPass.equals(newPass)) {
                showToast("Current password does not match");
                etConPass.requestFocus();
            } else {
                updatePassword(cuPass, newPass);
            }

        });

        dialog1.show();
    }

    private void updateProfile(String name, String email, String mobile) {
        if (fileUri != null) {
            RequestBody uidB = RequestBody.create(MediaType.parse("text/plain"), prefManager.getUid());
            RequestBody nameB = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody emailB = RequestBody.create(MediaType.parse("text/plain"), email);
            RequestBody mobileB = RequestBody.create(MediaType.parse("text/plain"), mobile);

            String filePath = FileUtils.getRealPathFromURI(fileUri, mContext);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            ApiCall.getApiService().updateProfileWithImage(uidB, nameB, emailB, mobileB, filePart).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CallResponse response1 = response.body();
                        if (response1.isSuccess()) {
                            showToast("Profile Updated");
                            getProfile();
                            dialog.dismiss();
                        } else {
                            showToast(response1.getMessage());
                        }
                    } else {
                        showToast("Some Error Occurred!");
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    showToast("Some Error Occurred!");
                }
            });
        } else {
            ApiCall.getApiService().updateProfileWithoutImage(prefManager.getUid(), name, email, mobile).enqueue(new Callback<CallResponse>() {
                @Override
                public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CallResponse response1 = response.body();
                        if (response1.isSuccess()) {
                            showToast("Profile Updated");
                            getProfile();
                            dialog.dismiss();
                        } else {
                            showToast(response1.getMessage());
                        }
                    } else {
                        showToast("Some Error Occurred!");
                    }
                }

                @Override
                public void onFailure(Call<CallResponse> call, Throwable t) {
                    showToast("Some Error Occurred!");
                }
            });
        }
    }

    private void openFilePicker() {
        filePickerLauncher.launch("image/*");
    }

    private void handleSelectedFile(Uri uri) {
        if (uri != null) {
            String fileType = requireActivity().getContentResolver().getType(uri);

            if (fileType != null) {
                if (fileType.startsWith("image/")) {
                    Glide.with(this)
                            .load(uri)
                            .into(imageProfile);
                } else {
                    showToast("Unsupported file type");
                }
                fileUri = uri;
            }
        }
    }


    private void updatePassword(String cuP, String newP) {
        ApiCall.getApiService().updatePassword(prefManager.getUid(), prefManager.getEmail(), cuP, newP).enqueue(new Callback<CallResponse>() {
            @Override
            public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CallResponse response1 = response.body();
                    if (response1.isSuccess()) {
                        showToast("Password Updated");
                        dialog1.dismiss();
                    } else {
                        showToast(response1.getMessage());
                    }
                } else {
                    showToast("Some Error Occurred!");
                }
            }

            @Override
            public void onFailure(Call<CallResponse> call, Throwable t) {
                showToast("Some Error Occurred!");
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}