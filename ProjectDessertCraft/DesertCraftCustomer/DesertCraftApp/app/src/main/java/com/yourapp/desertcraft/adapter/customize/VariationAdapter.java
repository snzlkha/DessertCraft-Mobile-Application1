package com.yourapp.desertcraft.adapter.customize;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yourapp.desertcraft.databinding.ItemVariationsBinding;
import com.yourapp.desertcraft.productlist.Variation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariationAdapter extends RecyclerView.Adapter<VariationAdapter.ViewHolder> {
    private List<Variation> variations;
    private List<OptionAdapter> optionAdapters;
    private VariationSelectionListener selectionListener;

    public VariationAdapter(List<Variation> variations) {
        this.variations = variations;
        this.optionAdapters = new ArrayList<>(variations.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemVariationsBinding binding = ItemVariationsBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(variations.get(position), position);
    }

    @Override
    public int getItemCount() {
        return variations.size();
    }

    public List<String> getSelectedOptions() {
        List<String> selectedOptions = new ArrayList<>();
        for (OptionAdapter optionAdapter : optionAdapters) {
            String selectedOption = optionAdapter.getSelectedOption();
            if (selectedOption != null) {
                selectedOptions.add(selectedOption);
            }
        }
        return selectedOptions;
    }

    public void setSelectionListener(VariationSelectionListener listener) {
        this.selectionListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemVariationsBinding binding;

        public ViewHolder(ItemVariationsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Variation variation, int position) {
            binding.tvVariation.setText(variation.getVariation());
            binding.innerRecyclerView.setLayoutManager(new LinearLayoutManager(binding.innerRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<String> options = Arrays.asList(variation.getOption().split(", "));
            OptionAdapter optionAdapter = new OptionAdapter(options, option -> {
                selectionListener.onVariationSelected();
            },selectionListener);
            binding.innerRecyclerView.setAdapter(optionAdapter);

            if (optionAdapters.size() <= position) {
                optionAdapters.add(position, optionAdapter);
            } else {
                optionAdapters.set(position, optionAdapter);
            }

        }
    }

    public interface VariationSelectionListener {
        void onVariationSelected();
    }
}


