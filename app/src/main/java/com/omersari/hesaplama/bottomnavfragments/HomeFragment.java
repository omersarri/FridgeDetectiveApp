package com.omersari.hesaplama.bottomnavfragments;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.RecipeDetailsActivity;
import com.omersari.hesaplama.adapter.IngredientsAdapter;
import com.omersari.hesaplama.adapter.RecipeAdapter;
import com.omersari.hesaplama.adapter.RecipeRecyclerViewInterface;
import com.omersari.hesaplama.database.FirebaseHelper;
import com.omersari.hesaplama.database.LocalDataManager;
import com.omersari.hesaplama.databinding.FragmentHomeBinding;
import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.Recipe;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class HomeFragment extends Fragment implements RecipeRecyclerViewInterface {

    private RecipeAdapter recipeAdapter;
    private FirebaseFirestore firebaseFirestore;
    FragmentHomeBinding binding;
    ArrayList<Recipe> recipesArrayList;
    ArrayList<Ingredient> ingredientsArrayList;
    private FirebaseAuth auth;
    String email;
    LocalDataManager localDataManager;
    FirebaseHelper firebaseHelper;
    LocalDate date = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipesArrayList = new ArrayList<>();
        ingredientsArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        localDataManager = new LocalDataManager();
        firebaseHelper = new FirebaseHelper();
        ingredientsArrayList = firebaseHelper.getUserIngredients(getContext(),email);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.now();
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipeAdapter = new RecipeAdapter(recipesArrayList, (RecipeRecyclerViewInterface) HomeFragment.this);
        binding.recyclerView.setAdapter(recipeAdapter);

        getData();
        findButtonClick();

        return binding.getRoot();
    }




    private void dailyRecipe(ArrayList<Recipe> recipeArrayList) {
        Random rand = new Random();
        int randomNumber = rand.nextInt(recipeArrayList.size());


        //localDataManager.setSharedPreference(getActivity().getApplicationContext(), "date",date.toString());
        //localDataManager.setSharedPreference(getActivity().getApplicationContext(), "randNo", String.valueOf(randomNumber));

        String refDate = localDataManager.getSharedPreference(getActivity().getApplicationContext(),"date", "");
        String refRandomNo = localDataManager.getSharedPreference(getActivity().getApplicationContext(),"randNo", "");

        if(refDate.equals(date.toString())){
            binding.recipeNameTextView.setText(recipeArrayList.get(Integer.valueOf(refRandomNo)).name);
            binding.prepTimeTextView.setText(recipeArrayList.get(Integer.valueOf(refRandomNo)).prepTime + " Hazırlık");
            binding.cookTimeTextView.setText(recipeArrayList.get(Integer.valueOf(refRandomNo)).cookTime + " Pişirme");
            Picasso.get().load(recipeArrayList.get(Integer.valueOf(refRandomNo)).downloadUrl).into(binding.dailyImageView);
        } else{
            localDataManager.setSharedPreference(getActivity().getApplicationContext(), "date",date.toString());
            localDataManager.setSharedPreference(getActivity().getApplicationContext(), "randNo", String.valueOf(randomNumber));
        }
    }



    private void getData() {

        firebaseFirestore.collection("Recipes").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if(value != null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();
                        String id = snapshot.getId();
                        String name = (String) data.get("recipeName");
                        String ingredients = (String) data.get("ingredients");
                        String preparation = (String) data.get("preparation");
                        String prepTime = (String) data.get("prepTime");
                        String cookTime = (String) data.get("cookTime");
                        String downloadUrl = (String) data.get("downloadurl");

                        Recipe recipe = new Recipe(id,name,ingredients,preparation, prepTime,cookTime,downloadUrl);
                        recipesArrayList.add(recipe);

                    }
                    /*
                    int listSize = recipesArrayList.size();
                    Random rand = new Random();
                    int randomNumber = rand.nextInt(listSize);


                    //localDataManager.setSharedPreference(getActivity().getApplicationContext(), "date",date.toString());
                    //localDataManager.setSharedPreference(getActivity().getApplicationContext(), "randNo", String.valueOf(randomNumber));

                    String refDate = localDataManager.getSharedPreference(getActivity().getApplicationContext(),"date", "");
                    String refRandomNo = localDataManager.getSharedPreference(getActivity().getApplicationContext(),"randNo", "");

                        if(refDate.equals(date.toString())){
                            binding.recipeNameTextView.setText(recipesArrayList.get(Integer.valueOf(refRandomNo)).name);
                            binding.prepTimeTextView.setText(recipesArrayList.get(Integer.valueOf(refRandomNo)).prepTime + " Hazırlık");
                            binding.cookTimeTextView.setText(recipesArrayList.get(Integer.valueOf(refRandomNo)).cookTime + " Pişirme");
                            Picasso.get().load(recipesArrayList.get(Integer.valueOf(refRandomNo)).downloadUrl).into(binding.dailyImageView);
                        } else{
                            localDataManager.setSharedPreference(getActivity().getApplicationContext(), "date",date.toString());
                            localDataManager.setSharedPreference(getActivity().getApplicationContext(), "randNo", String.valueOf(randomNumber));
                        }

                     */
                    dailyRecipe(recipesArrayList);

                    recipeAdapter.notifyDataSetChanged();
                }
            }
        });
    };







    private void deleteData(String id) {
        firebaseFirestore.collection("Recipes").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }





    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
        intent.putExtra("recipeName",recipesArrayList.get(position).name);
        intent.putExtra("prepTime",recipesArrayList.get(position).prepTime);
        intent.putExtra("cookTime",recipesArrayList.get(position).cookTime);
        intent.putExtra("ingredients",recipesArrayList.get(position).ingredients);
        intent.putExtra("preparation",recipesArrayList.get(position).preparation);
        intent.putExtra("downloadUrl", recipesArrayList.get(position).downloadUrl);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void deleteImageButtonClick(int position) {
        deleteData(recipesArrayList.get(position).id);
        getActivity().getSupportFragmentManager().beginTransaction().replace(HomeFragment.this.getId(), new HomeFragment()).commit();
    }

    @Override
    public void favImageButtonClick(int position) {

        String id = recipesArrayList.get(position).id;
        String name = recipesArrayList.get(position).name;
        String ingredients = recipesArrayList.get(position).ingredients;
        String preparation = recipesArrayList.get(position).preparation;
        String prepTime = recipesArrayList.get(position).prepTime;
        String cookTime = recipesArrayList.get(position).cookTime;
        String downloadUrl = recipesArrayList.get(position).downloadUrl;
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        HashMap<String, Object> postData = new HashMap<>();
        postData.put("id", id);
        postData.put("recipeName", name);
        postData.put("ingredients", ingredients);
        postData.put("preparation", preparation);
        postData.put("prepTime", prepTime);
        postData.put("cookTime", cookTime);
        postData.put("downloadurl", downloadUrl);
        postData.put("date", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Users").document(email).collection("Favorites").document(id).set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                                    /*
                                    Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                     */
                Toast.makeText(getActivity(), "Succesfull", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void recipeDetailsAlert(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        final View customLayout = getLayoutInflater().inflate(R.layout.recipe_details_custom,null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        ImageView imageView = customLayout.findViewById(R.id.imageView2);
        TextView nameText = customLayout.findViewById(R.id.detailsNameText);
        TextView ingredientsText = customLayout.findViewById(R.id.detailsIngredientsText);
        TextView prepartionText = customLayout.findViewById(R.id.detailsPreparationText);

        Picasso.get().load(recipesArrayList.get(position).downloadUrl).into(imageView);
        nameText.setText(recipesArrayList.get(position).name);
        ingredientsText.setText(recipesArrayList.get(position).ingredients);
        prepartionText.setText(recipesArrayList.get(position).preparation);


        dialog.show();
    }

    public void findButtonClick(){

        binding.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sayac = 0;
                for(Recipe recipe : recipesArrayList){
                    String recipeIngredient = recipe.ingredients.toLowerCase();
                    for(Ingredient ingredient : ingredientsArrayList){
                        String ingredientName = ingredient.name.toLowerCase();
                        if(recipeIngredient.contains(ingredientName)){
                            //System.out.println(recipe.name +" "+ ingredient.name );
                            sayac++;
                        }else{
                            //System.out.println("yok" );
                        }
                    }
                    recipe.matchedIngredient = sayac;
                    recipeAdapter.notifyDataSetChanged();
                    sayac =0;
                }
            }
        });
    }

    class RecipeComparator implements java.util.Comparator<Recipe> {
        @Override
        public int compare(Recipe a, Recipe b) {
            return a.matchedIngredient - b.matchedIngredient;
        }
    }
}