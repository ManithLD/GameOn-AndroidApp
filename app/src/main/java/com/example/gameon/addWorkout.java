package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.grpc.Context;

public class addWorkout extends AppCompatActivity {

    private String url = "https://api.openai.com/v1/completions";
    private String accessToken = "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";
    private Button bGen;
    private EditText EditTitle, EditContent;
    private FloatingActionButton saveWorkout;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private String[] item = {getEmojiByUnicode(0x2B50) + " Easy",
            getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Medium",
            getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Hard"};
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;
    private int difficulty = 0;
    private NumberPicker repPick;
    private NumberPicker setPick;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        bGen = findViewById(R.id.bGenerate);

        //Toast.makeText(this, GPT.chatGPT("Who are you?"), Toast.LENGTH_SHORT).show();

        selectCard = findViewById(R.id.selectCard);
        textAreas = findViewById(R.id.focusText);
        textAreas.setMovementMethod(new ScrollingMovementMethod());
        selected = new boolean[areas.length];
        selectCard.setOnClickListener(view -> {
            alertBuild();
        });
        textAreas.setOnClickListener(view -> {
            alertBuild();
        });

        String titleHint = "Enter your workout title here";
        String contentHint = "Enter your note workout here";

        EditTitle = findViewById(R.id.editWorkoutTitle);
        EditContent = findViewById(R.id.editContent);
        saveWorkout = findViewById(R.id.saveWorkout);
        saveWorkout = findViewById(R.id.saveWorkout);

        bGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAPI(EditTitle.getText().toString()); // testing
            }
        });


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
                    difficulty = 1;
                } else if (TextUtils.equals(item, getEmojiByUnicode(0x2B50) + getEmojiByUnicode(0x2B50) + " Medium")) {
                    difficulty = 2;
                } else {
                    difficulty = 3;
                }
                //Toast.makeText(addWorkout.this, difficulty, Toast.LENGTH_SHORT).show();
            }
        });

        repPick = findViewById(R.id.repPicker);
        repPick.setMinValue(1);
        repPick.setMaxValue(100);
        setPick = findViewById(R.id.setPicker);
        setPick.setMinValue(1);
        setPick.setMaxValue(50);
        //TextView view = findViewById(R.id.setHead);
        //String reps = Integer.toString(repPick.getValue());
        //view.setText(String.format(reps));

        saveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = EditTitle.getText().toString();
                String content = EditContent.getText().toString();
                String aof = textAreas.getText().toString();
                int reps = repPick.getValue();
                int sets = setPick.getValue();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    Toast.makeText(addWorkout.this, "Both fields are required", Toast.LENGTH_SHORT).show();
                }  else if (difficulty == 0) {
                    Toast.makeText(addWorkout.this, "Choose difficulty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    DocumentReference documentReference = firebaseFirestore
                            .collection("workouts").document(firebaseUser.getUid())
                            .collection("myWorkouts").document();
                    Map<String, Object> workout = new HashMap<>();
                    workout.put("title", title);
                    workout.put("content", content);
                    workout.put("aof", aof);
                    workout.put("difficulty", difficulty);
                    workout.put("reps", reps);
                    workout.put("sets", sets);


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


    private void callAPI(String query) {
        // Setting text for the question.
        // Creating a queue for the request queue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // Creating a JSON object.
        JSONObject jsonObject = new JSONObject();
        // Adding params to JSON object.
        try {
            jsonObject.put("model", "text-davinci-003");
            jsonObject.put("prompt", query);
            jsonObject.put("temperature", 0);
            jsonObject.put("max_tokens", 100);
            jsonObject.put("top_p", 1);
            jsonObject.put("frequency_penalty", 0.0);
            jsonObject.put("presence_penalty", 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Making JSON object request.
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Getting response message and setting it to text view.
                            String responseMsg = response.getJSONArray("choices").getJSONObject(0).getString("text");
                            Log.e("GPT MSG:", responseMsg);
                            Toast.makeText(addWorkout.this, responseMsg, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                // Adding on error listener.
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAGAPI", "Error is : " + error.getMessage() + "\n" + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Adding headers.
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer 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");
                return params;
            }
        };

        // Adding retry policy for the request.
        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                // You can implement your retry logic here if needed.
            }
        });
        // Adding the request to the queue.
        queue.add(postRequest);
    }

}