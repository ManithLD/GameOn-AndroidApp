package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addWorkout extends AppCompatActivity implements Adapter.ItemClickListener {

    EditText EditTitle, EditContent;
    FloatingActionButton saveWorkout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    ImageView editAOF;
    private Adapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        String titleHint = "Enter your workout title here";
        String contentHint = "Enter your note workout here";


        EditTitle = findViewById(R.id.editWorkoutTitle);
        EditContent = findViewById(R.id.editContent);
        saveWorkout = findViewById(R.id.saveWorkout);
        editAOF = findViewById(R.id.editAOF);
        editAOF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(addWorkout.this, "clicked", Toast.LENGTH_SHORT).show();
                //show dialog box!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        });

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ArrayList<String> tags = new ArrayList<>();
        tags.add("ABS");
        tags.add("Arms");
        tags.add("Sholders UWU OWO WOOOOW");
        tags.add("Back");
        tags.add("Chest");
        tags.add("Biceps");

        recyclerView = findViewById(R.id.rvTags);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        adapter = new Adapter(this, tags);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(addWorkout.this, "clicked", Toast.LENGTH_SHORT).show();
                //show dialog box!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                return false;
            }
        });

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

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on item position " + position, Toast.LENGTH_SHORT).show();
        //show dialog box!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
}