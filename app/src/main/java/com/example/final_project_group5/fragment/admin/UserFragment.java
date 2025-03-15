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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserFragment extends Fragment {
    private ListView listViewUsers;
    private UserAdapter userAdapter;
    public UserFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_user, container, false);
    }
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewUsers = view.findViewById(R.id.listViewUsers);

        fetchUsers();
        listViewUsers.setAdapter(userAdapter);
    }
    private void fetchUsers(){
        UserRepo.getUserService().getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful() && response.body()!= null ){
                    List<User> userList = response.body();
                    userAdapter = new UserAdapter(getContext(), userList);
                    listViewUsers.setAdapter(userAdapter);
                }else {
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}