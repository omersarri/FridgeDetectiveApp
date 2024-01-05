package com.omersari.hesaplama.model;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omersari.hesaplama.MainActivity;
import com.omersari.hesaplama.UploadActivity;
import com.omersari.hesaplama.bottomnavfragments.SearchFragment;
import com.omersari.hesaplama.database.ApiRecipeDatabase;
import com.omersari.hesaplama.database.FavoritesDao;
import com.omersari.hesaplama.database.FavoritesDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecipeManager {
    private static RecipeManager instance;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private FavoritesDao favoritesDao;
    private FavoritesDatabase favoritesDatabase;
    private CompositeDisposable compositeDisposable;

    private ArrayList<Recipe> favorites = new ArrayList<>();

    private RecipeManager() {

    }

    public interface AddRecipeCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface GetRecipesCallback {
        void onSuccess(ArrayList<Recipe> recipeArrayList);
        void onFailure(String errorMessage);
    }
    public interface DeleteRecipeCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public interface UpdateRecipeCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }


    public void addRecipe(Uri imageData, String name, String ingredients, String preparation, String prepTime, String cookTime, String serving, AddRecipeCallback addRecipeCallback) {
        if(imageData != null) {

            //universal unique id
            UUID uuid = UUID.randomUUID();
            String imageName = "recipeImages/" + uuid + ".jpg";
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Download url
                    StorageReference newReference = firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("recipeName", name);
                            postData.put("ingredients", ingredients);
                            postData.put("preparation", preparation);
                            postData.put("prepTime", prepTime);
                            postData.put("cookTime", cookTime);
                            postData.put("serving", serving);
                            postData.put("downloadurl", downloadUrl);
                            postData.put("whoFavorited", new ArrayList<>());
                            postData.put("date", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Recipes").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    addRecipeCallback.onSuccess();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    addRecipeCallback.onFailure(e.getLocalizedMessage());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    addRecipeCallback.onFailure(e.getLocalizedMessage());
                }
            });
        }
    }

    public void deleteRecipe(String recipeId, DeleteRecipeCallback deleteRecipeCallback) {

        firebaseFirestore.collection("Recipes").document(recipeId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteRecipeCallback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       deleteRecipeCallback.onFailure(e.getLocalizedMessage());
                    }
                });

    }
    public void getRecipes(GetRecipesCallback getRecipesCallback) {

        ArrayList<Recipe> recipeArrayList = new ArrayList<>();
        firebaseFirestore.collection("Recipes").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> data = document.getData();
                                String id = document.getId();
                                String name = (String) data.get("recipeName");
                                String ingredients = (String) data.get("ingredients");
                                String preparation = (String) data.get("preparation");
                                String prepTime = (String) data.get("prepTime");
                                String cookTime = (String) data.get("cookTime");
                                String serving = (String) data.get("serving");
                                String downloadUrl = (String) data.get("downloadurl");
                                ArrayList<String> whoFavorited = (ArrayList<String>) data.get("whoFavorited");

                                Recipe recipe = new Recipe();
                                recipe.setId(id);
                                recipe.setName(name);
                                recipe.setIngredients(ingredients);
                                recipe.setPreparation(preparation);
                                recipe.setPrepTime(prepTime);
                                recipe.setCookTime(cookTime);
                                recipe.setServing(serving);
                                recipe.setDownloadUrl(downloadUrl);
                                recipe.setWhoFavorited(whoFavorited);

                                recipeArrayList.add(recipe);

                            }
                            getRecipesCallback.onSuccess(recipeArrayList);
                        } else {
                            getRecipesCallback.onFailure("Error getting documents.");
                        }

                    }
                });
    }

    public void updateRecipe(String recipeId, String recipeField, Object data, UpdateRecipeCallback updateRecipeCallback){
        firebaseFirestore.collection("Recipes").document(recipeId).update(recipeField, data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateRecipeCallback.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateRecipeCallback.onFailure(e.getLocalizedMessage());
            }
        });

    }

    public void addFavorite(Context context, Recipe recipe, AddRecipeCallback addRecipeCallback) {
        favoritesDatabase = Room.databaseBuilder(context, FavoritesDatabase.class, "Recipes").build();
        favoritesDao = favoritesDatabase.favoritesDao();

        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(favoritesDao.insert(recipe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () ->{
                            addRecipeCallback.onSuccess();
                            //favorites.add(recipe);
                        },
                        throwable -> addRecipeCallback.onFailure("Error while adding favorite")));

    }

    public void getFavorites(Context context, AddRecipeCallback addRecipeCallback) {
        favoritesDatabase = Room.databaseBuilder(context, FavoritesDatabase.class, "Recipes").build();
        favoritesDao = favoritesDatabase.favoritesDao();

        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(favoritesDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        recipeList ->{
                            favorites.addAll(recipeList);
                            addRecipeCallback.onSuccess();
                        },
                        throwable -> addRecipeCallback.onFailure("Error while fetching recipes")
                        ));

    }

    public void deleteFavorite(Context context, Recipe recipe, DeleteRecipeCallback deleteRecipeCallback) {
        favoritesDatabase = Room.databaseBuilder(context, FavoritesDatabase.class, "Recipes").build();
        favoritesDao = favoritesDatabase.favoritesDao();

        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(favoritesDao.delete(recipe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () ->{
                            deleteRecipeCallback.onSuccess();
                            //favorites.remove(recipe);
                        },
                        throwable -> deleteRecipeCallback.onFailure("Error while deleting favorite")));

    }




    public ArrayList<Recipe> getFavorites(){
        return favorites;
    }







    public static synchronized RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }
}
