package com.omersari.hesaplama.bottomnavfragments;

import static android.content.ContentValues.TAG;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
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
import com.omersari.hesaplama.adapter.IngredientRecyclerViewInterface;
import com.omersari.hesaplama.adapter.IngredientsAdapter;
import com.omersari.hesaplama.databinding.FragmentFridgeBinding;
import com.omersari.hesaplama.model.Ingredient;

import java.util.ArrayList;
import java.util.Map;


public class FridgeFragment extends Fragment implements IngredientRecyclerViewInterface {
    FragmentFridgeBinding binding;

    FirebaseFirestore firebaseFirestore;

    ArrayList<Ingredient> ingredientArrayList;

    IngredientsAdapter ingredientsAdapter;

    FirebaseAuth auth;

    String email;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFridgeBinding.inflate(inflater,container,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        ingredientArrayList = new ArrayList<>();

        email = auth.getCurrentUser().getEmail();


        getData();
        floatingActionButton();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        ingredientsAdapter = new IngredientsAdapter(ingredientArrayList, (IngredientRecyclerViewInterface) FridgeFragment.this);
        binding.recyclerView.setAdapter(ingredientsAdapter);




        return binding.getRoot();
        // Inflate the layout for this fragment
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getData() {

        firebaseFirestore.collection("Users").document(email).collection("Ingredients").orderBy("recipeName", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        String downloadUrl = (String) data.get("downloadurl");

                        Ingredient ingredient = new Ingredient(id, name, downloadUrl);
                        ingredientArrayList.add(ingredient);

                    }
                    ingredientsAdapter.notifyDataSetChanged();
                }
            }
        });
    };

    private void deleteData(String id) {
        firebaseFirestore.collection("Users").document(email).collection("Ingredients").document(id)
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

    private void floatingActionButton() {
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                AddIngredientFragment addIngredientFragment = new AddIngredientFragment();

                transaction.replace(R.id.fragment_container, addIngredientFragment);
                transaction.addToBackStack(null); // Geri tuşuna basıldığında önceki Fragment'a dönmek için
                transaction.commit();
            }
        });
    }






    @Override
    public void deleteImageButtonClick(int position) {

        /*
        String name = ingredientArrayList.get(position).name;
        String downloadUrl = ingredientArrayList.get(position).downloadUrl;

        HashMap<String, Object> postData = new HashMap<>();
        postData.put("recipeName", name);
        postData.put("downloadurl", downloadUrl);
        postData.put("date", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Ingredients").document(name).set(postData);

         */
        deleteData(ingredientArrayList.get(position).id);
        getActivity().getSupportFragmentManager().beginTransaction().replace(FridgeFragment.this.getId(), new FridgeFragment()).commit();
        Toast.makeText(getActivity(), "Eklendi", Toast.LENGTH_LONG).show();
    }

    @Override
    public void addImageButtonClick(int position) {

    }


}