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
import com.omersari.hesaplama.model.IngredientManager;
import com.omersari.hesaplama.model.User;
import com.omersari.hesaplama.model.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddIngredientFragment extends Fragment implements IngredientRecyclerViewInterface{

    FragmentAddIngredientBinding binding;


    ArrayList<Ingredient> ingredientList;

    AddIngredientsAdapter addIngredientsAdapter;



    private IngredientManager ingredientManager;
    private UserManager userManager;
    private User currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredientManager =  IngredientManager.getInstance();
        userManager = UserManager.getInstance();
        currentUser = userManager.getCurrentUser();
        ingredientList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddIngredientBinding.inflate(inflater, container, false);

        getData();
        floatingActionButtonClick();


        return binding.getRoot();
    }


    private void getData() {

        ingredientManager.getIngredients(new IngredientManager.GetIngredientsCallback() {
            @Override
            public void onSuccess(ArrayList<Ingredient> ingredientArrayList) {
                for(int i=0; i<ingredientArrayList.size();i++) {
                    if(!ingredientArrayList.get(i).getWhoAdded().contains(currentUser.getEmail())){
                        ingredientList.add(ingredientArrayList.get(i));
                    }
                }
                binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                addIngredientsAdapter = new AddIngredientsAdapter(ingredientList, (IngredientRecyclerViewInterface) AddIngredientFragment.this);
                binding.recyclerView.setAdapter(addIngredientsAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }

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
        Ingredient clickedIngredient = ingredientList.get(position);
        ArrayList<String> whoAdded = clickedIngredient.getWhoAdded();
        if(!whoAdded.contains(currentUser.getEmail())){
            whoAdded.add(currentUser.getEmail());
        }

        ingredientManager.updateIngredient(clickedIngredient.getName(), "whoAdded", whoAdded, new IngredientManager.UpdateIngredientCallback() {
            @Override
            public void onSuccess() {
                ingredientList.remove(clickedIngredient);
                addIngredientsAdapter.notifyItemRemoved(position);
                Toast.makeText(getActivity(), "Eklendi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


}