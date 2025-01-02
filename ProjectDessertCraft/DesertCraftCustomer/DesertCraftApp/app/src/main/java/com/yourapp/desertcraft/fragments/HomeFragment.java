package com.yourapp.desertcraft.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.yourapp.desertcraft.LoginActivity;
import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.FragmentHomeBinding;
import com.yourapp.desertcraft.response.BannerResponse;
import com.yourapp.desertcraft.storage.DatabaseManager;
import com.yourapp.desertcraft.utils.ReplaceFragmentListener;
import com.yourapp.desertcraft.utils.PrefManager;
import com.yourapp.desertcraft.utils.Val;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ReplaceFragmentListener replaceFragmentListener;
    PrefManager prefManager;
    DatabaseManager databaseManager;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        databaseManager = new DatabaseManager(requireContext());
        databaseManager.open();
        prefManager = new PrefManager(requireContext());

        String partOfDay = getPartOfDay();
        binding.tvName.setText(String.format("%s%s!", partOfDay, prefManager.getName()));

        binding.orderNow.setOnClickListener(view -> {
            if (replaceFragmentListener != null) {
                replaceFragmentListener.onOrderNowClick();
            }
        });

        binding.trackNow.setOnClickListener(view -> {
            if (replaceFragmentListener != null) {
                replaceFragmentListener.onTrackNowClick();
            }
        });

        binding.imgCart.setOnClickListener(view -> {
            if (replaceFragmentListener != null) {
                replaceFragmentListener.openCartFragment();
            }
        });

        binding.btnLogout.setOnClickListener(view -> {
            prefManager.signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finishAffinity();
        });

        updateCartCount();
        getPromoBanner();
        setBackPressListener();

        return binding.getRoot();
    }

    private void updateCartCount() {
        Cursor cursor;

        try {
            cursor = databaseManager.getAllData();
            if (cursor != null) {
                int cartCount = cursor.getCount();
                if (cartCount > 0) {
                    binding.tvCartSize.setVisibility(View.VISIBLE);
                    binding.tvCartSize.setText(String.valueOf(cartCount));
                } else {
                    binding.tvCartSize.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPartOfDay() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "Good Morning, ";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon,";
        } else if (hour >= 17 && hour < 21) {
            return "Good Evening, ";
        } else {
            return "Good Night, ";
        }
    }

    private void getPromoBanner() {
        ApiCall.getApiService().getPromoBanner().enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(@NonNull Call<BannerResponse> call, @NonNull Response<BannerResponse> response) {
                if (response.isSuccessful()) {
                    BannerResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        Picasso.get().load(Val.IMG_URL + response1.getData().getImage()).into(binding.imgPromo);
                        binding.cardBanner.setVisibility(View.VISIBLE);
                    } else {
                        binding.cardBanner.setVisibility(View.INVISIBLE);
                    }
                } else {
                    binding.cardBanner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerResponse> call, @NonNull Throwable t) {
                binding.cardBanner.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setBackPressListener() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finishAffinity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        replaceFragmentListener = null;
    }
}