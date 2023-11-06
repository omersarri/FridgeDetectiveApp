package com.omersari.hesaplama.database;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class FirebaseHelper {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public ArrayList<Ingredient> getUserIngredients(Context context, String email) {
        ArrayList<Ingredient> ingredientsArrayList = new ArrayList<>();

        firebaseFirestore.collection("Users").document(email).collection("Ingredients").orderBy("recipeName", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if(value != null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();
                        String id = snapshot.getId();
                        String name = (String) data.get("recipeName");
                        String downloadUrl = (String) data.get("downloadurl");

                        Ingredient ingredient = new Ingredient(id, name, downloadUrl);
                        ingredientsArrayList.add(ingredient);

                    }
                }
            }

        });
        return ingredientsArrayList;
    }

    public ArrayList<Recipe> getRecipes(Context context) {
        ArrayList<Recipe> recipeArrayList = new ArrayList<>();
        firebaseFirestore.collection("Recipes").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                        recipeArrayList.add(recipe);

                    }
                }
            }
        });

        return recipeArrayList;
    }
}
