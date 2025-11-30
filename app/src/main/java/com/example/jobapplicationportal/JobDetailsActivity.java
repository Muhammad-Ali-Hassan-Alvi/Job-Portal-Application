package com.example.jobapplicationportal;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationportal.Model.Data;
import com.example.jobapplicationportal.adapter.CommentsAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class JobDetailsActivity extends AppCompatActivity {

    // UI Views
    private Toolbar toolbar;
    private TextView mTitle, mDate, mDescription, mSkills, mSalary;
    private TextView tvLikeCount, tvDislikeCount;
    private MaterialButton btnLike, btnDislike, btnPostComment; // Changed to MaterialButton
    private EditText etComment;
    private RecyclerView rvComments;

    // Logic Variables
    private CommentsAdapter commentsAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mJobPostRef;
    private String jobId;
    private String currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        // 1. Setup Toolbar
        toolbar = findViewById(R.id.toolbar_job_details);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Job Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 2. Initialize Firebase User
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            // Fallback if Display Name is empty
            currentUserName = TextUtils.isEmpty(currentUser.getDisplayName()) ? "User" : currentUser.getDisplayName();
        }

        // 3. Initialize Views
        initViews();

        // 4. Get Data from Intent
        // IMPORTANT: We primarily need the ID to fetch live data
        Intent intent = getIntent();
        jobId = intent.getStringExtra("id");

        // Set text immediately from intent (for better UX while loading)
        if (intent.hasExtra("title"))
            mTitle.setText(intent.getStringExtra("title"));
        if (intent.hasExtra("date"))
            mDate.setText(intent.getStringExtra("date"));
        if (intent.hasExtra("description"))
            mDescription.setText(intent.getStringExtra("description"));
        if (intent.hasExtra("skills"))
            mSkills.setText(intent.getStringExtra("skills"));
        if (intent.hasExtra("salary"))
            mSalary.setText(intent.getStringExtra("salary"));

        // 5. Validation
        if (jobId == null) {
            Toast.makeText(this, "Error: Job Not Found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 6. Setup Database Reference
        // Note: Ensure this path matches where you saved data in InsertJobPostActivity
        mJobPostRef = FirebaseDatabase.getInstance().getReference()
                .child("Public database")
                .child(jobId);

        // 7. Setup Recycler View for Comments
        setupRecyclerView();

        // 8. Start Listening to Database Changes
        loadJobDetails();

        // 9. Click Listeners
        btnLike.setOnClickListener(v -> handleLike());
        btnDislike.setOnClickListener(v -> handleDislike());
        btnPostComment.setOnClickListener(v -> postComment());
    }

    private void initViews() {
        mTitle = findViewById(R.id.job_details_title);
        mDate = findViewById(R.id.job_details_date);
        mDescription = findViewById(R.id.job_details_description);
        mSkills = findViewById(R.id.job_details_skills);
        mSalary = findViewById(R.id.job_details_salary);

        // Engagement Views
        btnLike = findViewById(R.id.btn_like);
        btnDislike = findViewById(R.id.btn_dislike);
        tvLikeCount = findViewById(R.id.tv_like_count);
        tvDislikeCount = findViewById(R.id.tv_dislike_count);

        // Comment Views
        etComment = findViewById(R.id.et_comment);
        btnPostComment = findViewById(R.id.btn_post_comment);
        rvComments = findViewById(R.id.rv_comments);
    }

    private void setupRecyclerView() {
        commentsAdapter = new CommentsAdapter(new ArrayList<>());
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setNestedScrollingEnabled(false); // Smooth scrolling inside NestedScrollView
        rvComments.setAdapter(commentsAdapter);
    }

    /**
     * Listens for any changes in the Job Post (likes, comments, etc.)
     * and updates the UI automatically.
     */
    private void loadJobDetails() {
        mJobPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Data jobData = dataSnapshot.getValue(Data.class);
                    if (jobData != null) {
                        // Refresh text fields (in case admin edited them)
                        mTitle.setText(jobData.getTitle());
                        mDate.setText(jobData.getDate());
                        mDescription.setText(jobData.getDescription());
                        mSkills.setText(jobData.getSkills());
                        mSalary.setText(jobData.getSalary());

                        // Update Logic
                        updateLikeDislikeUI(jobData);

                        // Update Comments
                        if (jobData.getComments() != null) {
                            commentsAdapter.updateComments(jobData.getComments());
                        } else {
                            commentsAdapter.updateComments(new ArrayList<>());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void updateLikeDislikeUI(Data jobData) {
        tvLikeCount.setText(String.valueOf(jobData.getLikeCount()));
        tvDislikeCount.setText(String.valueOf(jobData.getDislikeCount()));

        // Define Colors
        int colorActive = ContextCompat.getColor(this, R.color.colorPrimary);
        int colorError = ContextCompat.getColor(this, R.color.colorError);
        int colorInactive = ContextCompat.getColor(this, R.color.colorOnSurfaceVariant);

        // Check if I liked it
        if (currentUserId != null && jobData.getLikes() != null && jobData.getLikes().containsKey(currentUserId)) {
            btnLike.setTextColor(colorActive);
            btnLike.setIconTint(ColorStateList.valueOf(colorActive));
        } else {
            btnLike.setTextColor(colorInactive);
            btnLike.setIconTint(ColorStateList.valueOf(colorInactive));
        }

        // Check if I disliked it
        if (currentUserId != null && jobData.getDislikes() != null
                && jobData.getDislikes().containsKey(currentUserId)) {
            btnDislike.setTextColor(colorError);
            btnDislike.setIconTint(ColorStateList.valueOf(colorError));
        } else {
            btnDislike.setTextColor(colorInactive);
            btnDislike.setIconTint(ColorStateList.valueOf(colorInactive));
        }
    }

    private void handleLike() {
        if (currentUserId == null) {
            Toast.makeText(this, "Please login to interact", Toast.LENGTH_SHORT).show();
            return;
        }

        mJobPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Data data = snapshot.getValue(Data.class);
                    if (data != null) {
                        // Initialize maps if null
                        if (data.getLikes() == null)
                            data.setLikes(new HashMap<>());
                        if (data.getDislikes() == null)
                            data.setDislikes(new HashMap<>());

                        // Toggle Logic
                        if (data.getLikes().containsKey(currentUserId)) {
                            data.getLikes().remove(currentUserId);
                        } else {
                            data.getLikes().put(currentUserId, true);
                            data.getDislikes().remove(currentUserId); // Remove dislike if exists
                        }

                        // Update counts
                        data.setLikeCount(data.getLikes().size());
                        data.setDislikeCount(data.getDislikes().size());

                        // Save to Firebase
                        mJobPostRef.setValue(data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void handleDislike() {
        if (currentUserId == null) {
            Toast.makeText(this, "Please login to interact", Toast.LENGTH_SHORT).show();
            return;
        }

        mJobPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Data data = snapshot.getValue(Data.class);
                    if (data != null) {
                        if (data.getLikes() == null)
                            data.setLikes(new HashMap<>());
                        if (data.getDislikes() == null)
                            data.setDislikes(new HashMap<>());

                        if (data.getDislikes().containsKey(currentUserId)) {
                            data.getDislikes().remove(currentUserId);
                        } else {
                            data.getDislikes().put(currentUserId, true);
                            data.getLikes().remove(currentUserId); // Remove like if exists
                        }

                        data.setLikeCount(data.getLikes().size());
                        data.setDislikeCount(data.getDislikes().size());

                        mJobPostRef.setValue(data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void postComment() {
        String commentText = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) {
            etComment.setError("Write something...");
            return;
        }
        if (currentUserId == null) {
            Toast.makeText(this, "Please login to comment", Toast.LENGTH_SHORT).show();
            return;
        }

        mJobPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data data = snapshot.getValue(Data.class);
                if (data != null) {
                    // Create Comment Object
                    String commentId = mJobPostRef.push().getKey();
                    Data.Comment newComment = new Data.Comment(
                            commentId,
                            currentUserId,
                            currentUserName,
                            commentText);

                    if (data.getComments() == null)
                        data.setComments(new ArrayList<>());
                    data.getComments().add(newComment);

                    mJobPostRef.setValue(data).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            etComment.setText("");
                            Toast.makeText(JobDetailsActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}