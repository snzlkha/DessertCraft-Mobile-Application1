package com.yourapp.desertcraftadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yourapp.desertcraftadmin.R;
import com.yourapp.desertcraftadmin.model.CatData;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter<CatData> {
    private final List<CatData> catListFull;
    private List<CatData> catList;

    public CategoryAdapter(@NonNull Context context, @NonNull List<CatData> catList) {
        super(context, 0, catList);
        this.catList = new ArrayList<>(catList);
        catListFull = new ArrayList<>(catList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_category, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tv_category);

        CatData data = getItem(position);
        if (data != null) {
            tvName.setText(position + 1 + ".  " + data.getC_name());
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return trainFilter;
    }

    private final Filter trainFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<CatData> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(catListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CatData item : catListFull) {
                    if (item.getC_name().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            catList.clear();
            catList.addAll((List<CatData>) results.values);
            notifyDataSetChanged();
        }
    };
}
