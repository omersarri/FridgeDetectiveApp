package com.omersari.hesaplama.bottomnavfragments;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.adapter.RecipeAdapter;
import com.omersari.hesaplama.adapter.RecipeRecyclerViewInterface;
import com.omersari.hesaplama.databinding.FragmentHomeBinding;
import com.omersari.hesaplama.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class HomeFragment extends Fragment implements RecipeRecyclerViewInterface {

    private RecipeAdapter recipeAdapter;
    private FirebaseFirestore firebaseFirestore;

    FragmentHomeBinding binding;
    private CompositeDisposable compositeDisposable =new CompositeDisposable();

    ArrayList<Recipe> recipesArrayList;

    private FirebaseAuth auth;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);

        recipesArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipeAdapter = new RecipeAdapter(recipesArrayList, (RecipeRecyclerViewInterface) HomeFragment.this);
        binding.recyclerView.setAdapter(recipeAdapter);




        return binding.getRoot();
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
        compositeDisposable.clear();
    }

    @Override
    public void onItemClick(int position) {
        recipeDetailsAlert(position);
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

        String name = recipesArrayList.get(position).name;
        String ingredients = recipesArrayList.get(position).ingredients;
        String preparation = recipesArrayList.get(position).preparation;
        String prepTime = recipesArrayList.get(position).prepTime;
        String cookTime = recipesArrayList.get(position).cookTime;
        String downloadUrl = recipesArrayList.get(position).downloadUrl;
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        HashMap<String, Object> postData = new HashMap<>();
        postData.put("recipeName", name);
        postData.put("ingredients", ingredients);
        postData.put("preparation", preparation);
        postData.put("prepTime", prepTime);
        postData.put("cookTime", cookTime);
        postData.put("downloadurl", downloadUrl);
        postData.put("date", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Users").document(email).collection("Favorites").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
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

        TextView nameText = customLayout.findViewById(R.id.detailsNameText);
        TextView ingredientsText = customLayout.findViewById(R.id.detailsIngredientsText);
        TextView prepartionText = customLayout.findViewById(R.id.detailsPreparationText);


        nameText.setText(recipesArrayList.get(position).name);
        ingredientsText.setText(recipesArrayList.get(position).ingredients);
        prepartionText.setText(recipesArrayList.get(position).preparation);


        dialog.show();
    }
}