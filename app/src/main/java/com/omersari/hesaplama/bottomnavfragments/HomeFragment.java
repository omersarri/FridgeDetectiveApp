package com.omersari.hesaplama.bottomnavfragments;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omersari.hesaplama.AllRecipesActivity;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.RecipeDetailsActivity;
import com.omersari.hesaplama.adapter.RecipeAdapter;
import com.omersari.hesaplama.adapter.RecipeRecyclerViewInterface;
import com.omersari.hesaplama.database.IngredientDao;
import com.omersari.hesaplama.database.IngredientDatabase;
import com.omersari.hesaplama.database.LocalDataManager;
import com.omersari.hesaplama.database.NetworkUtils;
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
import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HomeFragment extends Fragment implements RecipeRecyclerViewInterface {

    private RecipeAdapter recipeAdapter;
    private FragmentHomeBinding binding;
    private ArrayList<Recipe> matchedRecipeList;
    private ArrayList<Recipe> allRecipeList;
    private ArrayList<Recipe> favorites;
    private ArrayList<Ingredient> ingredientArrayList;
    private LocalDataManager localDataManager;
    private LocalDate date = null;
    private String refRandomNo;
    private RecipeManager recipeManager;
    private IngredientManager ingredientManager;

    private FirebaseAuth auth;
    private String email;
    FirebaseFirestore firebaseFirestore;
    IngredientDatabase ingredientDatabase;
    IngredientDao ingredientDao;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matchedRecipeList = new ArrayList<>();
        allRecipeList = new ArrayList<>();

        ingredientArrayList = new ArrayList<>();
        ingredientManager = IngredientManager.getInstance();
        recipeManager = RecipeManager.getInstance();
        auth = FirebaseAuth.getInstance();
        favorites = recipeManager.getFavorites();
        System.out.println("favorites size home:"+ favorites.size());

        email = auth.getCurrentUser().getEmail();
        ingredientDatabase = Room.databaseBuilder(getActivity(),IngredientDatabase.class, "Ingredient").build();
        ingredientDao = ingredientDatabase.ingredientDao();


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
        binding.infoText.setText("Eşleşen tarif bulunmuyor ya da dolabınız boş!");
        binding.infoText.setVisibility(View.VISIBLE);

        binding.dailyImageView.setVisibility(View.INVISIBLE);
        binding.recipeNameTextView.setVisibility(View.INVISIBLE);
        binding.prepTimeTextView.setVisibility(View.INVISIBLE);
        binding.cookTimeTextView.setVisibility(View.INVISIBLE);
        binding.servingTextView.setVisibility(View.INVISIBLE);

        getMyIngredients();
        seeAllButtonClick();






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
                binding.servingTextView.setText(recipeArrayList.get(Integer.valueOf(refRandomNo)).getServing() + " Kişilik");
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
                Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
                intent.putExtra("clickedRecipe",allRecipeList.get(Integer.valueOf(refRandomNo)));
                startActivity(intent);

            }

        });



    }
    private void getMyIngredients() {
        compositeDisposable.add(ingredientDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HomeFragment.this::handleResponse));
    }

    private void handleResponse(List<Ingredient> ingredientList) {
        if(ingredientArrayList.size() == 0){
            ingredientArrayList.addAll(ingredientList);
            getRecipes();
        }



    }

    // get user ingredients from firebase
    /*
    private void getUserIngredients() {
        ingredientManager.getUserIngredients(email, new IngredientManager.GetIngredientsCallback() {
            @Override
            public void onSuccess(ArrayList<Ingredient> ingredientArrayList) {
                ingredientList.addAll(ingredientArrayList) ;
                getRecipes();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });



    };
    */

    private void getRecipes() {
        recipeManager.getRecipes(new RecipeManager.GetRecipesCallback() {
            @Override
            public void onSuccess(ArrayList<Recipe> recipeArrayList) {

                allRecipeList.addAll(recipeArrayList);
                for(Recipe recipe : recipeArrayList) {
                    ArrayList<Ingredient> matchedIngredients = new ArrayList<>();
                    for(Ingredient ingredient : ingredientArrayList){
                        if(recipe.getIngredients().toLowerCase().contains(ingredient.getName().toLowerCase())){
                            matchedIngredients.add(ingredient);
                        }
                    }
                    recipe.setMatchedIngredient(matchedIngredients);
                }

                for (Recipe recipe : recipeArrayList){
                    if(recipe.getMatchedIngredient().size() != 0){
                        matchedRecipeList.add(recipe);
                    }
                }
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recipeAdapter = new RecipeAdapter(matchedRecipeList, favorites, HomeFragment.this);
                binding.recyclerView.setAdapter(recipeAdapter);
                dailyRecipe(allRecipeList);
                binding.recipeNameTextView.setVisibility(View.VISIBLE);
                binding.prepTimeTextView.setVisibility(View.VISIBLE);
                binding.cookTimeTextView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.servingTextView.setVisibility(View.VISIBLE);
                binding.dailyImageView.setVisibility(View.VISIBLE);
                if(matchedRecipeList.size() != 0){
                    binding.infoText.setVisibility(View.INVISIBLE);
                }else{
                    binding.infoText.setVisibility(View.VISIBLE);
                }



                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                System.out.println(errorMessage);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }















    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
        intent.putExtra("clickedRecipe",matchedRecipeList.get(position));
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void deleteImageButtonClick(int position) {
        if(NetworkUtils.isNetworkAvailable(getActivity())){
            recipeManager.deleteRecipe(matchedRecipeList.get(position).getId(), new RecipeManager.DeleteRecipeCallback() {
                @Override
                public void onSuccess() {
                    matchedRecipeList.remove(position);
                    allRecipeList.remove(matchedRecipeList.get(position));
                    recipeAdapter.notifyItemRemoved(position);
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(HomeFragment.this.getId(), new HomeFragment()).commit();
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        }else{
            Toast.makeText(getActivity(), "Silebilmek için çevrimiçi olun!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void favImageButtonClick(int position) {

        if(favorites.contains(matchedRecipeList.get(position))){
            recipeManager.deleteFavorite(getActivity(), matchedRecipeList.get(position), new RecipeManager.DeleteRecipeCallback() {
                @Override
                public void onSuccess() {
                    favorites.remove(matchedRecipeList.get(position));
                    recipeAdapter.notifyItemChanged(position);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            recipeManager.addFavorite(getActivity(), matchedRecipeList.get(position), new RecipeManager.AddRecipeCallback() {
                @Override
                public void onSuccess() {
                    favorites.add(matchedRecipeList.get(position));
                    recipeAdapter.notifyItemChanged(position);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }





        /*
        if(NetworkUtils.isNetworkAvailable(getActivity())){
            Recipe clickedRecipe = matchedRecipeList.get(position);
            ArrayList<String> whoFavorited = clickedRecipe.getWhoFavorited();

            if (whoFavorited.contains(email)) {
                whoFavorited.remove(email);

            } else {
                whoFavorited.add(email);
            }
            firebaseFirestore.collection("Recipes").document(clickedRecipe.getId()).update("whoFavorited", whoFavorited);
            recipeAdapter.notifyItemChanged(position);
        }else{
            Toast.makeText(getActivity(), "Favoriler için çevrimiçi olun!", Toast.LENGTH_SHORT).show();
        }

         */
    }

    private void seeAllButtonClick() {
        binding.seeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllRecipesActivity.class);
                intent.putExtra("allRecipes", allRecipeList);
                startActivity(intent);
            }
        });
    }


}