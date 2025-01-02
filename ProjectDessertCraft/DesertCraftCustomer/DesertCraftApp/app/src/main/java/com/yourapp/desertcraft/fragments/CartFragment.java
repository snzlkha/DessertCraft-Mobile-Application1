package com.yourapp.desertcraft.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yourapp.desertcraft.adapter.CartAdapter;
import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.FragmentCartBinding;
import com.yourapp.desertcraft.model.CartItem;
import com.yourapp.desertcraft.model.RewardData;
import com.yourapp.desertcraft.response.RewardResponse;
import com.yourapp.desertcraft.storage.DatabaseHelper;
import com.yourapp.desertcraft.storage.DatabaseManager;
import com.yourapp.desertcraft.utils.ReplaceFragmentListener;
import com.yourapp.desertcraft.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartFragment extends Fragment {
    private FragmentCartBinding binding;

    DatabaseManager databaseManager;
    PrefManager prefManager;
    CartAdapter cartAdapter;
    private int cartCount = 0;
    private int selectedItemId;
    int pointsAvailable = 0;
    private List<CartItem> itemList = new ArrayList<>();

    private ReplaceFragmentListener replaceFragmentListener;

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
        binding = FragmentCartBinding.inflate(inflater, container, false);

        prefManager = new PrefManager(requireContext());
        databaseManager = new DatabaseManager(requireContext());
        databaseManager.open();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartAdapter = new CartAdapter(itemList, new CartAdapter.OnItemClickListener() {
            @Override
            public void onRadioClick(CartItem item) {
                selectedItemId = item.getId();
            }

            @Override
            public void onDeleteClick(CartItem item) {
                int position = itemList.indexOf(item);
                deleteItem(item.getId(), position);
            }

            @Override
            public void onEditClick(CartItem item) {
                if (replaceFragmentListener != null) {
                    replaceFragmentListener.onEditItemClick(item.getId(), item.getDid());
                }
            }

            @Override
            public void onMinusClick(CartItem item) {
                int selectedQty = item.getQuantity();
                double selectedPrice = item.getPrice();
                double rate = selectedPrice / selectedQty;
                if (selectedQty > 1) {
                    selectedQty = selectedQty - 1;
                    double price = getUpdatedPrice(selectedQty, rate);
                    int result = databaseManager.updateRecord(item.getId(), price, selectedQty);
                    if (result > 0) {
                        refreshItemList();
                    }
                } else {
                    onDeleteClick(item);
                }
            }

            @Override
            public void onPlusClick(CartItem item) {
                int selectedQty = item.getQuantity();
                double selectedPrice = item.getPrice();
                double rate = selectedPrice / selectedQty;
                if (selectedQty > 0) {
                    selectedQty = selectedQty + 1;
                    double price = getUpdatedPrice(selectedQty, rate);
                    int result = databaseManager.updateRecord(item.getId(), price, selectedQty);
                    if (result > 0) {
                        refreshItemList();
                    }
                }
            }
        });

        binding.rvCartItem.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCartItem.setHasFixedSize(true);
        binding.rvCartItem.setAdapter(cartAdapter);

        getRewardPoints();

        refreshItemList();

        if (!itemList.isEmpty()) {
            cartAdapter.setSelectedPosition(0);
        }

        binding.btnCheckoutProceed.setOnClickListener(view1 -> {
            previewOrder();
        });

        binding.btnBack.setOnClickListener(view1 -> getParentFragmentManager().popBackStack());
    }

    private void getRewardPoints() {
        ApiCall.getApiService().getPoints(prefManager.getUid()).enqueue(new Callback<RewardResponse>() {
            @Override
            public void onResponse(Call<RewardResponse> call, Response<RewardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RewardResponse response1 = response.body();
                    if (response1.isSuccess()) {
                        RewardData data = response1.getData();
                        if (data != null) {
                            if (!data.getPoints().isEmpty() && data.getPoints() != null) {
                                pointsAvailable = Integer.parseInt(data.getPoints());
                            }
                        }
                    } else showToast("No Points!");
                } else showToast("Failed to get Points!");
            }

            @Override
            public void onFailure(Call<RewardResponse> call, Throwable t) {
                t.getLocalizedMessage();
                showToast("Failed to get Points!");
            }
        });
    }

    private void refreshItemList() {
        itemList.clear();
        Cursor cursor = databaseManager.getAllData();

        if (cursor != null) {
            cartCount = cursor.getCount();
            updateCartCount();

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

            while (cursor.moveToNext()) {
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
                itemList.add(item);
            }
            cursor.close();
        }

        cartAdapter.notifyDataSetChanged();
    }

    private double getUpdatedPrice(int qty, double price) {
        return price * qty;
    }

    public void deleteItem(long itemId, int position) {
        int result = databaseManager.deleteRecord(itemId);
        if (result > 0) {
            itemList.remove(position);
            cartAdapter.notifyItemRemoved(position);
            cartAdapter.notifyItemRangeChanged(position, itemList.size());
            showToast("Removed from Cart.");
            cartCount = itemList.size();
            updateCartCount();
        } else {
            showToast("Failed to delete item!");
        }
    }

    private void updateCartCount() {
        if (cartCount > 0) {
            binding.tvCartSize.setVisibility(View.VISIBLE);
            binding.tvCartSize.setText(String.valueOf(cartCount));
        } else {
            binding.tvCartSize.setVisibility(View.GONE);
        }
    }

    private void previewOrder() {
        if (replaceFragmentListener != null) {
            replaceFragmentListener.onCartItemClick(selectedItemId, pointsAvailable);
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