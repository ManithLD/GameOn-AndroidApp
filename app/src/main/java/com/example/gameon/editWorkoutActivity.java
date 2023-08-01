package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.util.Hex;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
    private String[] item = {getEmojiByUnicode(0x2B50) + " Easy",
            getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Medium",
            getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Hard"};

    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;
    private int difficulty = 0;
    private NumberPicker repPicker;
    private NumberPicker setPicker;
    boolean[] selected;
    private MaterialCardView selectCard;
    TextView textAreas;
    //ArrayList<Integer> indicies = new ArrayList<>();
    //ArrayList<String> removedItems = new ArrayList<>();
    String[] areas = {
            "Chest",
            "Back",
            "Shoulders (Deltoids)",
            "Biceps",
            "Triceps",
            "Legs (Quadriceps, Hamstrings, Calves)",
            "Abdominals (Core)",
            "Glutes",
            "Lower Back",
            "Forearms",
            "Neck",
            "Full Body"
    };
    ArrayList<String> tags = new ArrayList<>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContentWorkout);
        saveEditNote = findViewById(R.id.saveEditWorkout);
        repPicker = findViewById(R.id.repPicker);
        setPicker = findViewById(R.id.setPicker);

        Toolbar toolbar = findViewById(R.id.toolBarEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        selectCard = findViewById(R.id.selectCard);
        textAreas = findViewById(R.id.focusText);
        textAreas.setText(data.getStringExtra("aof"));
        textAreas.setMovementMethod(new ScrollingMovementMethod());
        selected = new boolean[areas.length];
        selectCard.setOnClickListener(view -> {
            alertBuild();
        });
        textAreas.setOnClickListener(view -> {
            alertBuild();
        });

        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        switch (data.getIntExtra("difficulty", 1)) {
            case 1:
                autoCompleteTextView.setText("Easy   " + getEmojiByUnicode(0x2B50));
                difficulty = 1;
                break;
            case 2:
                autoCompleteTextView.setText("Medium   " + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50));
                difficulty = 2;
                break;
            case 3:
                autoCompleteTextView.setText("Hard   " + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50));
                difficulty = 3;
                break;
        }
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                String text1 = getEmojiByUnicode(0x2B50) + " Easy";
                if (TextUtils.equals(item, getEmojiByUnicode(0x2B50) + " Easy")) {
                    difficulty = 1;
                } else if (TextUtils.equals(item, getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Medium")) {
                    difficulty = 2;
                } else {
                    difficulty = 3;
                }
                //Toast.makeText(addWorkout.this, difficulty, Toast.LENGTH_SHORT).show();
            }
        });

        String workoutTitle = data.getStringExtra("title");
        String workoutContent = data.getStringExtra("content");
        editTitle.setText(workoutTitle);
        editContent.setText(workoutContent);

        repPicker.setMinValue(1);
        repPicker.setMaxValue(100);
        setPicker.setMinValue(1);
        setPicker.setMaxValue(50);
        repPicker.setValue(data.getIntExtra("reps", 1));
        setPicker.setValue(data.getIntExtra("sets", 1));

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        saveEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(editWorkoutActivity.this, "save button clicked!", Toast.LENGTH_SHORT).show();

                String newTitle = editTitle.getText().toString();
                String newContent = editContent.getText().toString();
                String aof = textAreas.getText().toString();
                int reps = repPicker.getValue();
                int sets = setPicker.getValue();

                if (newTitle.isEmpty() || newContent.isEmpty()) {
                    Toast.makeText(editWorkoutActivity.this, "Both fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (difficulty == 0) {
                    Toast.makeText(editWorkoutActivity.this, "Choose difficulty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    DocumentReference documentReference = firebaseFirestore
                            .collection("workouts").document(firebaseUser.getUid())
                            .collection("myWorkouts").document(data.getStringExtra("workoutId"));
                    Map<String, Object> workout = new HashMap<>();
                    workout.put("title", newTitle);
                    workout.put("content", newContent);
                    workout.put("aof", aof);
                    workout.put("difficulty", difficulty);
                    workout.put("reps", reps);
                    workout.put("sets", sets);
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

    private void alertBuild() {
        AlertDialog.Builder builder = new AlertDialog.Builder(editWorkoutActivity.this);
        builder.setTitle("Select The Areas of Focus");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(areas, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    tags.add(areas[i]);
                } else {
                    tags.remove(areas[i]);
                }
            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < tags.size(); j++) {
                    stringBuilder.append(tags.get(j));
                    if (j != tags.size() - 1) stringBuilder.append(", ");
                }
                textAreas.setText(stringBuilder.toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < selected.length; j++) {
                    selected[j] = false;
                    tags.clear();
                    textAreas.setText("");
                }

            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}