package com.yourapp.desertcraft.adapter.customize;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.databinding.ItemOptionsBinding;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder> {
    private List<String> options;
    private OnItemClickListener mListener;
    private int selectedPosition = -1;
    private VariationAdapter.VariationSelectionListener selectionListener;

    public OptionAdapter(List<String> options, OnItemClickListener mListener,VariationAdapter.VariationSelectionListener selectionListener) {
        this.options = options;
        this.mListener = mListener;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemOptionsBinding binding = ItemOptionsBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(options.get(position), position);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public String getSelectedOption() {
        return selectedPosition != -1 ? options.get(selectedPosition) : null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOptionsBinding binding;

        public ViewHolder(ItemOptionsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String option, int position) {
            binding.radioButton.setText(option);
            binding.radioButton.setChecked(position == selectedPosition);
            updateTextColor(binding.radioButton, position == selectedPosition);

            binding.radioButton.setOnClickListener(v -> {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
                mListener.onButtonClick(option);
            });
        }

        private void updateTextColor(RadioButton radioButton, boolean isSelected) {
            radioButton.setTextColor(isSelected ? itemView.getResources().getColor(R.color.white) : itemView.getResources().getColor(R.color.coco));
        }
    }

    public interface OnItemClickListener {
        void onButtonClick(String option);
    }
}
