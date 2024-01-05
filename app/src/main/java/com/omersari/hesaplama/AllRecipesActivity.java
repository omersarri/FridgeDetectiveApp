package com.omersari.hesaplama;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omersari.hesaplama.adapter.RecipeAdapter;
import com.omersari.hesaplama.adapter.RecipeRecyclerViewInterface;
import com.omersari.hesaplama.bottomnavfragments.HomeFragment;
import com.omersari.hesaplama.database.IngredientDao;
import com.omersari.hesaplama.database.IngredientDatabase;
import com.omersari.hesaplama.database.LocalDataManager;
import com.omersari.hesaplama.databinding.ActivityAllRecipesBinding;
import com.omersari.hesaplama.model.IngredientManager;
import com.omersari.hesaplama.model.Recipe;
import com.omersari.hesaplama.model.RecipeManager;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class AllRecipesActivity extends AppCompatActivity implements RecipeRecyclerViewInterface{
    ActivityAllRecipesBinding binding;
    RecipeAdapter recipeAdapter;
    ArrayList<Recipe> recipeArrayList;
    private RecipeManager recipeManager;

    private FirebaseAuth auth;
    private String email;
    private ArrayList<Recipe> favorites;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllRecipesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        recipeManager = RecipeManager.getInstance();
        auth = FirebaseAuth.getInstance();

        email = auth.getCurrentUser().getEmail();
        firebaseFirestore = FirebaseFirestore.getInstance();

        favorites = recipeManager.getFavorites();




        Intent intent = getIntent();
        recipeArrayList = (ArrayList<Recipe>) intent.getSerializableExtra("allRecipes");
        System.out.println(recipeArrayList.size());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(AllRecipesActivity.this));
        recipeAdapter = new RecipeAdapter(recipeArrayList, favorites,   this);
        binding.recyclerView.setAdapter(recipeAdapter);


    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra("clickedRecipe",recipeArrayList.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void deleteImageButtonClick(int position) {
        recipeManager.deleteRecipe(recipeArrayList.get(position).getId(), new RecipeManager.DeleteRecipeCallback() {
            @Override
            public void onSuccess() {
                recipeAdapter.notifyItemRemoved(position);
                //getActivity().getSupportFragmentManager().beginTransaction().replace(HomeFragment.this.getId(), new HomeFragment()).commit();
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    @Override
    public void favImageButtonClick(int position) {

        if(favorites.contains(recipeArrayList.get(position))){
            recipeManager.deleteFavorite(this, recipeArrayList.get(position), new RecipeManager.DeleteRecipeCallback() {
                @Override
                public void onSuccess() {
                    favorites.remove(recipeArrayList.get(position));
                    recipeAdapter.notifyItemChanged(position);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(AllRecipesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            recipeManager.addFavorite(this, recipeArrayList.get(position), new RecipeManager.AddRecipeCallback() {
                @Override
                public void onSuccess() {
                    favorites.add(recipeArrayList.get(position));
                    recipeAdapter.notifyItemChanged(position);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(AllRecipesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

        /*
        Recipe clickedRecipe = recipeArrayList.get(position);
        ArrayList<String> whoFavorited = clickedRecipe.getWhoFavorited();

        if (whoFavorited.contains(email)) {
            whoFavorited.remove(email);

        } else {
            whoFavorited.add(email);
        }
        firebaseFirestore.collection("Recipes").document(clickedRecipe.getId()).update("whoFavorited", whoFavorited);
        recipeAdapter.notifyItemChanged(position);

         */
    }

    public void backButtonClick(View view) {
        finish();
    }
}