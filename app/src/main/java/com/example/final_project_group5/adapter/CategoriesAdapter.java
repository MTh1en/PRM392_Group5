package com.example.final_project_group5.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.Categories;
import java.util.HashMap;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Categories> categoriesList;
    private OnCategoryClickListener listener;

    // HashMap ánh xạ ID danh mục với icon tương ứng
    private static final HashMap<Integer, Integer> categoryIcons = new HashMap<>();
    static {
        categoryIcons.put(1, R.drawable.app_logo);
        categoryIcons.put(2, R.drawable.laptop);
        categoryIcons.put(3, R.drawable.processor);
        categoryIcons.put(4, R.drawable.monitor);
        categoryIcons.put(5, R.drawable.keyboard);
        categoryIcons.put(6, R.drawable.mouse);

    }

    public CategoriesAdapter(List<Categories> categoriesList, OnCategoryClickListener listener) {
        this.categoriesList = categoriesList;
        this.listener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Categories category);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categories category = categoriesList.get(position);
        holder.categoryName.setText(category.getName());

        // Lấy icon theo ID danh mục
        Integer iconResId = categoryIcons.get(category.getId());
        if (iconResId != null) {
            holder.categoryIcon.setImageResource(iconResId);
        } else {
            holder.categoryIcon.setImageResource(R.drawable.app_logo); // Icon mặc định nếu không tìm thấy
        }

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
        }
    }
}
