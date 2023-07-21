package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;

public class forgotActivity extends AppCompatActivity {
    private EditText EditTextEmail;
    private Button buttonReset;
    private TextInputLayout LayoutEmail;
    private TextView backToLoginText;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        EditTextEmail = findViewById(R.id.editText_reset_email);
        buttonReset = findViewById(R.id.button_reset);
        backToLoginText = findViewById(R.id.textResetLinkBack);
        LayoutEmail = findViewById(R.id.email3);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        EditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!EditTextEmail.getText().toString().isEmpty()) {
                    LayoutEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        backToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgotActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = EditTextEmail.getText().toString().trim();

                if (TextUtils.isEmpty(textEmail)) {
                    //Toast.makeText(forgotActivity.this, "Please fill in your email", Toast.LENGTH_LONG).show();
                    LayoutEmail.setError("Email is Required");
                    EditTextEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    //Toast.makeText(signupActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
                    LayoutEmail.setError("Valid Email is Required");
                    EditTextEmail.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.sendPasswordResetEmail(textEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                /*
                                int colorInt = getResources().getColor(R.color.primary);
                                ColorStateList csl = ColorStateList.valueOf(colorInt);
                                LayoutEmail.setError("An email has been sent to reset your password. You will be sent back to the login page in 5 seconds.");
                                LayoutEmail.setErrorIconDrawable(null);
                                LayoutEmail.setErrorTextColor(csl);

                                 */
                                Toast.makeText(forgotActivity.this, "Email sent if the account exists", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                finish();
                                startActivity(new Intent(forgotActivity.this, MainActivity.class));

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    LayoutEmail.setError("Email is Wrong or Account Does Not Exist");
                                    buttonReset.setText("Resend Email");
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }
}