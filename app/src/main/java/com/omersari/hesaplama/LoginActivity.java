package com.omersari.hesaplama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omersari.hesaplama.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{}

    }

    public void signInClicked(View view) {

        String  email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();

        }else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    public void signUpClicked(View view) {
        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
        }else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}