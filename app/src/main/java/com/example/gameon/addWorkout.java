package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;

public class addWorkout extends AppCompatActivity {

    private EditText EditTitle, EditContent;
    private FloatingActionButton saveWorkout;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private ImageView editAOF;
    private String[] item = {getEmojiByUnicode(0x2B50) + " Easy",
            getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Medium",
            getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Hard"};
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;
    private String difficulty = "";
    private NumberPicker repPick;
    private NumberPicker setPick;
    boolean[] selected;
    private MaterialCardView selectCard;
    TextView textAreas;
    //ArrayList<Integer> indicies = new ArrayList<>();
    //ArrayList<String> removedItems = new ArrayList<>();
    String[] areas = {"ABS", "Biceps", "Legs", "Arm"};
    ArrayList<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        selectCard = findViewById(R.id.selectCard);
        textAreas = findViewById(R.id.focusText);
        selected = new boolean[areas.length];
        selectCard.setOnClickListener(view -> {
            alertBuild();
        });

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

        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                String text1 = getEmojiByUnicode(0x2B50) + " Easy";
                if (TextUtils.equals(item, getEmojiByUnicode(0x2B50) + " Easy")) {
                    difficulty = getEmojiByUnicode(0x2B50);
                } else if (TextUtils.equals(item, getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Medium")) {
                    difficulty = getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50);
                } else {
                    difficulty = getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50);
                }
                Toast.makeText(addWorkout.this, difficulty, Toast.LENGTH_SHORT).show();
            }
        });

        repPick = findViewById(R.id.repPicker);
        repPick.setMinValue(1);
        repPick.setMaxValue(100);
        setPick = findViewById(R.id.setPicker);
        setPick.setMinValue(1);
        setPick.setMaxValue(50);
        TextView view = findViewById(R.id.setHead);
        String reps = Integer.toString(repPick.getValue());
        //view.setText(String.format(reps));

        saveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = EditTitle.getText().toString() + " " + difficulty;
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

    private void alertBuild() {
        AlertDialog.Builder builder = new AlertDialog.Builder(addWorkout.this);
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

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}