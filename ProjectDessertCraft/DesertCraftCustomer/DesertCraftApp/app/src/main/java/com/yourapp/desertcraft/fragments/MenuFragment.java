package com.yourapp.desertcraft.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.adapter.DesertTypeAdapter;
import com.yourapp.desertcraft.adapter.DesertsAdapter;
import com.yourapp.desertcraft.adapter.RatingAdapter;
import com.yourapp.desertcraft.adapter.customize.VariationAdapter;
import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.FragmentMenuBinding;
import com.yourapp.desertcraft.model.CatData;
import com.yourapp.desertcraft.model.RateData;
import com.yourapp.desertcraft.model.RewardData;
import com.yourapp.desertcraft.productlist.ApiResponse;
import com.yourapp.desertcraft.productlist.Price;
import com.yourapp.desertcraft.productlist.Product;
import com.yourapp.desertcraft.productlist.Variation;
import com.yourapp.desertcraft.response.CatResponse;
import com.yourapp.desertcraft.response.RateResponse;
import com.yourapp.desertcraft.response.RewardResponse;
import com.yourapp.desertcraft.storage.DatabaseManager;
import com.yourapp.desertcraft.utils.PrefManager;
import com.yourapp.desertcraft.utils.ReplaceFragmentListener;
import com.yourapp.desertcraft.view.GridSpacingItemDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends Fragment implements VariationAdapter.VariationSelectionListener {
    private FragmentMenuBinding binding;
    private final ArrayList<CatData> catList = new ArrayList<>();
    private DesertTypeAdapter typeAdapter;

    private DesertsAdapter desertsAdapter;
    private final ArrayList<Product> desertList = new ArrayList<>();

    private String selectedDid = "", selectedName = "", selectedDesc = "", selectedImage = "", requiredOnDate = "", customization = "";
    private double selectedPrice = 0, selectedOfferPrice = 0, totalPrice;
    private int selectedQty = 1;

    DatabaseManager databaseManager;
    PrefManager prefManager;

    private ReplaceFragmentListener replaceFragmentListener;

    int selectedItemId;
    String productId;

    private List<RateData> rateList = new ArrayList<>();
    private RatingAdapter ratingAdapter;

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
        binding = FragmentMenuBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedItemId = bundle.getInt("itemId");
            productId = bundle.getString("productId");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(requireContext());

        prefManager = new PrefManager(requireContext());
        databaseManager = new DatabaseManager(requireContext());
        databaseManager.open();

        binding.rvDesertType.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        binding.rvDesertType.setHasFixedSize(true);
        typeAdapter = new DesertTypeAdapter(catList, type -> {
            hideCustomization();
            desertsAdapter.filterList(type.getCid());
            binding.radioButton.setChecked(false);
            binding.radioButton.setTextColor(getResources().getColor(R.color.coco));
        });
        binding.rvDesertType.setAdapter(typeAdapter);

        binding.radioButton.setOnClickListener(view1 -> {
            typeAdapter.uncheckAllRadioButtons();
            desertsAdapter.clearFilter();
            binding.radioButton.setTextColor(getResources().getColor(R.color.white));
        });

        getCategory();
        setupRecyclerView();
        updateCartCount();
        setBackPressListener();

        binding.layoutDesertList.setOnClickListener(view1 -> hideCustomization());
        binding.btnClose.setOnClickListener(view1 -> hideCustomization());

        binding.imgCart.setOnClickListener(view1 -> {
            if (replaceFragmentListener != null) {
                replaceFragmentListener.openCartFragment();
            }
        });

        binding.etSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchQuery = charSequence.toString().trim();
                desertsAdapter.getFilter().filter(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.rvRatings.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        ratingAdapter = new RatingAdapter(rateList);
        binding.rvRatings.setAdapter(ratingAdapter);
    }

    private void getCategory() {
        ApiCall.getApiService().getCategory().enqueue(new Callback<CatResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<CatResponse> call, Response<CatResponse> response) {
                if (response.isSuccessful()) {
                    CatResponse catResponse = response.body();
                    if (catResponse != null && catResponse.isSuccess()) {
                        catList.addAll(catResponse.getData());
                        typeAdapter.notifyDataSetChanged();
                    } else {
                        binding.rvDesertType.setVisibility(View.GONE);
                    }
                } else {
                    binding.rvDesertType.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CatResponse> call, Throwable t) {
                binding.rvDesertType.setVisibility(View.GONE);
            }
        });
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

    private void hideCustomization() {
        if (binding.layoutCustomize.getVisibility() == View.VISIBLE) {
            slideDown(binding.layoutCustomize);
            resetDesertData();
        }
    }

    private void resetDesertData() {
        selectedDid = "";
        selectedName = "";
        selectedDesc = "";
        selectedImage = "";
        customization = "";
        requiredOnDate = "";
        selectedPrice = 0;
        selectedOfferPrice = 0;
        totalPrice = 0;
        selectedQty = 1;
    }

    private void setupRecyclerView() {
        int spanCount = 2;
        int spacing = 16;

        binding.rvDesertList.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        binding.rvDesertList.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing));

        desertsAdapter = new DesertsAdapter(desertList, databaseManager, new DesertsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product data) {
                hideCustomization();
            }

            @Override
            public void onButtonClick(Product data) {
                selectedDid = data.getPid();
                selectedName = data.getPName();
                selectedDesc = data.getDescription();
                selectedImage = data.getPImage();
                selectedPrice = Double.parseDouble(data.getBase_price());
                if (!data.getOffer_price().isEmpty())
                    selectedOfferPrice = Double.parseDouble(data.getOffer_price());

                if (data.getParsedVariation() != null && !data.getParsedVariation().isEmpty()) {
                    setCustomisationData(data);
                } else {
                    hideCustomization();
                    goToMyCart();
                }
            }
        });

        binding.rvDesertList.setAdapter(desertsAdapter);

        getDesertList();
    }

    private void getDesertList() {
        ApiCall.getApiService().getDesertList().enqueue(new Callback<ApiResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        desertList.clear();
                        List<Product> products = apiResponse.getData();
                        if (productId != null && !productId.isEmpty()) {
                            for (Product product : products) {
                                if (product.getPid().equals(productId)) {
                                    desertList.add(product);
                                    break;
                                }
                            }
                        } else {
                            desertList.addAll(products);
                        }

                        // Parse nested JSON strings
                        Gson gson = new Gson();
                        Type variationListType = new TypeToken<List<Variation>>() {
                        }.getType();
                        Type priceListType = new TypeToken<List<Price>>() {
                        }.getType();

                        for (Product product : desertList) {
                            List<Variation> variations = gson.fromJson(product.getVariation(), variationListType);
                            product.setParsedVariation(variations);

                            List<Price> prices = gson.fromJson(product.getPrice(), priceListType);
                            product.setParsedPrice(prices);
                        }
                        desertsAdapter.notifyDataSetChanged();

                        getRewardPoints();
                    } else {
                        showToast(apiResponse.getMessage());
                    }
                } else {
                    showToast("No data available!");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Some error occurred!");
            }
        });
    }

    private void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        Animation slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        view.startAnimation(slideUp);
    }

    private void slideDown(View view) {
        Animation slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);
        view.startAnimation(slideDown);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void setCustomisationData(Product data) {
        slideUp(binding.layoutCustomize);

        if (productId != null && !productId.isEmpty()) {
            binding.btnCheckoutProceed.setText("Update Item");
        }

        binding.btnPriceCalculate.setVisibility(View.VISIBLE);
        binding.btnCheckoutProceed.setVisibility(View.GONE);

        if (!data.getDescription().isEmpty()) {
            binding.tvDescription.setText(data.getDescription());
            binding.tvDescription.setVisibility(View.VISIBLE);
        } else {
            binding.tvDescription.setVisibility(View.GONE);
        }

        binding.rvProductVariation.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.rvProductVariation.setHasFixedSize(true);

        VariationAdapter variationAdapter = new VariationAdapter(data.getParsedVariation());
        variationAdapter.setSelectionListener(this);
        binding.rvProductVariation.setAdapter(variationAdapter);

        binding.tvRequiredOn.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog picker = new DatePickerDialog(requireContext(), (view1, year1, month1, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                binding.tvRequiredOn.setText(selectedDate);
                requiredOnDate = selectedDate;
                binding.tvClearDate.setVisibility(View.VISIBLE);
                binding.tvClearDate.setOnClickListener(view11 -> {
                    binding.tvRequiredOn.setText("");
                    requiredOnDate = "";
                    binding.tvClearDate.setVisibility(View.GONE);
                });
            }, year, month, day);

            picker.show();
        });

        if (data.getOffer_price() != null && !data.getOffer_price().isEmpty()) {
            selectedOfferPrice = Double.parseDouble(data.getOffer_price());
        }
        selectedPrice = Double.parseDouble(data.getBase_price());

        if (selectedOfferPrice != 0) {
            binding.tvDesertPrice.setText("RM" + selectedOfferPrice);
        } else {
            binding.tvDesertPrice.setText("RM" + selectedPrice);
        }

        binding.tvQty.setText(String.valueOf(selectedQty));

        updatePrice();

        binding.tvQtyMinus.setOnClickListener(view -> {
            if (selectedQty > 1) {
                selectedQty--;
                binding.tvQty.setText(String.valueOf(selectedQty));
                updatePrice();
            } else {
                showToast("Minimum quantity selected.");
            }
        });

        binding.tvQtyPlus.setOnClickListener(view -> {
            if (selectedQty > 0) {
                selectedQty++;
                binding.tvQty.setText(String.valueOf(selectedQty));
                updatePrice();
            } else {
                showToast("No more quantity available.");
            }
        });

        binding.btnCheckoutProceed.setOnClickListener(view -> {
            goToMyCart();
        });

        binding.btnPriceCalculate.setOnClickListener(view -> {
            List<String> selectedOptions = variationAdapter.getSelectedOptions();
            StringBuilder selectedOptionsString = new StringBuilder();
            for (String option : selectedOptions) {
                selectedOptionsString.append(option).append(", ");
            }

            String variation = selectedOptionsString.toString().trim();
            if (variation.endsWith(",")) {
                customization = variation.substring(0, variation.length() - 1);
            } else {
                customization = variation;
            }

            boolean foundMatchingPrice = false;
            for (Price price : data.getParsedPrice()) {
                Gson gson = new Gson();
                String jsonPrice = gson.toJson(price);

                int matchedWordsCount = 0;
                for (String option : selectedOptions) {
                    if (jsonPrice.contains(option)) {
                        matchedWordsCount++;
                        break;
                    }
                }

                if (matchedWordsCount > 0) {
                    selectedPrice = Double.parseDouble(price.getPrice());
                    if (price.getOfferPrice() != null && !price.getOfferPrice().isEmpty()) {
                        selectedOfferPrice = Double.parseDouble(price.getOfferPrice());
                    } else {
                        selectedOfferPrice = 0;
                    }
                    foundMatchingPrice = true;
                    break;
                }
            }

            if (foundMatchingPrice) {
                updatePrice();
                binding.btnPriceCalculate.setVisibility(View.GONE);
                binding.btnCheckoutProceed.setVisibility(View.VISIBLE);
            } else {
                showToast("Not found for selected options.");
            }
        });

        getRatings(data.getPid());
    }

    private void getRatings(String pid) {
        ApiCall.getApiService().getRatings(pid).enqueue(new Callback<RateResponse>() {
            @Override
            public void onResponse(Call<RateResponse> call, Response<RateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RateResponse response1 = response.body();
                    if (response1.isSuccess()) {
                        rateList.clear();
                        rateList.addAll(response1.getData());
                        ratingAdapter.notifyDataSetChanged();
                    } else {
                        showToast("No Rating Available.");
                    }
                } else {
                    showToast("Error getting ratings.");
                }
            }

            @Override
            public void onFailure(Call<RateResponse> call, Throwable t) {
                showToast("Error getting ratings.");
            }
        });
    }

    private void updatePrice() {
        if (selectedOfferPrice != 0) {
            totalPrice = selectedOfferPrice;
        } else {
            totalPrice = selectedPrice;
        }
        totalPrice *= selectedQty;
        binding.tvDesertPrice.setText(String.format("RM%.2f", totalPrice));
    }


    private void goToMyCart() {

        if (selectedItemId != 0) {
            long updateData = databaseManager.updateRecordById(selectedItemId, prefManager.getUid(), selectedDid, selectedName, selectedDesc, selectedImage, customization, requiredOnDate, totalPrice, selectedQty);

            if (updateData != -1L) {
                updateCartCount();
                if (replaceFragmentListener != null) {
                    replaceFragmentListener.openCartFragment();
                }
            } else {
                showToast("Failed to update your Cart!");
            }
        } else {
            long insertData = databaseManager.insertOrUpdateRecord(prefManager.getUid(), selectedDid, selectedName, selectedDesc, selectedImage, customization, requiredOnDate, totalPrice, selectedQty);

            if (insertData != -1L) {
                updateCartCount();
                if (replaceFragmentListener != null) {
                    replaceFragmentListener.openCartFragment();
                }
            } else {
                showToast("Failed to update your Cart!");
            }
        }
    }

    private void getRewardPoints() {
        ApiCall.getApiService().getPoints(prefManager.getUid()).enqueue(new Callback<RewardResponse>() {
            @Override
            public void onResponse(Call<RewardResponse> call, Response<RewardResponse> response) {
                if (response.isSuccessful()) {
                    RewardResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        RewardData data = response1.getData();
                        if (data != null) {
                            Log.d("TAG", "onResponsePoint: " + data.getPoints());
                            if (!data.getPoints().isEmpty() && data.getPoints() != null) {
                                binding.tvPoints.setText(String.format("Points: %s", data.getPoints()));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RewardResponse> call, Throwable t) {
                t.getLocalizedMessage();
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void setBackPressListener() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.layoutCustomize.getVisibility() == View.VISIBLE) {
                    slideDown(binding.layoutCustomize);
                } else {
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    }
                }
            }
        });
    }

    @Override
    public void onVariationSelected() {
        binding.btnPriceCalculate.setVisibility(View.VISIBLE);
        binding.btnCheckoutProceed.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        replaceFragmentListener = null;
    }
}