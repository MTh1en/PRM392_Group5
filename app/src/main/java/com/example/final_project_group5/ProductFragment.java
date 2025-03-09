package com.example.final_project_group5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProductFragment extends Fragment {

    private String categoryName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        if (getArguments() != null) {
            categoryName = getArguments().getString("category");
        }

        // Cập nhật tiêu đề của Toolbar
        TextView toolbarTitle = view.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(categoryName);

        // Xử lý nút Back
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack(); // Quay về CategoriesFragment
            }
        });

        return view;
    }
}
