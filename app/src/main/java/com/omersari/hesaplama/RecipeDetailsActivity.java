package com.omersari.hesaplama;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.omersari.hesaplama.databinding.ActivityRecipeDetailsBinding;
import com.omersari.hesaplama.databinding.ActivitySignupBinding;
import com.squareup.picasso.Picasso;

public class RecipeDetailsActivity extends AppCompatActivity {
    ActivityRecipeDetailsBinding binding;
    String name;
    String prepTime;
    String cookTime;
    String ingredients;
    String preparation;
    String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle intentData = getIntent().getExtras();
        name = intentData.getString("recipeName");
        prepTime = intentData.getString("prepTime");
        cookTime = intentData.getString("cookTime");
        ingredients = intentData.getString("ingredients");
        preparation = intentData.getString("preparation");
        downloadUrl = intentData.getString("downloadUrl");

        binding.detailsNameText.setText(name);
        binding.detailsIngredientsText.setText(ingredients);
        binding.detailsPreparationText.setText(preparation);
        binding.prepTimeTextView.setText(prepTime + " Dakika Hazırlık");
        binding.cookTimeTextView.setText(cookTime + " Dakika Pişirme");

        Picasso.get().load(downloadUrl).into(binding.imageView2);
    }

    public void speakButtonClick(View view) {

    }


}