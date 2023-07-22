package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class homeActivity extends AppCompatActivity {

    private ImageView logout;
    private FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel, workoutViewHolder> workoutAdapater;
    FloatingActionButton addWorkoutButton;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        addWorkoutButton = findViewById(R.id.addWorkout);
        logout = findViewById(R.id.logoutButton);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(homeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(homeActivity.this, addWorkout.class));
            }
        });

        Query query = firebaseFirestore.collection("workouts")
                .document(firebaseUser.getUid()).collection("myWorkouts")
                .orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebasemodel> alluserworkouts = new FirestoreRecyclerOptions
                .Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        workoutAdapater = new FirestoreRecyclerAdapter<firebasemodel, workoutViewHolder>(alluserworkouts) {
            @Override
            protected void onBindViewHolder(@NonNull workoutViewHolder holder, int position, @NonNull firebasemodel model) {
                holder.workoutTitle.setText(model.getTitle());
                holder.workoutContent.setText(model.getContent());
            }
            @NonNull
            @Override
            public workoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_layout, parent, false);
                return new workoutViewHolder(view);
            }
        };

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(workoutAdapater);
    }


    public class workoutViewHolder extends RecyclerView.ViewHolder {
        private TextView workoutTitle;
        private TextView workoutContent;
        LinearLayout workout;
        public workoutViewHolder(@NonNull View itemView) {
            super(itemView);

            workoutTitle = itemView.findViewById(R.id.workoutTitle);
            workoutContent = itemView.findViewById(R.id.workoutContent);
            workout = itemView.findViewById(R.id.workout);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        workoutAdapater.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (workoutAdapater != null) {
            workoutAdapater.stopListening();
        }
    }

}