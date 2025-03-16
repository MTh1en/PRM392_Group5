package com.example.final_project_group5.fragment.admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.final_project_group5.R;
import com.example.final_project_group5.adapter.UserAdapter;
import com.example.final_project_group5.entity.User;
import com.example.final_project_group5.repository.UserRepo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment implements UserAdapter.OnUserActionListener {
    private ListView listViewUsers;
    private UserAdapter userAdapter;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewUsers = view.findViewById(R.id.listViewUsers);
        fetchUsers();
    }

    private void fetchUsers() {
        UserRepo.getUserService().getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> userList = response.body();

                    // Lọc danh sách, chỉ giữ những user KHÔNG có role là "admin"
                    List<User> filteredList = new ArrayList<>();
                    for (User user : userList) {
                        if (!"admin".equalsIgnoreCase(user.getRole())) {
                            filteredList.add(user);
                        }
                    }

                    userAdapter = new UserAdapter(getContext(), filteredList);
                    userAdapter.setOnUserActionListener(UserFragment.this);
                    listViewUsers.setAdapter(userAdapter);
                } else {
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onToggleBan(User user, int position) {
        if (user.isActive()) {
            banUser(user, position);
        } else {
            unbanUser(user, position);
        }
    }

    private void banUser(User user, int position) {
        // Tạo đối tượng User mới với trạng thái isActive = false
        User updatedUser = new User(
                user.getId(),
                user.getName(),
                user.getAvatar(),
                user.getEmail(),
                user.getPassword(),
                user.getAddress(),
                user.getPhone(),
                user.getRole(),
                false, // Ban user
                user.getCreateAt(),
                user.getUpdateAt()
        );

        UserRepo.getUserService().updateUser(user.getId(), updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user.setActive(false);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "User banned successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to ban user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unbanUser(User user, int position) {
        // Tạo đối tượng User mới với trạng thái isActive = true
        User updatedUser = new User(
                user.getId(),
                user.getName(),
                user.getAvatar(),
                user.getEmail(),
                user.getPassword(),
                user.getAddress(),
                user.getPhone(),
                user.getRole(),
                true, // Unban user
                user.getCreateAt(),
                user.getUpdateAt()
        );

        UserRepo.getUserService().updateUser(user.getId(), updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user.setActive(true);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "User unbanned successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to unban user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}