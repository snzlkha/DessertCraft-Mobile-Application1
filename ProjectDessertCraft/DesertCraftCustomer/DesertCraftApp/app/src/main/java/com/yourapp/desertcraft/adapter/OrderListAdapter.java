package com.yourapp.desertcraft.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.databinding.LayoutOrderedItemListBinding;
import com.yourapp.desertcraft.model.OrderData;
import com.yourapp.desertcraft.utils.Constant;
import com.yourapp.desertcraft.utils.Val;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.DessertViewHolder> {
    private Context context;
    private List<OrderData> dessertList;
    private OnItemClickListener mListener;

    public OrderListAdapter(Context context, List<OrderData> dessertList, OnItemClickListener mListener) {
        this.context = context;
        this.dessertList = dessertList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public DessertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutOrderedItemListBinding binding = LayoutOrderedItemListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new DessertViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DessertViewHolder holder, int position) {
        OrderData item = dessertList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return dessertList.size();
    }

    public class DessertViewHolder extends RecyclerView.ViewHolder {
        private LayoutOrderedItemListBinding binding;

        public DessertViewHolder(@NonNull LayoutOrderedItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderData item) {

            binding.tvOrderId.setText(String.format("Order ID: %s", item.getId()));
            binding.tvDesertName.setText(item.getDesert_name());

            if (!item.getImage().isEmpty()) {
                Picasso.get().load(Val.IMG_URL + item.getImage()).into(binding.imgDesert);
            }

            binding.tvPrice.setText("Price: RM" + item.getPrice());
            binding.tvCustomizations.setText(Constant.cleanUpString(item.getCustomization()));
            binding.tvOrderQty.setText("Qty: " + item.getQuantity());

            if (item.getOrder_status() == 0) {
                binding.tvOrderStatus.setText(Val.status0);
                binding.imgOrderStatus.setImageResource(R.drawable.clock);
                binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(context, R.color.coco), PorterDuff.Mode.SRC_IN);
            } else if (item.getOrder_status() == 1) {
                binding.tvOrderStatus.setText(Val.status2);
                binding.imgOrderStatus.setImageResource(R.drawable.saving);
                binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.SRC_IN);
            } else if (item.getOrder_status() == 2) {
                binding.tvOrderStatus.setText(Val.status1);
                binding.imgOrderStatus.setImageResource(R.drawable.checked);
                binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(context, R.color.coco), PorterDuff.Mode.SRC_IN);
            } else if (item.getOrder_status() == 3) {
                binding.tvOrderStatus.setText(Val.status3);
                binding.imgOrderStatus.setImageResource(R.drawable.chef);
                binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(context, R.color.yellow), PorterDuff.Mode.SRC_IN);
            } else if (item.getOrder_status() == 4) {
                binding.tvOrderStatus.setText(Val.status4);
                binding.imgOrderStatus.setImageResource(R.drawable.pickup);
                binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(context, R.color.coco), PorterDuff.Mode.SRC_IN);
            } else if (item.getOrder_status() == 5) {
                binding.tvOrderStatus.setText(Val.status5);
                binding.imgOrderStatus.setImageResource(R.drawable.checked);
                binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.SRC_IN);
            } else if (item.getOrder_status() == 6) {
                binding.tvOrderStatus.setText(Val.status6);
                binding.imgOrderStatus.setImageResource(R.drawable.info_alt);
                binding.imgOrderStatus.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_light), PorterDuff.Mode.SRC_IN);
            }

            itemView.setOnClickListener(view -> mListener.onButtonClick(item));
        }
    }

    public interface OnItemClickListener {
        void onButtonClick(OrderData item);
    }
}
