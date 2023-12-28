package com.omersari.hesaplama;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.omersari.hesaplama.databinding.ActivitySignupBinding;

import com.omersari.hesaplama.model.UserManager;



public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private String gender;

    private UserManager userManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userManager = UserManager.getInstance();

        genderRadioButtonClick();


    }

    public void genderRadioButtonClick() {
        binding.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){gender = "Male";}

            }
        });
        binding.radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){gender = "Female";}
            }
        });
    }


    public void signUpClick(View view) {
        String name = binding.nameText.getText().toString();
        String email = binding.mailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if(name.equals("") || email.equals("") || password.equals("") || gender == null){
            Toast.makeText(this, "İsim, email, şifre ve cinsiyet giriniz.", Toast.LENGTH_SHORT).show();
        }else{
                userManager.createNewUser(name, email, password, gender, new UserManager.CreateUserCallback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

        }
    }


    public void toLogInClick(View view) {
        Intent intentToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intentToLogin);
        finish();
    }
}