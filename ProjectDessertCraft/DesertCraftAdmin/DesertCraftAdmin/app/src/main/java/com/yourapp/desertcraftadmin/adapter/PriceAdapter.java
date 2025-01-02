package com.yourapp.desertcraftadmin.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yourapp.desertcraftadmin.databinding.LayoutListVariationPriceBinding;
import com.yourapp.desertcraftadmin.model.PriceCombination;
import com.yourapp.desertcraftadmin.productlist.Price;
import com.yourapp.desertcraftadmin.utils.Constant;

import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.CombinationViewHolder> {
    private List<PriceCombination> combinationList;
    private List<Price> priceList;

    public PriceAdapter(List<PriceCombination> combinationList, List<Price> priceList) {
        this.combinationList = combinationList;
        this.priceList = priceList;
    }

    @NonNull
    @Override
    public CombinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutListVariationPriceBinding binding = LayoutListVariationPriceBinding.inflate(inflater, parent, false);
        return new CombinationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CombinationViewHolder holder, int position) {
        PriceCombination combination = combinationList.get(position);

        // Find matching price
        for (Price price : priceList) {
            if (combination.matches(price)) {
                combination.setPrice(price.getPrice());
                combination.setOfferPrice(price.getOfferPrice());
                combination.setStock(price.getStock());
                break;
            }
        }

        holder.bind(combination);
    }

    @Override
    public int getItemCount() {
        return combinationList.size();
    }

    public List<PriceCombination> getCombinationList() {
        return combinationList;
    }

    static class CombinationViewHolder extends RecyclerView.ViewHolder {
        private final LayoutListVariationPriceBinding binding;

        public CombinationViewHolder(@NonNull LayoutListVariationPriceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PriceCombination combination) {
            binding.tvCombination.setText(Constant.cleanOptions(combination.toString()));

            // Update the EditText fields with existing values if present
            binding.etPrice.setText(combination.getPrice());
            binding.etOfferPrice.setText(combination.getOfferPrice());
            binding.etStock.setText(combination.getStock());

            // Add TextWatchers to update the model when text changes
            binding.etPrice.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    combination.setPrice(s.toString());
                }
            });
            binding.etOfferPrice.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    combination.setOfferPrice(s.toString());
                }
            });
            binding.etStock.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    combination.setStock(s.toString());
                }
            });
        }
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}
