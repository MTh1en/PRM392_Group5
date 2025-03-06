package com.example.final_project_group5;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.final_project_group5.adapter.UserAdapter;
import com.example.final_project_group5.entity.AppDatabase;
import com.example.final_project_group5.entity.AppExecutors;
import com.example.final_project_group5.entity.Users;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UserFragment extends Fragment {
    private ListView listViewUsers;
    private UserAdapter userAdapter;
    private AppDatabase appDatabase;




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
        appDatabase = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, "electronics_store.dn").build();

        // Lấy dữ liệu từ Room Database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Users> userList = appDatabase.usersDao().getAllUsers();
            Log.d("UserFragment", "Users: " + userList);

            // Cập nhật UI trên Main Thread
            requireActivity().runOnUiThread(() -> {
                userAdapter = new UserAdapter(requireContext(), userList);
                listViewUsers.setAdapter(userAdapter);
            });
        });
    }
}