package com.example.final_project_group5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.Feedback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private Context context;
    private List<Feedback> feedbackList;
    private Map<Integer, String> userMap; // Map để lưu userId -> username

    public FeedbackAdapter(Context context, List<Feedback> feedbackList, Map<Integer, String> userMap) {
        this.context = context;
        this.feedbackList = feedbackList;
        this.userMap = userMap != null ? userMap : new HashMap<>();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        String username = userMap.getOrDefault(feedback.getUserId(), "Unknown User");
        holder.tvUserId.setText("User: " + username); // Hiển thị username thay vì userId
        holder.tvTitle.setText(feedback.getTitle());
        holder.tvComment.setText(feedback.getComment());
        holder.rbRating.setRating(feedback.getRating());
        holder.tvCreateAt.setText(feedback.getCreateAt());
    }

    @Override
    public int getItemCount() {
        return feedbackList != null ? feedbackList.size() : 0;
    }

    public void updateFeedbacks(List<Feedback> newFeedbackList) {
        this.feedbackList = newFeedbackList;
        notifyDataSetChanged();
    }

    public void updateUserMap(Map<Integer, String> newUserMap) {
        this.userMap = newUserMap != null ? newUserMap : new HashMap<>();
        notifyDataSetChanged();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvTitle, tvComment, tvCreateAt;
        RatingBar rbRating;

        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvComment = itemView.findViewById(R.id.tvComment);
            rbRating = itemView.findViewById(R.id.rbRating);
            tvCreateAt = itemView.findViewById(R.id.tvCreateAt);
        }
    }
}