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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.adapter.FavoritesAdapter;
import com.omersari.hesaplama.adapter.FavoriteRecyclerViewInterface;

import com.omersari.hesaplama.adapter.RecipeAdapter;
import com.omersari.hesaplama.adapter.RecipeRecyclerViewInterface;
import com.omersari.hesaplama.databinding.FragmentFavoriteBinding;
import com.omersari.hesaplama.model.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class FavoriteFragment extends Fragment implements FavoriteRecyclerViewInterface {

    private FavoritesAdapter favoritesAdapter;
    private ArrayList<Recipe> recipesArrayList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    String email;


    FragmentFavoriteBinding binding;
    private CompositeDisposable compositeDisposable =new CompositeDisposable();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater,container,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recipesArrayList = new ArrayList<>();
        getData();
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoritesAdapter = new FavoritesAdapter(recipesArrayList, (FavoriteRecyclerViewInterface) FavoriteFragment.this);
        binding.recyclerView2.setAdapter(favoritesAdapter);



        //getRecipes();
        return binding.getRoot();


    }

    private void getData() {
        email = auth.getCurrentUser().getEmail();
        firebaseFirestore.collection("Users").document(email).collection("Favorites").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    favoritesAdapter.notifyDataSetChanged();
                }
            }
        });
    };


    private void deleteData(String id) {
        firebaseFirestore.collection("Users").document(email).collection("Favorites").document(id)
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
        getActivity().getSupportFragmentManager().beginTransaction().replace(FavoriteFragment.this.getId(), new FavoriteFragment()).commit();
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