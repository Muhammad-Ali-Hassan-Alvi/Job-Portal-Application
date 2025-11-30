package com.example.jobapplicationportal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobapplicationportal.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllJobActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    // Firebase
    private DatabaseReference mAllJobPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_job);

        // Toolbar Setup
        toolbar = findViewById(R.id.all_job_post);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Jobs");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Database Reference
        mAllJobPost = FirebaseDatabase.getInstance().getReference().child("Public database");
        mAllJobPost.keepSynced(true);

        // RecyclerView Setup
        recyclerView = findViewById(R.id.recycler_all_job);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mAllJobPost, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, AllJobPostViewHolder> adapter = new FirebaseRecyclerAdapter<Data, AllJobPostViewHolder>(
                firebaseRecyclerOptions) {

            @NonNull
            @Override
            public AllJobPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // CHANGED: Using the new 'job_post_item' layout we created earlier
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_post_item, parent, false);
                return new AllJobPostViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AllJobPostViewHolder viewHolder, int i,
                    @NonNull final Data model) {

                viewHolder.setJobTitle(model.getTitle());
                viewHolder.setJobDate(model.getDate());
                viewHolder.setJobDescription(model.getDescription());
                viewHolder.setJobSkills(model.getSkills());
                viewHolder.setJobSalary(model.getSalary());

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), JobDetailsActivity.class);

                        intent.putExtra("id", model.getId());

                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("date", model.getDate());
                        intent.putExtra("description", model.getDescription());
                        intent.putExtra("skills", model.getSkills());
                        intent.putExtra("salary", model.getSalary());

                        startActivity(intent);
                    }
                });
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class AllJobPostViewHolder extends RecyclerView.ViewHolder {

        View myview;

        public AllJobPostViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setJobTitle(String title) {
            TextView mTitle = myview.findViewById(R.id.job_title);
            mTitle.setText(title);
        }

        public void setJobDate(String date) {
            TextView mDate = myview.findViewById(R.id.job_date);
            mDate.setText(date);
        }

        public void setJobDescription(String description) {
            TextView mDescription = myview.findViewById(R.id.job_description);
            mDescription.setText(description);
        }

        public void setJobSkills(String skills) {

            TextView mSkills = myview.findViewById(R.id.job_skills);
            if (mSkills != null) {
                mSkills.setText(skills);
            }
        }

        public void setJobSalary(String salary) {
            TextView mSalary = myview.findViewById(R.id.job_salary);
            mSalary.setText(salary);
        }
    }
}