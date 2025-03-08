package com.example.final_project_group5.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project_group5.ProductFragment;
import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.Categories;
import java.util.HashMap;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Categories> categoriesList;
    private FragmentActivity activity; // Thêm biến activity để chuyển fragment

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

    public CategoriesAdapter(List<Categories> categoriesList, FragmentActivity activity) {
        this.categoriesList = categoriesList;
        this.activity = activity;
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

        // Xử lý khi nhấn vào danh mục
        holder.itemView.setOnClickListener(v -> openProductFragment(category.getName()));
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

    // Chuyển sang ProductFragment và truyền dữ liệu danh mục
    private void openProductFragment(String categoryName) {
        ProductFragment productFragment = new ProductFragment();

        Bundle bundle = new Bundle();
        bundle.putString("category", categoryName);
        productFragment.setArguments(bundle);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, productFragment)
                .addToBackStack(null)
                .commit();
    }
}
