package com.example.final_project_group5.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project_group5.R;
import com.example.final_project_group5.adapter.ProductAdapter;
import com.example.final_project_group5.api.ProductService;
import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.repository.ProductRepo;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView rvFeaturedProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private MaterialButton btnViewMap;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ RecyclerView
        rvFeaturedProducts = view.findViewById(R.id.rvFeaturedProducts);
        rvFeaturedProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Ánh xạ nút View Map
        btnViewMap = view.findViewById(R.id.btnViewMap);
        btnViewMap.setOnClickListener(v -> openMap());

        // Khởi tạo danh sách sản phẩm và adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        rvFeaturedProducts.setAdapter(productAdapter);

        // Lấy dữ liệu sản phẩm
        fetchFeaturedProducts();

        return view;
    }

    private void fetchFeaturedProducts() {
        ProductService productService = ProductRepo.getProductService();
        Call<List<Product>> call = productService.getAllProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void openMap() {
        String uri = "geo:10.7769,106.7009?q=Ho+Chi+Minh+City";
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://maps.google.com/?q=Ho+Chi+Minh+City"));
            startActivity(browserIntent);
        }
    }
}