package com.yourapp.desertcraft.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yourapp.desertcraft.adapter.OrderListAdapter;
import com.yourapp.desertcraft.api.ApiCall;
import com.yourapp.desertcraft.databinding.FragmentOrderBinding;
import com.yourapp.desertcraft.model.OrderData;
import com.yourapp.desertcraft.response.OrderResponse;
import com.yourapp.desertcraft.utils.PrefManager;
import com.yourapp.desertcraft.utils.ReplaceFragmentListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {
    private FragmentOrderBinding binding;
    PrefManager prefManager;
    OrderListAdapter orderListAdapter;
    private ArrayList<OrderData> dataList = new ArrayList<>();
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
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        prefManager = new PrefManager(requireContext());
        setBackPressListener();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOrders.setHasFixedSize(true);

        orderListAdapter = new OrderListAdapter(requireContext(), dataList, item -> {
            openTrackingFragment(item.getId());
        });
        binding.rvOrders.setAdapter(orderListAdapter);

        getOrders();

        binding.swipeRefresh.setOnRefreshListener(this::getOrders);
        binding.btnBack.setOnClickListener(view1 -> {
            if (replaceFragmentListener!=null){
                replaceFragmentListener.goBackToHome();
            }
        });
    }

    private void getOrders() {
        ApiCall.getApiService().getOrderList(prefManager.getUid()).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse response1 = response.body();
                    if (response1 != null && response1.isSuccess()) {
                        dataList.clear();
                        dataList.addAll(response1.getData());
                        orderListAdapter.notifyDataSetChanged();
                    } else {
                        if (response1 != null) {
                            showToast(response1.getMessage());
                        } else {
                            showToast("Error while fetching orders");
                        }
                    }
                } else {
                    showToast("Error while fetching orders");
                }
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showToast("Some error occurred!");
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void openTrackingFragment(int id) {
        if (replaceFragmentListener != null) {
            replaceFragmentListener.openTrackingFragment(id);
        }
    }

    private void setBackPressListener() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (replaceFragmentListener!=null){
                    replaceFragmentListener.goBackToHome();
                }
            }
        });
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