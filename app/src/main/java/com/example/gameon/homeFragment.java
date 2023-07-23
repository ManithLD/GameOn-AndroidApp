package com.example.gameon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setUpRecycler();

        return root;
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