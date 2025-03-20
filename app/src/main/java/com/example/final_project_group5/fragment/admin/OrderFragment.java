package com.example.final_project_group5.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project_group5.R;
import com.example.final_project_group5.adapter.OrderAdapterAdmin;
import com.example.final_project_group5.api.OrderService;
import com.example.final_project_group5.entity.Order;
import com.example.final_project_group5.repository.OrderRepo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView rvOrders;
    private OrderAdapterAdmin orderAdapter;
    private List<Order> orderList;

    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        // Ánh xạ RecyclerView
        rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách và adapter
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapterAdmin(orderList);
        rvOrders.setAdapter(orderAdapter);

        // Fetch tất cả đơn hàng
        fetchAllOrders();

        return view;
    }

    private void fetchAllOrders() {
        OrderService orderService = OrderRepo.getOrderService();
        Call<List<Order>> call = orderService.getAllOrders();

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();
                    if (orderList.isEmpty()) {
                        Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}