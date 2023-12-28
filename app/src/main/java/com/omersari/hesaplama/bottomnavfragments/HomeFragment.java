package com.omersari.hesaplama.bottomnavfragments;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.RecipeDetailsActivity;
import com.omersari.hesaplama.adapter.RecipeAdapter;
import com.omersari.hesaplama.adapter.RecipeRecyclerViewInterface;
import com.omersari.hesaplama.database.LocalDataManager;
import com.omersari.hesaplama.databinding.FragmentHomeBinding;
import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.IngredientManager;
import com.omersari.hesaplama.model.Recipe;
import com.omersari.hesaplama.model.RecipeManager;
import com.omersari.hesaplama.model.User;
import com.omersari.hesaplama.model.UserManager;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class HomeFragment extends Fragment implements RecipeRecyclerViewInterface {

    private RecipeAdapter recipeAdapter;
    private FragmentHomeBinding binding;
    private ArrayList<Recipe> recipeList;
    private ArrayList<Ingredient> ingredientList;
    private LocalDataManager localDataManager;
    private LocalDate date = null;
    private String refRandomNo;
    private RecipeManager recipeManager;
    private IngredientManager ingredientManager;
    private UserManager userManager;
    private User currentUser;
    FirebaseFirestore firebaseFirestore;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeList = new ArrayList<>();
        ingredientList = new ArrayList<>();
        ingredientManager = IngredientManager.getInstance();
        recipeManager = RecipeManager.getInstance();
        userManager = UserManager.getInstance();
        currentUser = userManager.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        localDataManager = new LocalDataManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.now();
        }


        binding.dailyImageView.setVisibility(View.INVISIBLE);
        binding.recipeNameTextView.setVisibility(View.INVISIBLE);
        binding.prepTimeTextView.setVisibility(View.INVISIBLE);
        binding.cookTimeTextView.setVisibility(View.INVISIBLE);

        getData();
        findButtonClick();


        return binding.getRoot();
    }





    private void dailyRecipe(ArrayList<Recipe> recipeArrayList) {
        if(recipeArrayList.size() != 0){
            Random rand = new Random();
            int randomNumber = rand.nextInt(recipeArrayList.size());


            String refDate = localDataManager.getSharedPreference(getActivity().getApplicationContext(),"date", "");
            refRandomNo = localDataManager.getSharedPreference(getActivity().getApplicationContext(),"randNo", "");

            if(refDate.equals(date.toString())){
                binding.recipeNameTextView.setText(recipeArrayList.get(Integer.valueOf(refRandomNo)).getName());
                binding.prepTimeTextView.setText(recipeArrayList.get(Integer.valueOf(refRandomNo)).getPrepTime() + " Dakika Hazırlık");
                binding.cookTimeTextView.setText(recipeArrayList.get(Integer.valueOf(refRandomNo)).getCookTime() + " Dakika Pişirme");
                Picasso.get().load(recipeArrayList.get(Integer.valueOf(refRandomNo)).getDownloadUrl()).into(binding.dailyImageView);
            } else{
                localDataManager.setSharedPreference(getActivity().getApplicationContext(), "date",date.toString());
                localDataManager.setSharedPreference(getActivity().getApplicationContext(), "randNo", String.valueOf(randomNumber));
            }
        }else{

        }
        binding.dailyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(Integer.valueOf(refRandomNo));

            }

        });



    }



    private void getData() {
        recipeManager.getRecipes(new RecipeManager.GetRecipesCallback() {
            @Override
            public void onSuccess(ArrayList<Recipe> recipeArrayList) {
                recipeList = recipeArrayList;
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recipeAdapter = new RecipeAdapter(recipeList, (RecipeRecyclerViewInterface) HomeFragment.this);
                binding.recyclerView.setAdapter(recipeAdapter);
                dailyRecipe(recipeList);
                binding.dailyImageView.setVisibility(View.VISIBLE);

                binding.recipeNameTextView.setVisibility(View.VISIBLE);
                binding.prepTimeTextView.setVisibility(View.VISIBLE);
                binding.cookTimeTextView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
        ingredientManager.getIngredients(new IngredientManager.GetIngredientsCallback() {
            @Override
            public void onSuccess(ArrayList<Ingredient> ingredientArrayList) {
                ingredientList = ingredientArrayList;
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    };











    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
        intent.putExtra("recipeName",recipeList.get(position).getName());
        intent.putExtra("prepTime",recipeList.get(position).getPrepTime());
        intent.putExtra("cookTime",recipeList.get(position).getCookTime());
        intent.putExtra("ingredients",recipeList.get(position).getIngredients());
        intent.putExtra("preparation",recipeList.get(position).getPreparation());
        intent.putExtra("downloadUrl", recipeList.get(position).getDownloadUrl());
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void deleteImageButtonClick(int position) {
        recipeManager.deleteRecipe(recipeList.get(position).getId(), new RecipeManager.DeleteRecipeCallback() {
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
        Recipe clickedRecipe = recipeList.get(position);
        ArrayList<String> whoFavorited = clickedRecipe.getWhoFavorited();

        if (whoFavorited.contains(currentUser.getEmail())) {
            whoFavorited.remove(currentUser.getEmail());

        } else {
            whoFavorited.add(currentUser.getEmail());
        }
        firebaseFirestore.collection("Recipes").document(clickedRecipe.getId()).update("whoFavorited", whoFavorited);
        recipeAdapter.notifyItemChanged(position);
    }


    public void findButtonClick(){

        binding.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sayac = 0;
                for(Recipe recipe : recipeList){
                    String recipeIngredient = recipe.getIngredients().toLowerCase();
                    for(Ingredient ingredient : ingredientList){
                        String ingredientName = ingredient.getName().toLowerCase();
                        if(recipeIngredient.contains(ingredientName)){
                            //System.out.println(recipe.name +" "+ ingredient.name );
                            sayac++;
                        }else{
                            //System.out.println("yok" );
                        }
                    }
                    recipe.setMatchedIngredient(sayac);
                    recipeAdapter.notifyDataSetChanged();
                    sayac =0;
                }
            }
        });
    }

    class RecipeComparator implements java.util.Comparator<Recipe> {
        @Override
        public int compare(Recipe a, Recipe b) {
            return a.getMatchedIngredient() - b.getMatchedIngredient();
        }
    }
}