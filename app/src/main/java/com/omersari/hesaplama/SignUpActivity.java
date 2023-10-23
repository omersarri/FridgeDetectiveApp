package com.omersari.hesaplama;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.omersari.hesaplama.databinding.ActivityAddIngredientBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivityAddIngredientBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddIngredientBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }





}