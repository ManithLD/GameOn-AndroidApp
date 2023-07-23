package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class noteDetailsActivity extends AppCompatActivity {

    private TextView workoutTitle;
    private TextView workoutContent;
    FloatingActionButton goToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        workoutTitle = findViewById(R.id.textWorkoutTitle);
        workoutContent = findViewById(R.id.textContent);
        goToEdit = findViewById(R.id.editWorkout);
        Toolbar toolbar = findViewById(R.id.toolBarDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();

        goToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), editWorkoutActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("workoutId", data.getStringExtra("workoutId"));
                view.getContext().startActivity(intent);
            }
        });

        workoutTitle.setText(data.getStringExtra("title"));
        workoutContent.setText(data.getStringExtra("content"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}