package com.example.gameon;

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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class homeFragment extends Fragment {

    private FirebaseAuth auth;
    FirebaseUser firebaseUser;
    View root;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    workoutAdapter adapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private SearchView searchView;

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