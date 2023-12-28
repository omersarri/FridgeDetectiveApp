package com.omersari.hesaplama.bottomnavfragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omersari.hesaplama.IngredientUploadActivity;
import com.omersari.hesaplama.LoginActivity;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.RecipeDetailsActivity;
import com.omersari.hesaplama.UploadActivity;
import com.omersari.hesaplama.adapter.FavoriteRecyclerViewInterface;
import com.omersari.hesaplama.adapter.FavoritesAdapter;
import com.omersari.hesaplama.databinding.ActivityUploadBinding;
import com.omersari.hesaplama.databinding.FragmentProfileBinding;
import com.omersari.hesaplama.model.Recipe;
import com.omersari.hesaplama.model.RecipeManager;
import com.omersari.hesaplama.model.User;
import com.omersari.hesaplama.model.UserManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.internal.cache.DiskLruCache;

public class ProfileFragment extends Fragment implements FavoriteRecyclerViewInterface{
    FragmentProfileBinding binding;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    private FavoritesAdapter favoritesAdapter;
    private ArrayList<Recipe> recipeList;

    private FirebaseAuth auth;
    private RecipeManager recipeManager;
    private UserManager userManager;
    private User currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        recipeList = new ArrayList<>();
        recipeManager = RecipeManager.getInstance();
        userManager = UserManager.getInstance();
        currentUser = userManager.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater,container,false);
        binding.textView9.setText(currentUser.getName());
        Picasso.get().load(currentUser.getProfilePic()).into(binding.userProfilePic);
        if(currentUser.getEmail().equals("omersari@hotmail.com") ){
            binding.button2.setVisibility(View.VISIBLE);
            binding.button3.setVisibility(View.VISIBLE);
        }else{
            binding.button2.setVisibility(View.INVISIBLE);
            binding.button3.setVisibility(View.INVISIBLE);
        }





        getFavorites();

        editProfilePicButtonClick();
        registerLauncher();
        addIngredientButtonClick();
        addRecipeButtonClick();
        logoutButtonClick();

        return binding.getRoot();
    }




    private void getFavorites() {
        recipeManager.getRecipes(new RecipeManager.GetRecipesCallback() {
            @Override
            public void onSuccess(ArrayList<Recipe> recipeArrayList) {
                for(int i = 0; i< recipeArrayList.size(); i++){
                    if(recipeArrayList.get(i).getWhoFavorited().contains(currentUser.getEmail())){
                        recipeList.add(recipeArrayList.get(i));
                    }
                }
                binding.recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
                favoritesAdapter = new FavoritesAdapter(recipeList, (FavoriteRecyclerViewInterface) ProfileFragment.this);
                binding.recyclerView2.setAdapter(favoritesAdapter);

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    };

    private void addIngredientButtonClick() {
        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToUpload = new Intent(getActivity(), IngredientUploadActivity.class);
                startActivity(intentToUpload);
            }
        });
    }
    private void addRecipeButtonClick() {
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToUpload = new Intent(getActivity(), UploadActivity.class);
                startActivity(intentToUpload);
            }
        });
    }
    private void logoutButtonClick() {
        binding.logoutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();

                Intent intentToLogin = new Intent(getActivity(), LoginActivity.class);
                startActivity(intentToLogin);
                getActivity().finish();
            }
        });
    }
    private void editProfilePicButtonClick() {
        binding.editProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //ask permission
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
                    }else{
                        //ask permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }else{
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
            }
        });
    }



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
        Recipe clickedRecipe = recipeList.get(position);
        ArrayList<String> whoFavorited = clickedRecipe.getWhoFavorited();
        whoFavorited.remove(currentUser.getEmail());

        recipeManager.updateRecipe(clickedRecipe.getId(), "whoFavorited", whoFavorited, new RecipeManager.UpdateRecipeCallback() {
            @Override
            public void onSuccess() {
                recipeList.remove(clickedRecipe);
                favoritesAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectImage(View view) {


    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        userManager.uploadProfilePic(imageData, new UserManager.UploadProfilePicCallback() {
                            @Override
                            public void onSuccess() {
                                binding.userProfilePic.setImageURI(imageData);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(getActivity(), "Permission needed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}