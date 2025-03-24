package com.example.final_project_group5.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project_group5.R;
import com.example.final_project_group5.activity.MapActivity;
import com.example.final_project_group5.adapter.ProductAdapterHome;
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

    private RecyclerView rvBestSeller, rvPopular, rvComponent;
    private ProductAdapterHome bestSellerAdapter, popularAdapter, componentAdapter;
    private List<Product> bestSellerList, popularList, componentList;
    private MaterialButton btnViewMap;
    private String userId;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Lấy userId từ arguments
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }

        // Initialize RecyclerViews
        rvBestSeller = view.findViewById(R.id.rvBestSeller);
        rvPopular = view.findViewById(R.id.rvPopular);
        rvComponent = view.findViewById(R.id.rvComponent);

        // Set horizontal layout managers
        rvBestSeller.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvComponent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize lists and adapters
        bestSellerList = new ArrayList<>();
        popularList = new ArrayList<>();
        componentList = new ArrayList<>();

        bestSellerAdapter = new ProductAdapterHome(bestSellerList);
        popularAdapter = new ProductAdapterHome(popularList);
        componentAdapter = new ProductAdapterHome(componentList);

        rvBestSeller.setAdapter(bestSellerAdapter);
        rvPopular.setAdapter(popularAdapter);
        rvComponent.setAdapter(componentAdapter);

        // Set click listeners for each adapter
        bestSellerAdapter.setOnProductClickListener(product -> navigateToProductDetail(product));
        popularAdapter.setOnProductClickListener(product -> navigateToProductDetail(product));
        componentAdapter.setOnProductClickListener(product -> navigateToProductDetail(product));

        // Map button
        btnViewMap = view.findViewById(R.id.btnViewMap);
        btnViewMap.setOnClickListener(v -> openMap());

        // Fetch products
        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        ProductService productService = ProductRepo.getProductService();
        Call<List<Product>> call = productService.getAllProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> allProducts = response.body();

                    // Filter products for each section
                    bestSellerList.clear();
                    popularList.clear();
                    componentList.clear();

                    for (Product product : allProducts) {
                        // Best Seller: Stock < 50
                        if (product.getStock() < 50) {
                            bestSellerList.add(product);
                        }
                        // Popular: Price < 5,000,000
                        if (product.getOriginalPrice() < 5000000) {
                            popularList.add(product);
                        }
                        // Component: Category is "Component"
                        if ("Component".equals(product.getCategory())) {
                            componentList.add(product);
                        }
                    }

                    bestSellerAdapter.notifyDataSetChanged();
                    popularAdapter.notifyDataSetChanged();
                    componentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Handle error
                Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToProductDetail(Product product) {
        if (product.getId() == null) {
            Toast.makeText(getContext(), "Product ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        ProductDetailFragment productDetailFragment = ProductDetailFragment.newInstance(userId, product.getId());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout1, productDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openMap() {
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivity(intent);
    }
}