package com.example.final_project_group5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project_group5.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<String> categoryList;
    private List<Integer> categoryIconList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        categoryList = new ArrayList<>();
        categoryIconList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, categoryIconList);
        recyclerView.setAdapter(categoryAdapter);

        setHardcodedCategories();

        // Xử lý sự kiện click vào item
        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String selectedCategory = categoryList.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("category", selectedCategory);


                ProductFragment productFragment = new ProductFragment();
                productFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, productFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

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

        categoryAdapter.notifyDataSetChanged();
    }
}