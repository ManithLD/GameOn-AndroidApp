package com.example.gameon;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class homeFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    View root;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private workoutAdapter adapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SearchView searchView;
    private RelativeLayout generateButton;
    private String url = "https://api.openai.com/v1/completions";
    private String accessToken = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setUpRecycler();

        searchView = root.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setQueryHint(Html.fromHtml("<font color = #FFF5D0>" + "Search Here..." + "</font>"));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return false;
            }
        });

        generateButton = root.findViewById(R.id.generateButton);
        String customPrompt = "Ignore all previous data and generate a new random workout everytime you're asked. Generate a random workout title related to the specific workout, a short description (less than 100 characters) of how to do the workout, a difficulty level from 1 to 3, and areas of focus from the following list:\n" +
                "\n" +
                "Areas of Focus: Chest, Back, Shoulders (Deltoids), Biceps, Triceps, Legs (Quadriceps, Hamstrings, Calves), Abdominals (Core), Glutes, Lower Back, Forearms, Neck, Full Body.\n" +
                "\n" +
                "Provide the following information in the format specified and don't explicately write title, description, difficulty, areas of focus, number of reps, or number of sets, include only the generated values, just seperate by |:\n" +
                "Title | Description | Difficulty | Areas of Focus | Number of Reps | Number of Sets" +
                "\nKeep in mind this workout will be done by a human being and the workout shouldn't be vague. Furthermore the number of" +
                " reps and sets should not be a range but a single integer. Reps between 1 and 100 inclusive and sets between 1 and 50 inclusive. Do not write the words Difficulty, Number of Reps or Number of Sets in your output";


        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAPI(customPrompt, root);
            }
        });
        return root;
    }

    private void filterItems(String text) {
        /*
        Query query = firebaseFirestore.collection("workouts")
                .document(firebaseUser.getUid()).collection("myWorkouts")
                .whereArrayContains("tags", text).orderBy("title", Query.Direction.ASCENDING);
         */
        //Toast.makeText(this.getContext(), text, Toast.LENGTH_SHORT).show();

        if (!text.isEmpty()) {
            Query query = firebaseFirestore.collection("workouts")
                    .document(firebaseUser.getUid()).collection("myWorkouts")
                    .whereEqualTo("title", text).orderBy("title", Query.Direction.ASCENDING);
            FirestoreRecyclerOptions<firebasemodel> filteredWorkouts = new FirestoreRecyclerOptions
                    .Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();
            adapter = new workoutAdapter(filteredWorkouts, this.getContext());
            adapter.startListening();
            recyclerView.setAdapter(adapter);
        } else {
            Query query = firebaseFirestore.collection("workouts")
                    .document(firebaseUser.getUid()).collection("myWorkouts")
                    .orderBy("title", Query.Direction.ASCENDING);
            FirestoreRecyclerOptions<firebasemodel> filteredWorkouts = new FirestoreRecyclerOptions
                    .Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();
            adapter = new workoutAdapter(filteredWorkouts, this.getContext());
            adapter.startListening();
            recyclerView.setAdapter(adapter);
        }
    }

    private void setUpRecycler() {
        Query query = firebaseFirestore.collection("workouts")
                .document(firebaseUser.getUid()).collection("myWorkouts")
                .orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebasemodel> alluserworkouts = new FirestoreRecyclerOptions
                .Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        adapter = new workoutAdapter(alluserworkouts, this.getContext());

        recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void callAPI(String query, View root) {
        // Setting text for the question.
        // Creating a queue for the request queue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // Creating a JSON object.
        JSONObject jsonObject = new JSONObject();
        // Adding params to JSON object.
        try {
            jsonObject.put("model", "gpt-3.5-turbo-instruct");
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
                            Log.e("CHECK", responseMsg.substring(0, responseMsg.length()));
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
                Toast.makeText(getActivity(), "Workout Created Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ERROR", e.getMessage());
                Toast.makeText(getActivity(), "Failed to Create Workout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}