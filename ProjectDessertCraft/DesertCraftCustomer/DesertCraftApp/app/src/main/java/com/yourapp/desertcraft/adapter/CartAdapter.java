package com.yourapp.desertcraft.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yourapp.desertcraft.databinding.LayoutListCartItemBinding;
import com.yourapp.desertcraft.model.CartItem;
import com.yourapp.desertcraft.utils.Val;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.DessertViewHolder> {
    private List<CartItem> dessertList;
    private OnItemClickListener mListener;
    private int selectedPosition = 0;

    public CartAdapter(List<CartItem> dessertList, OnItemClickListener mListener) {
        this.dessertList = dessertList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public DessertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutListCartItemBinding binding = LayoutListCartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DessertViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DessertViewHolder holder, int position) {
        CartItem item = dessertList.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return dessertList.size();
    }

    public class DessertViewHolder extends RecyclerView.ViewHolder {
        private final LayoutListCartItemBinding binding;

        public DessertViewHolder(@NonNull LayoutListCartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItem item, int position) {
            binding.radioButton.setChecked(position == selectedPosition);
            binding.radioButton.setOnClickListener(v -> {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
                mListener.onRadioClick(item);
            });

            binding.tvDesertName.setText(item.getName());

            if (!item.getImage().isEmpty()) {
                Picasso.get().load(Val.IMG_URL + item.getImage()).into(binding.imgPlaceholder);
            }

            if (!item.getCustomization().isEmpty()) {
                binding.tvCustomizations.setText(item.getCustomization());
            }

            binding.tvPrice.setText("Price: RM"+item.getPrice());

            binding.tvQty.setText(String.valueOf(item.getQuantity()));

            binding.imgDelete.setOnClickListener(view -> mListener.onDeleteClick(item));
            binding.imgEdit.setOnClickListener(view -> mListener.onEditClick(item));
            binding.tvQtyMinus.setOnClickListener(view -> mListener.onMinusClick(item));
            binding.tvQtyPlus.setOnClickListener(view -> mListener.onPlusClick(item));
        }
    }

    public void setSelectedPosition(int position) {
        int previousSelectedPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousSelectedPosition);
        notifyItemChanged(selectedPosition);
        mListener.onRadioClick(dessertList.get(position));
    }

    public interface OnItemClickListener {
        void onRadioClick(CartItem item);

        void onDeleteClick(CartItem item);
        void onEditClick(CartItem item);

        void onMinusClick(CartItem item);

        void onPlusClick(CartItem item);
    }

}
