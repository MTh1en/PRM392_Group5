package com.example.final_project_group5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import com.example.final_project_group5.entity.Users;
import com.example.final_project_group5.R;
import java.util.List;

public class UserAdapter extends ArrayAdapter<Users> {
    public UserAdapter(Context context, List<Users> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        Users user = getItem(position);

        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtEmail = convertView.findViewById(R.id.txtEmail);
        TextView txtAddress = convertView.findViewById(R.id.txtAddress);
        TextView txtPhone = convertView.findViewById(R.id.txtPhone);

        if (user != null) {
            txtName.setText(user.getName());
            txtEmail.setText(user.getEmail());
            txtAddress.setText(user.getAddress());
            txtPhone.setText(user.getPhone());
        }

        return convertView;
    }
}
