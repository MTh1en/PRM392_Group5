package com.example.final_project_group5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.User;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> userList;
    private OnUserActionListener actionListener;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.actionListener = listener;
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
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.imgAvatar);
            holder.tvUserName = view.findViewById(R.id.txtName);
            holder.tvUserEmail = view.findViewById(R.id.txtEmail);
            holder.tvUserAddress = view.findViewById(R.id.txtAddress);
            holder.tvUserPhone = view.findViewById(R.id.txtPhone);
            holder.tvUserActive = view.findViewById(R.id.txtActive);
            holder.btnToggleBan = view.findViewById(R.id.btnToggleBan);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        User user = userList.get(i);
        holder.tvUserName.setText(user.getName());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserAddress.setText(user.getAddress());
        holder.tvUserPhone.setText(user.getPhone());
        Glide.with(context).load(user.getAvatar()).into(holder.imageView);

        // Cập nhật trạng thái và màu sắc
        holder.tvUserActive.setText(user.isActive() ? "Active" : "Inactive");
        holder.tvUserActive.setTextColor(user.isActive() ?
                Color.parseColor("#4CAF50") : // Màu xanh lá cho Active
                Color.parseColor("#FF4444")   // Màu đỏ cho Inactive
        );

        holder.btnToggleBan.setText(user.isActive() ? "Ban" : "Unban");

        holder.btnToggleBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(user, i);
            }
        });

        return view;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView tvUserName;
        TextView tvUserEmail;
        TextView tvUserAddress;
        TextView tvUserPhone;
        TextView tvUserActive;
        Button btnToggleBan;
    }

    private void showConfirmationDialog(final User user, final int position) {
        String action = user.isActive() ? "ban" : "unban";
        new AlertDialog.Builder(context)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to " + action + " this user?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (actionListener != null) {
                            actionListener.onToggleBan(user, position);
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public interface OnUserActionListener {
        void onToggleBan(User user, int position);
    }
}