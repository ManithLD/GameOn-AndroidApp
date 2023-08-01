package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class noteDetailsActivity extends AppCompatActivity {

    private TextView workoutTitle;
    private TextView workoutContent;
    private TextView textAof;
    private TextView textDifficulty;
    private NumberPicker repPicker;
    private NumberPicker setPicker;
    FloatingActionButton goToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        workoutTitle = findViewById(R.id.textWorkoutTitle);
        workoutContent = findViewById(R.id.textContent);
        goToEdit = findViewById(R.id.editWorkout);
        textAof = findViewById(R.id.focusTextDetails);
        textDifficulty = findViewById(R.id.textDifficulty);
        repPicker = findViewById(R.id.repPicker);
        setPicker = findViewById(R.id.setPicker);

        Toolbar toolbar = findViewById(R.id.toolBarDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textAof.setMovementMethod(new ScrollingMovementMethod());

        Intent data = getIntent();

        goToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), editWorkoutActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("aof", data.getStringExtra("aof"));
                intent.putExtra("difficulty", data.getIntExtra("difficulty", 1));
                intent.putExtra("reps", data.getIntExtra("reps", 1));
                intent.putExtra("sets", data.getIntExtra("sets", 1));
                intent.putExtra("workoutId", data.getStringExtra("workoutId"));
                view.getContext().startActivity(intent);
            }
        });

        workoutTitle.setText(data.getStringExtra("title"));
        workoutContent.setText(data.getStringExtra("content"));
        textAof.setText(data.getStringExtra("aof"));
        switch (data.getIntExtra("difficulty", 1)) {
            case 1:
                textDifficulty.setText("Easy   " + getEmojiByUnicode(0x2B50));
                break;
            case 2:
                textDifficulty.setText("Medium   " + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50));
                break;
            case 3:
                textDifficulty.setText("Hard   " + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50));
                break;
        }
        repPicker.setMinValue(data.getIntExtra("reps", 1));
        repPicker.setMaxValue(data.getIntExtra("reps", 1));
        setPicker.setMinValue(data.getIntExtra("sets", 1));
        setPicker.setMaxValue(data.getIntExtra("sets", 1));
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}