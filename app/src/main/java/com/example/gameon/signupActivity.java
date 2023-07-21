package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class signupActivity extends AppCompatActivity {

    private EditText editTextRegisterEmail, editTextRegisterPwd,
            editTextRegisterConfirmPwd;
    private TextInputLayout LayoutEmail, LayoutPwd, LayoutPwd2;
    private ProgressBar progressBar;
    private TextView backToLogin;
    private Button buttonRegister;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirmpassword);
        backToLogin = findViewById(R.id.textRegLinkBack);
        buttonRegister = findViewById(R.id.button_register);
        LayoutEmail = findViewById(R.id.email2);
        LayoutPwd = findViewById(R.id.password2);
        LayoutPwd2 = findViewById(R.id.password3);

        editTextRegisterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextRegisterEmail.getText().toString().isEmpty()) {
                    LayoutEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextRegisterPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextRegisterPwd.getText().toString().isEmpty()) {
                    LayoutPwd.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextRegisterConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextRegisterConfirmPwd.getText().toString().isEmpty()) {
                    LayoutPwd2.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextRegisterEmail.getText().toString().trim();
                String textPwd = editTextRegisterPwd.getText().toString().trim();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString().trim();

                if (TextUtils.isEmpty(textEmail)) {
                    //Toast.makeText(signupActivity.this, "Please fill in your email", Toast.LENGTH_LONG).show();
                    LayoutEmail.setError("Email is Required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    //Toast.makeText(signupActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
                    LayoutEmail.setError("Valid Email is Required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    //Toast.makeText(signupActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    LayoutPwd.setError("Password is Required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    //Toast.makeText(signupActivity.this, "Please choose a longer password", Toast.LENGTH_LONG).show();
                    LayoutPwd.setError("Password must be at least 6 characters");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    //Toast.makeText(signupActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    LayoutPwd2.setError("Password is Required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    //Toast.makeText(signupActivity.this, "Passwords don't match", Toast.LENGTH_LONG).show();
                    LayoutPwd2.setError("Passwords don't match");
                    editTextRegisterConfirmPwd.requestFocus();
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    registerUser(textEmail, textPwd);
                }
            }
        });
    }

    private void registerUser(String textEmail, String textPwd) {
        firebaseAuth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    if (firebaseUser != null) {
                        firebaseUser.sendEmailVerification();
                        firebaseAuth.signOut();
                        Toast.makeText(signupActivity.this, "Successful! Please verify your email", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(signupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(signupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}