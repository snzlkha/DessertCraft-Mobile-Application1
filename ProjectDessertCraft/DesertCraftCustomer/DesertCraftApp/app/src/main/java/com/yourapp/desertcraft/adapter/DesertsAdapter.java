package com.yourapp.desertcraft.adapter;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.databinding.LayoutListDesertsBinding;
import com.yourapp.desertcraft.productlist.Product;
import com.yourapp.desertcraft.storage.DatabaseManager;
import com.yourapp.desertcraft.utils.Val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DesertsAdapter extends RecyclerView.Adapter<DesertsAdapter.ViewHolder> implements Filterable {
    private final List<Product> desertList;
    List<Product> filteredList;

    DatabaseManager databaseManager;
    private OnItemClickListener mListener;

    public DesertsAdapter(List<Product> rawList, DatabaseManager manager, OnItemClickListener listener) {
        this.desertList = rawList;
        this.mListener = listener;
        filteredList = desertList;
        this.databaseManager = manager;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutListDesertsBinding binding = LayoutListDesertsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = filteredList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutListDesertsBinding binding;

        public ViewHolder(@NonNull LayoutListDesertsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product data) {

            if (!data.getPImage().isEmpty())
                Picasso.get().load(Val.IMG_URL + data.getPImage()).into(binding.imgDesert);
            binding.tvDesertName.setText(data.getPName());
            binding.tvDesertPrice.setText("RM"+data.getBase_price());
            if (!data.getOffer_price().isEmpty()) {
                binding.tvDesertOfferPrice.setText("RM"+data.getOffer_price());
                binding.tvDesertOfferPrice.setVisibility(View.VISIBLE);
                binding.tvDesertPrice.setPaintFlags(binding.tvDesertPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                binding.tvDesertOfferPrice.setVisibility(View.INVISIBLE);
            }

            if (data.getParsedVariation() != null && !data.getParsedVariation().isEmpty()) {
                binding.btnAddToCart.setText("+ Customize");
            } else {
                binding.btnAddToCart.setText("+ Add to Cart");
            }

            binding.btnAddToCart.setOnClickListener(view -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onButtonClick(data);
                    }
                }
            });

            if (!data.getRating().isEmpty()) {
                float rating = Float.parseFloat(data.getRating());
                binding.ratingBar.setRating(rating);
            }

            boolean isExistInFavourite = databaseManager.isPostInFavorites(Integer.parseInt(data.getPid()));
            if (isExistInFavourite) {
                binding.imgLike.setImageResource(R.drawable.heart_filled);
            } else {
                binding.imgLike.setImageResource(R.drawable.heart_empty);
            }

            binding.imgLike.setOnClickListener(view -> {
                boolean isLiked = databaseManager.isPostInFavorites(Integer.parseInt(data.getPid()));
                if (isLiked) {
                    long setUnLike = databaseManager.setUnLike(Integer.parseInt(data.getPid()));
                    if (setUnLike != -1L) {
                        binding.imgLike.setImageResource(R.drawable.heart_empty);
                    }
                } else {
                    long setLike = databaseManager.setLike(Integer.parseInt(data.getPid()));
                    if (setLike != -1L) {
                        binding.imgLike.setImageResource(R.drawable.heart_filled);
                    }
                }
            });

            itemView.setOnClickListener(v -> mListener.onItemClick(data));
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.values = desertList;
                } else {
                    List<Product> filteredItems = new ArrayList<>();
                    for (Product data : desertList) {
                        if (data.getPName().toLowerCase().contains(constraint.toString().toLowerCase())) {
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

    public void filterList(String categoryId) {
        filteredList = new ArrayList<>();
        if (categoryId == null || categoryId.isEmpty()) {
            filteredList.addAll(desertList);
        } else {
            for (Product data : desertList) {
                if (data.getCid().equals(categoryId)) {
                    filteredList.add(data);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void clearFilter() {
        filteredList = new ArrayList<>(desertList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Product data);

        void onButtonClick(Product data);
    }

}


