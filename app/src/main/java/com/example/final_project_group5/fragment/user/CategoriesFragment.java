package com.example.final_project_group5.fragment.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.final_project_group5.R;
import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private GridLayout categoriesContainer;
    private List<String> categoryList;
    private List<Integer> categoryIconList;
    private String userId;
    public static CategoriesFragment newInstance(String userId) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }
        categoriesContainer = view.findViewById(R.id.categoriesContainer);
        Log.d("CartFragment", "onCreate - Received userId: " + userId);
        // Xử lý Toolbar
        ImageView btnBack = view.findViewById(R.id.btn_back);
        ImageView btnSearch = view.findViewById(R.id.btn_search);
        ImageView btnCart = view.findViewById(R.id.btn_cart);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnSearch.setOnClickListener(v -> {
            // Xử lý tìm kiếm (có thể mở một SearchFragment sau này)
        });
        Log.d("Category", "User ID: " + userId);
        btnCart.setOnClickListener(v -> {
            CartFragment cartFragment = CartFragment.newInstance(userId); // Sử dụng userId trực tiếp
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout1, cartFragment)
                    .addToBackStack(null)
                    .commit();
        });

        categoryList = new ArrayList<>();
        categoryIconList = new ArrayList<>();

        setHardcodedCategories();
        displayCategories();

        return view;
    }

    private void setHardcodedCategories() {
        categoryList.add("PC Gaming");
        categoryList.add("Laptop");
        categoryList.add("Component");
        categoryList.add("Screen");
        categoryList.add("Keyboard");
        categoryList.add("Mouse");

        categoryIconList.add(R.drawable.app_logo);
        categoryIconList.add(R.drawable.laptop);
        categoryIconList.add(R.drawable.processor);
        categoryIconList.add(R.drawable.monitor);
        categoryIconList.add(R.drawable.keyboard);
        categoryIconList.add(R.drawable.mouse);
    }

    private void displayCategories() {
        categoriesContainer.removeAllViews();

        for (int i = 0; i < categoryList.size(); i++) {
            final String categoryName = categoryList.get(i);
            int categoryIcon = categoryIconList.get(i);

            View categoryLayout = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_category, categoriesContainer, false);

            ImageView categoryImageView = categoryLayout.findViewById(R.id.categoryIcon);
            TextView categoryTextView = categoryLayout.findViewById(R.id.categoryTextView);

            categoryImageView.setImageResource(categoryIcon);
            categoryTextView.setText(categoryName);

            categoryLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductFragment productFragment = ProductFragment.newInstance(userId); // Sử dụng userId trực tiếp
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout1, productFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            categoryLayout.setLayoutParams(params);

            categoriesContainer.addView(categoryLayout);
        }
    }
}
