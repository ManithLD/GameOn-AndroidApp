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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class homeActivity extends AppCompatActivity {

    private ImageView logout;
    private FirebaseAuth auth;
    FirebaseUser firebaseUser;

    workoutAdapter adapter;
    FloatingActionButton addWorkoutButton;

    BottomNavigationView nav;

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

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        addWorkoutButton = findViewById(R.id.addWorkout);
        logout = findViewById(R.id.logoutButton);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(homeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(homeActivity.this, addWorkout.class));
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}