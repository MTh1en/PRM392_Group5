package com.example.final_project_group5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView textName, textEmail, textAddress, textPhone;
    private ImageView imageProfile;
    private Button btnEdit;

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

        // Ánh xạ các view từ XML
        textName = view.findViewById(R.id.textView6);
        textEmail = view.findViewById(R.id.textView10);
        textAddress = view.findViewById(R.id.textView11);
        textPhone = view.findViewById(R.id.textView12);
        imageProfile = view.findViewById(R.id.imageProfile);
        btnEdit = view.findViewById(R.id.btnEdit);

        // Gọi hàm lấy dữ liệu từ API
        loadUserProfile();
    }

    private void loadUserProfile() {
        // TODO: Viết API call để lấy dữ liệu profile từ server
        // Ví dụ dữ liệu giả lập:
        String name = "John Doe";
        String email = "johndoe@example.com";
        String address = "123 Main Street, City";
        String phone = "+1234567890";

        // Set dữ liệu lên UI
        textName.setText(name);
        textEmail.setText(email);
        textAddress.setText(address);
        textPhone.setText(phone);

        // Nếu có ảnh từ API, có thể dùng Glide/Picasso để load
        // Glide.with(this).load(profileImageUrl).into(imageProfile);
    }
}
