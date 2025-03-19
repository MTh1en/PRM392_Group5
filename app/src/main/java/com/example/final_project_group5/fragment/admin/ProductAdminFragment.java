package com.example.final_project_group5.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
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
    private SearchView searchView;
    private Spinner spinnerSearchCriteria;

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
        productAdapter.setOnProductClickListener(this::showUpdateProductDialog);
        recyclerView.setAdapter(productAdapter);

        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());

        searchView = view.findViewById(R.id.searchView);
        spinnerSearchCriteria = view.findViewById(R.id.spinnerSearchCriteria);
        setupSearchCriteriaSpinner();
        setupSearchView();

        fetchAllProducts();

        return view;
    }

    private void setupSearchCriteriaSpinner() {
        List<String> criteria = Arrays.asList("Name", "Brand", "Category");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, criteria);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearchCriteria.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    fetchAllProducts();
                }
                return true;
            }
        });
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

    private void searchProducts(String query) {
        ProductService productService = ProductRepo.getProductService();
        Call<List<Product>> call;
        String selectedCriteria = spinnerSearchCriteria.getSelectedItem().toString();

        switch (selectedCriteria) {
            case "Name":
                call = productService.searchProductsByName(query);
                break;
            case "Brand":
                call = productService.searchProductsByBrand(query);
                break;
            case "Category":
                call = productService.searchProductsByCategory(query);
                break;
            default:
                call = productService.getAllProducts(); // Trường hợp không chọn tiêu chí
        }

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                    if (productList.isEmpty()) {
                        Toast.makeText(getContext(), "No products found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No products found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Search error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddProductDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle("Add New Product");

        TextInputEditText etProductName = dialogView.findViewById(R.id.etProductName);
        TextInputEditText etProductDescription = dialogView.findViewById(R.id.etProductDescription);
        TextInputEditText etOriginalPrice = dialogView.findViewById(R.id.etOriginalPrice);
        TextInputEditText etDiscountPercentage = dialogView.findViewById(R.id.etDiscountPercentage);
        TextInputEditText etBrand = dialogView.findViewById(R.id.etBrand);
        TextInputEditText etStock = dialogView.findViewById(R.id.etStock);
        TextInputEditText etImage = dialogView.findViewById(R.id.etImage);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        List<String> categories = Arrays.asList("PC Gaming", "Laptop", "Component", "Screen", "Keyboard", "Mouse");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etProductName.getText().toString().trim();
            String description = etProductDescription.getText().toString().trim();
            String originalPriceStr = etOriginalPrice.getText().toString().trim();
            String discountPercentageStr = etDiscountPercentage.getText().toString().trim();
            String brand = etBrand.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String image = etImage.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();

            if (name.isEmpty() || originalPriceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double originalPrice = Double.parseDouble(originalPriceStr);
            float discountPercentage = discountPercentageStr.isEmpty() ? 0 : Float.parseFloat(discountPercentageStr);
            double discountedPrice = originalPrice * (1 - discountPercentage / 100);
            int stock = stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr);

            int ratingCount = 0;
            float averageRating = 0;

            Product newProduct = new Product(null, name, description, originalPrice, discountedPrice,
                    discountPercentage, ratingCount, averageRating, brand, stock, image, category);

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

    private void showUpdateProductDialog(Product product) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle("Update Product");

        TextInputEditText etProductName = dialogView.findViewById(R.id.etProductName);
        TextInputEditText etProductDescription = dialogView.findViewById(R.id.etProductDescription);
        TextInputEditText etOriginalPrice = dialogView.findViewById(R.id.etOriginalPrice);
        TextInputEditText etDiscountPercentage = dialogView.findViewById(R.id.etDiscountPercentage);
        TextInputEditText etBrand = dialogView.findViewById(R.id.etBrand);
        TextInputEditText etStock = dialogView.findViewById(R.id.etStock);
        TextInputEditText etImage = dialogView.findViewById(R.id.etImage);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        etProductName.setText(product.getName());
        etProductDescription.setText(product.getDescription());
        etOriginalPrice.setText(String.valueOf(product.getOriginalPrice()));
        etDiscountPercentage.setText(String.valueOf(product.getDiscountPercentage()));
        etBrand.setText(product.getBrand());
        etStock.setText(String.valueOf(product.getStock()));
        etImage.setText(product.getImage());

        List<String> categories = Arrays.asList("PC Gaming", "Laptop", "Component", "Screen", "Keyboard", "Mouse");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setSelection(categories.indexOf(product.getCategory()));

        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = etProductName.getText().toString().trim();
            String description = etProductDescription.getText().toString().trim();
            String originalPriceStr = etOriginalPrice.getText().toString().trim();
            String discountPercentageStr = etDiscountPercentage.getText().toString().trim();
            String brand = etBrand.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String image = etImage.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();

            if (name.isEmpty() || originalPriceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double originalPrice = Double.parseDouble(originalPriceStr);
            float discountPercentage = discountPercentageStr.isEmpty() ? 0 : Float.parseFloat(discountPercentageStr);
            double discountedPrice = originalPrice * (1 - discountPercentage / 100);
            int stock = stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr);

            Product updatedProduct = new Product(
                    product.getId(),
                    name,
                    description,
                    originalPrice,
                    discountedPrice,
                    discountPercentage,
                    product.getRatingCount(),
                    product.getAverageRating(),
                    brand,
                    stock,
                    image,
                    category
            );

            updateProduct(updatedProduct);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateProduct(Product product) {
        ProductService productService = ProductRepo.getProductService();
        Call<Product> call = productService.updateProduct(product.getId(), product);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int position = productList.indexOf(productList.stream()
                            .filter(p -> p.getId().equals(product.getId()))
                            .findFirst()
                            .orElse(null));
                    if (position != -1) {
                        productList.set(position, response.body());
                        productAdapter.notifyItemChanged(position);
                        Toast.makeText(getContext(), "Product updated successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to update product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}