package com.omersari.hesaplama.bottomnavfragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.adapter.AddIngredientsAdapter;
import com.omersari.hesaplama.adapter.IngredientRecyclerViewInterface;
import com.omersari.hesaplama.databinding.FragmentAddIngredientBinding;
import com.omersari.hesaplama.model.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddIngredientFragment extends Fragment implements IngredientRecyclerViewInterface{

    FragmentAddIngredientBinding binding;

    FirebaseFirestore firebaseFirestore;

    ArrayList<Ingredient> ingredientArrayList;

    AddIngredientsAdapter addIngredientsAdapter;

    FirebaseAuth auth;

    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddIngredientBinding.inflate(inflater, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        ingredientArrayList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        addIngredientsAdapter = new AddIngredientsAdapter(ingredientArrayList, (IngredientRecyclerViewInterface) AddIngredientFragment.this);
        binding.recyclerView.setAdapter(addIngredientsAdapter);
        getData();
        floatingActionButtonClick();
        return binding.getRoot();


    }


    private void getData() {
        firebaseFirestore.collection("Ingredients").orderBy("recipeName", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    addIngredientsAdapter.notifyDataSetChanged();
                }
            }
        });
    };

    public void floatingActionButtonClick() {
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                FridgeFragment fridgeFragment = new FridgeFragment();

                transaction.replace(R.id.fragment_container, fridgeFragment);
                transaction.commit();

            }
        });
    }



    @Override
    public void deleteImageButtonClick(int position) {

    }

    @Override
    public void addImageButtonClick(int position) {
        String name = ingredientArrayList.get(position).name;
        String downloadUrl = ingredientArrayList.get(position).downloadUrl;
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        HashMap<String, Object> postData = new HashMap<>();
        postData.put("recipeName", name);
        postData.put("downloadurl", downloadUrl);
        postData.put("date", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Users").document(email).collection("Ingredients").document(name).set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Eklendi", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}