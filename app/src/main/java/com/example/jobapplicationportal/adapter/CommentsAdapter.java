package com.example.jobapplicationportal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationportal.Model.Data;
import com.example.jobapplicationportal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Data.Comment> commentsList;

    // Constructor
    public CommentsAdapter(List<Data.Comment> commentsList) {
        this.commentsList = commentsList != null ? commentsList : new ArrayList<>();
    }

    // --- THIS FIXES THE "Cannot resolve method updateComments" ERROR ---
    public void updateComments(List<Data.Comment> newComments) {
        this.commentsList = newComments != null ? newComments : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This connects to the item_comment.xml layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Data.Comment comment = commentsList.get(position);

        holder.tvUserName.setText(comment.getUserName() != null ? comment.getUserName() : "Anonymous");
        holder.tvContent.setText(comment.getContent());

        // Format Timestamp
        if (comment.getTimestamp() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault());
            holder.tvDate.setText(sdf.format(new Date(comment.getTimestamp())));
        } else {
            holder.tvDate.setText("Just now");
        }
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    // ViewHolder Class
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvContent, tvDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your item_comment.xml exactly
            tvUserName = itemView.findViewById(R.id.tv_comment_user);
            tvContent = itemView.findViewById(R.id.tv_comment_text);
            tvDate = itemView.findViewById(R.id.tv_comment_time);
        }
    }
}