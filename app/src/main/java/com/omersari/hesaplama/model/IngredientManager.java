package com.omersari.hesaplama.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IngredientManager {
    private static IngredientManager instance;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();

    private IngredientManager() {

    }

    public interface AddIngredientCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface GetIngredientsCallback {
        void onSuccess(ArrayList<Ingredient> ingredientArrayList);
        void onFailure(String errorMessage);
    }
    public interface UpdateIngredientCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }


    public void addIngredient(Uri imageData, String name, AddIngredientCallback addIngredientCallback) {
        if(imageData != null) {

            String imageName = "ingedientImages/" + name + ".jpg";
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
                            postData.put("ingredientName", name);
                            postData.put("downloadurl", downloadUrl);
                            postData.put("whoAdded", new ArrayList<>());
                            postData.put("date", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Ingredients").document(name).set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    addIngredientCallback.onSuccess();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    addIngredientCallback.onFailure(e.getLocalizedMessage());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    addIngredientCallback.onFailure(e.getLocalizedMessage());
                }
            });
        }
    }

    public void getIngredients(GetIngredientsCallback getIngredientsCallback) {

        ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();
        firebaseFirestore.collection("Ingredients").orderBy("ingredientName", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> data = document.getData();
                                String name = (String) data.get("ingredientName");
                                String downloadUrl = (String) data.get("downloadurl");
                                ArrayList<String> whoAdded = (ArrayList<String>) data.get("whoAdded");

                                Ingredient ingredient = new Ingredient();
                                ingredient.setName(name);
                                ingredient.setDownloadUrl(downloadUrl);
                                ingredient.setWhoAdded(whoAdded);


                                ingredientArrayList.add(ingredient);

                            }
                            getIngredientsCallback.onSuccess(ingredientArrayList);
                        } else {
                            getIngredientsCallback.onFailure("Error getting documents.");
                        }

                    }
                });
    }

    public void getUserIngredients(String email, GetIngredientsCallback getIngredientsCallback) {

        ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();
        firebaseFirestore.collection("Ingredients").whereArrayContains("whoAdded", email).orderBy("ingredientName", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> data = document.getData();
                                String name = (String) data.get("ingredientName");
                                String downloadUrl = (String) data.get("downloadurl");
                                ArrayList<String> whoAdded = (ArrayList<String>) data.get("whoAdded");

                                Ingredient ingredient = new Ingredient();

                                ingredient.setName(name);
                                ingredient.setDownloadUrl(downloadUrl);
                                ingredient.setWhoAdded(whoAdded);


                                ingredientArrayList.add(ingredient);

                            }
                            getIngredientsCallback.onSuccess(ingredientArrayList);
                        } else {
                            getIngredientsCallback.onFailure("Error getting documents.");
                        }

                    }
                });
    }

    public void updateIngredient(String ingredientName, String recipeField, Object data, UpdateIngredientCallback updateIngredientCallback) {
        firebaseFirestore.collection("Ingredients").document(ingredientName).update(recipeField, data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateIngredientCallback.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateIngredientCallback.onFailure(e.getLocalizedMessage());
            }
        });
    }












    public static synchronized IngredientManager getInstance() {
        if (instance == null) {
            instance = new IngredientManager();
        }
        return instance;
    }
}
