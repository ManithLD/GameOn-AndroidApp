package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addWorkout extends AppCompatActivity {

    EditText EditTitle, EditContent;
    FloatingActionButton saveWorkout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        String titleHint = "Enter your workout title here";
        String contentHint = "Enter your note workout here";


        EditTitle = findViewById(R.id.editWorkoutTitle);
        EditContent = findViewById(R.id.editContent);
        saveWorkout = findViewById(R.id.saveWorkout);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        saveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = EditTitle.getText().toString();
                String content = EditContent.getText().toString();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    Toast.makeText(addWorkout.this, "Both fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference documentReference = firebaseFirestore
                            .collection("workouts").document(firebaseUser.getUid())
                            .collection("myWorkouts").document();
                    Map<String, Object> workout = new HashMap<>();
                    workout.put("title", title);
                    workout.put("content", content);

                    documentReference.set(workout).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Workout Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(addWorkout.this, homeActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to Create Workout", Toast.LENGTH_SHORT).show();
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