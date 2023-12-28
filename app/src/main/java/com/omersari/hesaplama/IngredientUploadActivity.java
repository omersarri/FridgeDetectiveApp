package com.omersari.hesaplama;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omersari.hesaplama.databinding.ActivityIngredientUploadBinding;
import com.omersari.hesaplama.model.IngredientManager;


import java.util.HashMap;
import java.util.UUID;

public class IngredientUploadActivity extends AppCompatActivity {

    Uri imageData;

    private ActivityIngredientUploadBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    private IngredientManager ingredientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ingredientManager = IngredientManager.getInstance();
        registerLauncher();
    }

    public void uploadButtonClicked(View view) {
        String name = binding.nameEditText.getText().toString();
        ingredientManager.addIngredient(imageData, name, new IngredientManager.AddIngredientCallback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(IngredientUploadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(IngredientUploadActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void selectImage(View view) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
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

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        binding.imageView.setImageURI(imageData);
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
                    Toast.makeText(IngredientUploadActivity.this, "Permission needed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}