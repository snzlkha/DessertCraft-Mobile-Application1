package com.yourapp.desertcraftadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yourapp.desertcraftadmin.R;
import com.yourapp.desertcraftadmin.databinding.LayoutOrderedItemListBinding;
import com.yourapp.desertcraftadmin.model.OrderData;
import com.yourapp.desertcraftadmin.utils.Constant;
import com.yourapp.desertcraftadmin.utils.Val;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.DessertViewHolder> implements Filterable {
    private Context context;
    private List<OrderData> dessertList;
    List<OrderData> filteredList;
    private OnItemClickListener mListener;

    public OrderListAdapter(Context context, List<OrderData> rawList, OnItemClickListener mListener) {
        this.context = context;
        this.dessertList = rawList;
        filteredList = dessertList;
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
        OrderData item = filteredList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class DessertViewHolder extends RecyclerView.ViewHolder {
        private LayoutOrderedItemListBinding binding;

        public DessertViewHolder(@NonNull LayoutOrderedItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderData item) {

            binding.tvDesertName.setText(item.getDesert_name());
            if (!item.getImage().isEmpty()) {
                Picasso.get().load(Val.IMG_URL + item.getImage()).into(binding.imgDesert);
            }
            binding.tvPrice.setText("Price: RM" + item.getPrice());
            binding.tvCreatedOn.setText("Created on: " + item.getCreated_on());
            binding.tvCustomizations.setText(Constant.cleanUpString(item.getCustomization()));
            binding.tvOrderId.setText("Order ID: " + item.getId());
            binding.tvOrderQty.setText("Qty: " + item.getQuantity());

            binding.imgOpenItemDetails.setOnClickListener(view -> mListener.onButtonClick(item));

            itemView.setOnClickListener(view -> mListener.onButtonClick(item));
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.values = dessertList;
                } else {
                    List<OrderData> filteredItems = new ArrayList<>();
                    for (OrderData data : dessertList) {
                        if (data.getId().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredItems.add(data);
                        }
                    }
                    filterResults.values = filteredItems;
                }
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = new ArrayList<>();
                List<?> values = results != null ? (List<?>) results.values : null;
                if (values != null) {
                    for (Object value : values) {
                        if (value instanceof OrderData) {
                            filteredList.add((OrderData) value);
                        }
                    }
                } else {
                    filteredList = Collections.emptyList();
                }
                notifyDataSetChanged();
            }
        };
    }

    public void filterList(String orderDate) {
        filteredList = new ArrayList<>();
        if (orderDate == null || orderDate.isEmpty()) {
            filteredList.addAll(dessertList);
        } else {
            for (OrderData data : dessertList) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate;
                try {
                    Date date = inputFormat.parse(data.getCreated_on());
                    if (date != null) {
                        formattedDate = outputFormat.format(date).toUpperCase();
                        if (formattedDate.equals(orderDate)) {
                            filteredList.add(data);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDataSetChanged();
    }


    public void clearFilter() {
        filteredList = new ArrayList<>(dessertList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onButtonClick(OrderData item);
    }
}
