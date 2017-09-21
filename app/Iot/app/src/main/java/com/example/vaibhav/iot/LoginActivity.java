package com.example.vaibhav.iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FirebaseAuth firebaseAuth ;
        final EditText email,password;
        Button button_Login;
        final ProgressBar progressBar;
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        email = (EditText)findViewById(R.id.email_login);
        password = (EditText)findViewById(R.id.password_login);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_login);
        progressBar.setVisibility(View.INVISIBLE);
        button_Login =(Button)findViewById(R.id.button_login);
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_value = email.getText().toString();
                String password_value = password.getText().toString();
                if (TextUtils.isEmpty(email_value)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password_value)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email_value,password_value).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {
                            email.setError("Enter Correct Email");
                            password.setError("Enter Correct Password");
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            sharedPref.edit().putString(getString(R.string.email),email.getText().toString()).apply();
                            sharedPref.edit().putString(getString(R.string.password),password.getText().toString()).apply();
                            Intent i = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(i);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                
            }
        });

        Button registerButton = findViewById(R.id.signup_button_login);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        String emailString = sharedPref.getString(getString(R.string.email),null);
        String passwordString = sharedPref.getString(getString(R.string.password),null);
        if(emailString!=null && passwordString!=null){
            email.setText(emailString);
            password.setText(passwordString);
            button_Login.callOnClick();
        }
    }
}
