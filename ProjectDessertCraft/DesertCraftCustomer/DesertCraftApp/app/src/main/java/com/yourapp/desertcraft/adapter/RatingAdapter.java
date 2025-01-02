package com.yourapp.desertcraft.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yourapp.desertcraft.databinding.LayoutRatingItemBinding;
import com.yourapp.desertcraft.model.RateData;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RateViewHolder> {
    private List<RateData> ratings;

    public RatingAdapter(List<RateData> ratings) {
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRatingItemBinding binding = LayoutRatingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        RateData data = ratings.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public class RateViewHolder extends RecyclerView.ViewHolder {
        private LayoutRatingItemBinding binding;

        public RateViewHolder(@NonNull LayoutRatingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RateData data) {
            binding.tvComments.setText(data.getComments());
            if (!data.getRating().isEmpty()) {
                float rating = Float.parseFloat(data.getRating());
                binding.ratingBar.setRating(rating);
            }
        }
    }
}
