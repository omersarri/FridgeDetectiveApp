package com.omersari.hesaplama;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omersari.hesaplama.databinding.ActivityLoginBinding;

import com.omersari.hesaplama.model.UserManager;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private UserManager userManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userManager = UserManager.getInstance();

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{}

    }

    public void loginClicked(View view) {

        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();

        }else{
            userManager.login(email, password, new UserManager.LoginCallback() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }



    public void toSignUpClick(View view) {
        Intent intentToSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intentToSignUp);
    }

}