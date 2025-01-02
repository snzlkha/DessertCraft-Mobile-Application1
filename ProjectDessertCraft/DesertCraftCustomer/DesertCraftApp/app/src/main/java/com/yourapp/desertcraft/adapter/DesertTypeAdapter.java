package com.yourapp.desertcraft.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.databinding.LayoutListDesertTypeBinding;
import com.yourapp.desertcraft.model.CatData;

import java.util.List;

public class DesertTypeAdapter extends RecyclerView.Adapter<DesertTypeAdapter.ViewHolder> {
    private final List<CatData> typeList;
    private final ItemClickListener clickListener;
    private int selectedPosition = -1;

    public DesertTypeAdapter(List<CatData> typeList, ItemClickListener clickListener) {
        this.typeList = typeList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutListDesertTypeBinding binding = LayoutListDesertTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CatData journalItem = typeList.get(position);
        holder.bind(journalItem, position);
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutListDesertTypeBinding binding;

        public ViewHolder(@NonNull LayoutListDesertTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CatData type, int position) {

            binding.radioButton.setText(type.getC_name());
            binding.radioButton.setChecked(position == selectedPosition);
            if (position == selectedPosition) {
                binding.radioButton.setTextColor(itemView.getResources().getColor(R.color.white));
            } else {
                binding.radioButton.setTextColor(itemView.getResources().getColor(R.color.coco));
            }

            binding.radioButton.setOnClickListener(v -> {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
                clickListener.onClick(type);
            });
        }
    }

    public void uncheckAllRadioButtons() {
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onClick(CatData type);
    }
}


