package com.example.final_project_group5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.User;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> userList;
    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.item_user,viewGroup,false);
        }
        ImageView imageView = view.findViewById(R.id.imgAvatar);
        TextView tvUserName = view.findViewById(R.id.txtName);
        TextView tvUserEmail = view.findViewById(R.id.txtEmail);
        TextView tvUserAddress = view.findViewById(R.id.txtAddress);
        TextView tvUserPhone = view.findViewById(R.id.txtPhone);
        TextView tvUserActive = view.findViewById(R.id.txtActive);


        User user = userList.get(i);
        tvUserName.setText(user.getName());
        tvUserEmail.setText(user.getEmail());
        tvUserAddress.setText(user.getAddress());
        tvUserPhone.setText(user.getPhone());
        Glide.with(context).load(user.getAvatar()).into(imageView);

        tvUserActive.setText(user.isActive() ? "Active" : "Inactive");

        return view;
    }
}
