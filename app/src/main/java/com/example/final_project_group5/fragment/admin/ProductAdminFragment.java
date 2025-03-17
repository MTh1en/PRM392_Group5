package com.example.final_project_group5.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project_group5.R;
import com.example.final_project_group5.adapter.ProductAdapter;
import com.example.final_project_group5.api.ProductService;
import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.repository.ProductRepo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdminFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FloatingActionButton fabAddProduct;

    public ProductAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_admin, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());

        fetchAllProducts();

        return view;
    }

    private void fetchAllProducts() {
        ProductService productService = ProductRepo.getProductService();
        Call<List<Product>> call = productService.getAllProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddProductDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle("Add New Product");

        // Ánh xạ các trường
        TextInputEditText etProductName = dialogView.findViewById(R.id.etProductName);
        TextInputEditText etProductDescription = dialogView.findViewById(R.id.etProductDescription);
        TextInputEditText etOriginalPrice = dialogView.findViewById(R.id.etOriginalPrice);
        TextInputEditText etDiscountPercentage = dialogView.findViewById(R.id.etDiscountPercentage);
        TextInputEditText etBrand = dialogView.findViewById(R.id.etBrand);
        TextInputEditText etStock = dialogView.findViewById(R.id.etStock);
        TextInputEditText etImage = dialogView.findViewById(R.id.etImage);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        // Danh sách các danh mục
        List<String> categories = Arrays.asList("PC Gaming", "Laptop", "Component", "Screen", "Keyboard", "Mouse");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Lấy dữ liệu từ các trường
            String name = etProductName.getText().toString().trim();
            String description = etProductDescription.getText().toString().trim();
            String originalPriceStr = etOriginalPrice.getText().toString().trim();
            String discountPercentageStr = etDiscountPercentage.getText().toString().trim();
            String brand = etBrand.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String image = etImage.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString(); // Lấy giá trị từ Spinner

            // Kiểm tra dữ liệu bắt buộc
            if (name.isEmpty() || originalPriceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển đổi dữ liệu
            double originalPrice = Double.parseDouble(originalPriceStr);
            float discountPercentage = discountPercentageStr.isEmpty() ? 0 : Float.parseFloat(discountPercentageStr);
            double discountedPrice = originalPrice * (1 - discountPercentage / 100); // Tính Discounted Price
            int stock = stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr);

            // Rating Count và Average Rating mặc định là 0
            int ratingCount = 0;
            float averageRating = 0;

            // Tạo sản phẩm mới
            Product newProduct = new Product(null, name, description, originalPrice, discountedPrice,
                    discountPercentage, ratingCount, averageRating, brand, stock, image, category);

            // Gửi lên API
            addProduct(newProduct);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addProduct(Product product) {
        ProductService productService = ProductRepo.getProductService();
        Call<Product> call = productService.createProduct(product);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.add(response.body());
                    productAdapter.notifyItemInserted(productList.size() - 1);
                    recyclerView.scrollToPosition(productList.size() - 1);
                    Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}