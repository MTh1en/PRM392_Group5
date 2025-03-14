package com.example.final_project_group5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private GridLayout categoriesContainer; // Thay LinearLayout bằng GridLayout
    private List<String> categoryList;
    private List<Integer> categoryIconList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        categoriesContainer = view.findViewById(R.id.categoriesContainer);

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
                    Bundle bundle = new Bundle();
                    bundle.putString("category", categoryName);

                    ProductFragment productFragment = new ProductFragment();
                    productFragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, productFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });


            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Mỗi item chiếm 1 cột với trọng số đều
            categoryLayout.setLayoutParams(params);

            categoriesContainer.addView(categoryLayout);
        }
    }
}