package com.omersari.hesaplama;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.omersari.hesaplama.databinding.ActivityRecipeDetailsBinding;
import com.omersari.hesaplama.databinding.ActivitySignupBinding;
import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeDetailsActivity extends AppCompatActivity {
    private ActivityRecipeDetailsBinding binding;
    private Recipe clickedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle intentData = getIntent().getExtras();
        clickedRecipe = (Recipe) intentData.getSerializable("clickedRecipe");


        String fullText = clickedRecipe.getIngredients();
        ArrayList<Ingredient> ingredientArrayList = clickedRecipe.getMatchedIngredient();

        SpannableString spannableString = new SpannableString(fullText);
        for (Ingredient ingredient : ingredientArrayList) {

            int startIndex = fullText.toLowerCase().indexOf(ingredient.getName().toLowerCase());

            if (startIndex != -1) {
                int endIndex = startIndex + ingredient.getName().length();

                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);

                spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }

        binding.detailsNameText.setText(clickedRecipe.getName());
        binding.detailsIngredientsText.setText(spannableString);
        binding.detailsPreparationText.setText(clickedRecipe.getPreparation());
        binding.prepTimeTextView.setText(clickedRecipe.getPrepTime() + " Dakika Hazırlık");
        binding.cookTimeTextView.setText(clickedRecipe.getCookTime() + " Dakika Pişirme");
        binding.servingTextView.setText(clickedRecipe.getServing() + " Kişilik");

        Picasso.get().load(clickedRecipe.getDownloadUrl()).into(binding.imageView2);

    }




}