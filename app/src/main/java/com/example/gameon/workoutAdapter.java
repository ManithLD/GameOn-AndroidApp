package com.example.gameon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class workoutAdapter extends FirestoreRecyclerAdapter<firebasemodel, workoutAdapter.workoutViewHolder> {

    public workoutAdapter(@NonNull FirestoreRecyclerOptions<firebasemodel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull workoutViewHolder holder, int position, @NonNull firebasemodel model) {
        holder.workoutTitle.setText(model.getTitle());
        holder.workoutContent.setText(model.getContent());
    }
    @NonNull
    @Override
    public workoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_layout, parent, false);
        return new workoutViewHolder(view);
    }

    public class workoutViewHolder extends RecyclerView.ViewHolder {
        private TextView workoutTitle;
        private TextView workoutContent;
        LinearLayout workout;
        public workoutViewHolder(@NonNull View itemView) {
            super(itemView);

            workoutTitle = itemView.findViewById(R.id.workoutTitle);
            workoutContent = itemView.findViewById(R.id.workoutContent);
            workout = itemView.findViewById(R.id.workout);
        }
    }
}
