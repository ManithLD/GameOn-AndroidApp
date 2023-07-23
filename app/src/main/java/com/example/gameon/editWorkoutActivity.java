package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class editWorkoutActivity extends AppCompatActivity {

    private Intent data;
    private EditText editTitle;
    private EditText editContent;
    private FloatingActionButton saveEditNote;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContentWorkout);
        saveEditNote = findViewById(R.id.saveEditWorkout);
        Toolbar toolbar = findViewById(R.id.toolBarEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        String workoutTitle = data.getStringExtra("title");
        String workoutContent = data.getStringExtra("content");
        editTitle.setText(workoutTitle);
        editContent.setText(workoutContent);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        saveEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(editWorkoutActivity.this, "save button clicked!", Toast.LENGTH_SHORT).show();

                String newTitle = editTitle.getText().toString();
                String newContent = editContent.getText().toString();

                if (newTitle.isEmpty() || newContent.isEmpty()) {
                    Toast.makeText(editWorkoutActivity.this, "Both fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    DocumentReference documentReference = firebaseFirestore
                            .collection("workouts").document(firebaseUser.getUid())
                            .collection("myWorkouts").document(data.getStringExtra("workoutId"));
                    Map<String, Object> workout = new HashMap<>();
                    workout.put("title", newTitle);
                    workout.put("content", newContent);
                    documentReference.set(workout).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(editWorkoutActivity.this, "Note Updated!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(editWorkoutActivity.this, homeActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(editWorkoutActivity.this, "Failed to Update!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}