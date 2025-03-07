package com.example.final_project_group5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project_group5.adapter.CategoriesAdapter;
import com.example.final_project_group5.entity.Categories;
import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoriesAdapter adapter;
    private List<Categories> categoriesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        categoriesList = new ArrayList<>();
        categoriesList.add(new Categories(1, "PC Gaming"));
        categoriesList.add(new Categories(2, "Laptop"));
        categoriesList.add(new Categories(3, "Linh kiện PC"));
        categoriesList.add(new Categories(4, "Màn hình"));
        categoriesList.add(new Categories(5, "Bàn phím"));
        categoriesList.add(new Categories(6, "Chuột"));


        adapter = new CategoriesAdapter(categoriesList, category -> {
            // Xử lý khi click vào danh mục (Chuyển sang danh sách sản phẩm)
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
