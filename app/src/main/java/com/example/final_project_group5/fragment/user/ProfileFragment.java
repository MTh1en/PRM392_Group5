package com.example.final_project_group5.fragment.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.User;
import com.example.final_project_group5.repository.UserRepo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView changePasswordTitle;
    private EditText etNameInput, etEmailInput, etAddressInput, etPhoneInput, etOldPassword, etNewPassword, etConfirmPassword;
    private ImageView ivProfileImage;
    private Button btnEditProfile, btnChangePassword;
    private String userId;
    private boolean isEditing = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changePasswordTitle = view.findViewById(R.id.tvChangePasswordTitle);
        etNameInput = view.findViewById(R.id.etNameInput);
        etEmailInput = view.findViewById(R.id.etEmailInput);
        etAddressInput = view.findViewById(R.id.etAddressInput);
        etPhoneInput = view.findViewById(R.id.etPhoneInput);
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        Log.d("ProfileFragment", "User ID: " + userId);
        loadUserProfile();
        UserRepo.getUserService().getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = response.body();
                    if (user.getPassword().equals("google")){
                        changePasswordTitle.setText("Không thể đổi mật khẩu với tài khoản đăng nhập bằng google");
                        etOldPassword.setVisibility(View.GONE);
                        etNewPassword.setVisibility(View.GONE);
                        etConfirmPassword.setVisibility(View.GONE);
                        btnChangePassword.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        btnEditProfile.setOnClickListener(v -> toggleEditMode());
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void loadUserProfile() {
        Call<User> call = UserRepo.getUserService().getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    etNameInput.setText(user.getName());
                    etEmailInput.setText(user.getEmail());
                    etAddressInput.setText(user.getAddress());
                    etPhoneInput.setText(user.getPhone());

                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Glide.with(requireContext())
                                .load(user.getAvatar())
                                .placeholder(R.drawable.app_logo)
                                .error(R.drawable.app_logo)
                                .into(ivProfileImage);
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
        btnEditProfile.setText(isEditing ? "Save" : "Edit");
    }

    private void setEditable(boolean enabled) {
        etNameInput.setEnabled(enabled);
        etAddressInput.setEnabled(enabled);
        etPhoneInput.setEnabled(enabled);
    }

    private void saveUserProfile() {
        User updatedUser = new User();
        updatedUser.setName(etNameInput.getText().toString());
        updatedUser.setAddress(etAddressInput.getText().toString());
        updatedUser.setPhone(etPhoneInput.getText().toString());
        updatedUser.setActive(true);

        Call<User> call = UserRepo.getUserService().updateUser(userId, updatedUser);
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

    private void changePassword() {
        UserRepo.getUserService().getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                String oldPassword = etOldPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user.getPassword().equals("google")){
                        Toast.makeText(getContext(), "Không thể đổi mật khẩu cho tài khoản google", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (user.getPassword().equals(oldPassword)) {
                        if (newPassword.equals(confirmPassword)) {
                            User updatePasswordUser = new User();
                            updatePasswordUser.setPassword(newPassword);
                            UserRepo.getUserService().updateUser(userId, updatePasswordUser).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Đã cập nhật password thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(getContext(), "Đổi mật khẩu thất bại" + t, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Đổi mật khẩu thất bại" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
