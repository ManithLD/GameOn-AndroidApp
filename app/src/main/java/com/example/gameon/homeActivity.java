package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class homeActivity extends AppCompatActivity {

    private ImageView logout;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    workoutAdapter adapter;
    FloatingActionButton addWorkoutButton;

    BottomNavigationView nav;
    private String url = "https://api.openai.com/v1/completions";
    private String accessToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nav = findViewById(R.id.bottomNavigationView);
        replaceFragment(new homeFragment());
        nav.setBackground(null);
        TextView title = findViewById(R.id.topTitle);

        nav.setOnItemSelectedListener(item -> {
            switch (item.getTitle().toString()) {
                case "Home":
                    replaceFragment(new homeFragment());
                    title.setText("Home");
                    break;
                case "Health":
                    replaceFragment(new healthFragment());
                    title.setText("Health");
                    break;
                case "Clock":
                    replaceFragment(new clockFragment());
                    title.setText("Clock");
                    break;
                case "Schedule":
                    replaceFragment(new scheduleFragment());
                    title.setText("Schedule");
                    break;

            }

            return true;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        addWorkoutButton = findViewById(R.id.addWorkout);
        logout = findViewById(R.id.logoutButton);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(homeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        /*
        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(homeActivity.this, addWorkout.class));
            }
        });

         */
        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.setGravity(Gravity.CENTER);
                popupMenu.getMenu().add("Add Custom Workout").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                        startActivity(new Intent(homeActivity.this, addWorkout.class));
                        return false;
                    }
                });
                popupMenu.getMenu().add("Generate Workout").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                        String customPrompt = "Ignore all previous data and generate a new random workout everytime you're asked. Generate a random workout title related to the specific workout, a short description (less than 100 characters) of how to do the workout, a difficulty level from 1 to 3, and areas of focus from the following list:\n" +
                                "\n" +
                                "Areas of Focus: Chest, Back, Shoulders (Deltoids), Biceps, Triceps, Legs (Quadriceps, Hamstrings, Calves), Abdominals (Core), Glutes, Lower Back, Forearms, Neck, Full Body.\n" +
                                "\n" +
                                "Provide the following information in the format specified and don't explicately write title, just seperate by |:\n" +
                                "Title | Description | Difficulty | Areas of Focus | Number of Reps | Number of Sets" +
                                "\nKeep in mind this workout will be done by a human being and the workout shouldn't be vague. Furthermore the number of" +
                                " reps and sets should not be a range but a single integer. Reps between 1 and 100 inclusive and sets between 1 and 50 inclusive.";
                        callAPI(customPrompt);
                        // testing
                        //Flip Flop Pushups | Get into a pushup position and alternate between a regular and inverted pushup | 2 | Chest, Shoulders | 8 | 3
                        //Jump Rope Burpees | Jump rope for 30 seconds, perform 10 burpees, repeat 3 times | 3 | Full Body | 30 | 3
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
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
            jsonObject.put("temperature", 0.75);
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
                            int temp;
                            // Getting response message and setting it to text view.
                            String responseMsg = response.getJSONArray("choices").getJSONObject(0).getString("text");
                            Log.e("GPT MSG:", responseMsg);
                            //Toast.makeText(addWorkout.this, responseMsg, Toast.LENGTH_SHORT).show();
                            //Flip Flop Pushups | Get into a pushup position and alternate between a regular and inverted pushup | 2 | Chest, Shoulders | 8 | 3
                            //Jump Rope Burpees | Jump rope for 30 seconds, perform 10 burpees, repeat 3 times | 3 | Full Body | 30 | 3
                            temp = responseMsg.indexOf("|") - 1;
                            String title = responseMsg.substring(0, temp);
                            responseMsg = responseMsg.substring(temp + 3);
                            temp = responseMsg.indexOf("|") - 1;
                            String content = responseMsg.substring(0, temp);
                            responseMsg = responseMsg.substring(temp + 3);
                            temp = responseMsg.indexOf("|") - 1;
                            int difficulty = Integer.parseInt(responseMsg.substring(0, temp));
                            responseMsg = responseMsg.substring(temp + 3);
                            temp = responseMsg.indexOf("|") - 1;
                            String aof = responseMsg.substring(0, temp);
                            responseMsg = responseMsg.substring(temp + 3);
                            temp = responseMsg.indexOf("|") - 1;
                            int reps = Integer.parseInt(responseMsg.substring(0, temp));
                            responseMsg = responseMsg.substring(temp + 3);
                            int sets = Integer.parseInt(responseMsg.substring(0, responseMsg.length()));
                            addGeneratedWorkout(title, content, difficulty, aof, reps, sets);
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
                params.put("Authorization", "Bearer " + accessToken);
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

    private void addGeneratedWorkout(String title, String content, int difficulty, String aof, int reps, int sets) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore
                .collection("workouts").document(firebaseUser.getUid())
                .collection("myWorkouts").document();
        Map<String, Object> workout = new HashMap<>();
        String tempTitle = title.trim() + " [" + getEmojiByUnicode(0x1F300) + "] ";
        workout.put("title", tempTitle);
        workout.put("content", content.trim());
        workout.put("aof", aof.trim());
        workout.put("difficulty", difficulty);
        workout.put("reps", reps);
        workout.put("sets", sets);

        documentReference.set(workout).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Workout Created Successfully", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(homeActivity.this, homeActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to Create Workout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}