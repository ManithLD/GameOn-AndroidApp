package com.example.gameon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editTextLoginEmail, editTextLoginPwd;
    private TextInputLayout LayoutEmail, LayoutPwd;
    private ProgressBar progressBar;
    private Button buttonLogin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            finish();
            startActivity(new Intent(MainActivity.this, homeActivity.class));
        }

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_password);
        progressBar = findViewById(R.id.progressBar);
        buttonLogin = findViewById(R.id.button_login);
        LayoutEmail = findViewById(R.id.email);
        LayoutPwd = findViewById(R.id.password);

        editTextLoginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextLoginEmail.getText().toString().isEmpty()) {
                    LayoutEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editTextLoginPwd.getText().toString().isEmpty()) {
                    LayoutPwd.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //register
        TextView textViewRegister = findViewById(R.id.textView_link_register);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(MainActivity.this, signupActivity.class);
                startActivity(registerActivity);
            }
        });

        TextView textViewForgot = findViewById(R.id.textView_forgot);
        textViewForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(MainActivity.this, forgotActivity.class);
                startActivity(registerActivity);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText().toString().trim();
                String textPwd = editTextLoginPwd.getText().toString().trim();

                if (TextUtils.isEmpty(textEmail)) {
                    //Toast.makeText(MainActivity.this, "Please fill in your email", Toast.LENGTH_LONG).show();
                    LayoutEmail.setError("Email is Required");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    //Toast.makeText(MainActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
                    LayoutEmail.setError("Valid Email is Required");
                    editTextLoginEmail.requestFocus();
                }  else if (TextUtils.isEmpty(textPwd)) {
                    //Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    LayoutPwd.setError("Password is Required");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkEmailVerification();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    LayoutEmail.setError("User does not exist or no longer valid");
                                    editTextLoginEmail.requestFocus();;
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    LayoutEmail.setError("Invalid credentials");
                                    editTextLoginEmail.requestFocus();;
                                } catch (Exception e) {
                                    Log.e("MainActivity", e.getMessage());
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser.isEmailVerified() == true) {
            finish();
            startActivity(new Intent(MainActivity.this, homeActivity.class));
        } else {
            LayoutEmail.setError("Email not verified!");
            firebaseAuth.signOut();
            //showAlert();
        }
    }

//    private void showAlert() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Email Not Verified");
//        builder.setMessage("Please verify your email now. You cannot login without verifying your email.");
//        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //opens email
//                startActivity(intent);
//            }
//        });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

}