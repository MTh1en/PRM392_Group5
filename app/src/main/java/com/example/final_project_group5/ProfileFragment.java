package com.example.final_project_group5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.UserService;
import com.example.final_project_group5.entity.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private EditText textName, textEmail, textAddress, textPhone, textRole, textActive;
    private ImageView imageProfile;
    private Button btnEdit;
    private UserService userService;
    private String userId = "1"; // Cần thay bằng ID của user đang đăng nhập
    private boolean isEditing = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textName = view.findViewById(R.id.txtName2);
        textEmail = view.findViewById(R.id.txtEmail2);
        textAddress = view.findViewById(R.id.txtAddress2);
        textPhone = view.findViewById(R.id.txtPhone2);
        textRole = view.findViewById(R.id.txtRole2);
        textActive = view.findViewById(R.id.txtActive2);
        imageProfile = view.findViewById(R.id.imageProfile);
        btnEdit = view.findViewById(R.id.btnEdit);

        userService = ApiClient.getUserService();
        loadUserProfile();

        btnEdit.setOnClickListener(v -> toggleEditMode());
    }

    private void loadUserProfile() {
        Call<User> call = userService.getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    textName.setText(user.getName());
                    textEmail.setText(user.getEmail());
                    textAddress.setText(user.getAddress());
                    textPhone.setText(user.getPhone());
                    textRole.setText(user.getRole());
                    textActive.setText(user.isActive() ? "Active" : "Inactive");

                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Glide.with(requireContext())
                                .load(user.getAvatar())
                                .placeholder(R.drawable.app_logo)
                                .error(R.drawable.app_logo)
                                .into(imageProfile);
                    }

                    setEditable(false);
                } else {
                    Toast.makeText(getContext(), "Lỗi lấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleEditMode() {
        if (isEditing) {
            saveUserProfile();
        } else {
            setEditable(true);
        }
        isEditing = !isEditing;
        btnEdit.setText(isEditing ? "Save" : "Edit");
    }

    private void setEditable(boolean enabled) {
        textName.setEnabled(enabled);
        textEmail.setEnabled(enabled);
        textAddress.setEnabled(enabled);
        textPhone.setEnabled(enabled);
        textRole.setEnabled(enabled);
        textActive.setEnabled(enabled);
    }

    private void saveUserProfile() {
        User updatedUser = new User();
        updatedUser.setName(textName.getText().toString());
        updatedUser.setEmail(textEmail.getText().toString());
        updatedUser.setAddress(textAddress.getText().toString());
        updatedUser.setPhone(textPhone.getText().toString());
        updatedUser.setRole(textRole.getText().toString());
        updatedUser.setActive(textActive.getText().toString().equalsIgnoreCase("Active"));

        Call<User> call = userService.updateUser(userId, updatedUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    setEditable(false);
                } else {
                    Toast.makeText(getContext(), "Lỗi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
