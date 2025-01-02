package com.yourapp.desertcraftadmin.productlist;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yourapp.desertcraftadmin.R;
import com.yourapp.desertcraftadmin.databinding.LayoutListProductsBinding;
import com.yourapp.desertcraftadmin.model.OrderData;
import com.yourapp.desertcraftadmin.utils.Val;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {
    private List<Product> productList;
    private List<Product> filteredList;

    OnButtonClickListener mListener;

    public ProductAdapter(List<Product> rarList, OnButtonClickListener listener) {
        this.productList = rarList;
        filteredList = productList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutListProductsBinding binding = LayoutListProductsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = filteredList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        LayoutListProductsBinding binding;

        public ProductViewHolder(@NonNull LayoutListProductsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product) {
            binding.tvProductId.setText("Product ID: " + product.getPid());
            binding.tvDesertName.setText(product.getPName());

            // Assuming the first element for simplicity, you might need to adjust this according to your logic
            if (!product.getParsedVariation().isEmpty()) {
                binding.tvDesertFlavour.setText(product.getParsedVariation().get(0).getOption());
            }

            binding.tvPrice.setText(product.getBase_price());
            if (!product.getOffer_price().isEmpty()) {
                binding.tvOfferPrice.setText(product.getOffer_price());
                binding.tvPrice.setPaintFlags(binding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            if (!product.getParsedPrice().isEmpty()) {
//                binding.tvStockQty.setText("Stock: " + product.getParsedPrice().get(0).getStock());
            }

            binding.tvTotalSold.setText("Sold: " + product.getTotalSold());


            Glide.with(itemView.getContext())
                    .load(Val.IMG_URL + product.getPImage())
                    .placeholder(R.drawable.placeholder)
                    .into(binding.imgDesert);

            binding.btnEditDesert.setOnClickListener(view -> mListener.onEditClick(product));
            binding.btnDeleteDesert.setOnClickListener(view -> mListener.onDeleteClick(product));
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.values = productList;
                } else {
                    List<Product> filteredItems = new ArrayList<>();
                    for (Product data : productList) {
                        if (data.getPid().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredItems.add(data);
                        }else if (data.getPName().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
                        if (value instanceof Product) {
                            filteredList.add((Product) value);
                        }
                    }
                } else {
                    filteredList = Collections.emptyList();
                }
                notifyDataSetChanged();
            }
        };
    }

    public void filterById(String category) {
        filteredList = new ArrayList<>();
        if (category == null || category.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            for (Product data : productList) {
                try {
                    if (data.getPid().equals(category)) {
                        filteredList.add(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDataSetChanged();
    }


    public void clearFilter() {
        filteredList = new ArrayList<>(productList);
        notifyDataSetChanged();
    }

    public interface OnButtonClickListener {
        void onEditClick(Product product);

        void onDeleteClick(Product product);
    }
}
