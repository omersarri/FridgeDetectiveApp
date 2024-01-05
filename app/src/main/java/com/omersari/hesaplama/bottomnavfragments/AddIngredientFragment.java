package com.omersari.hesaplama.bottomnavfragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.room.Room;

import android.util.Log;
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
import com.omersari.hesaplama.adapter.IngredientsAdapter;
import com.omersari.hesaplama.database.IngredientDao;
import com.omersari.hesaplama.database.IngredientDatabase;
import com.omersari.hesaplama.databinding.FragmentAddIngredientBinding;
import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.IngredientManager;
import com.omersari.hesaplama.model.User;
import com.omersari.hesaplama.model.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AddIngredientFragment extends Fragment implements IngredientRecyclerViewInterface{

    FragmentAddIngredientBinding binding;


    ArrayList<Ingredient> ingredientList;
    ArrayList<Ingredient> myIngredientList;

    AddIngredientsAdapter addIngredientsAdapter;
    IngredientDatabase ingredientDatabase;
    IngredientDao ingredientDao;
    CompositeDisposable compositeDisposable = new CompositeDisposable();



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
        myIngredientList = new ArrayList<>();
        ingredientDatabase = Room.databaseBuilder(getActivity(),IngredientDatabase.class, "Ingredient").build();
        ingredientDao = ingredientDatabase.ingredientDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddIngredientBinding.inflate(inflater, container, false);

        getMyIngredients();
        floatingActionButtonClick();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        addIngredientsAdapter = new AddIngredientsAdapter(ingredientList, (IngredientRecyclerViewInterface) AddIngredientFragment.this);
        binding.recyclerView.setAdapter(addIngredientsAdapter);


        return binding.getRoot();
    }

    private void getMyIngredients() {
        compositeDisposable.add(ingredientDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(AddIngredientFragment.this::handleResponse));
    }

    private void handleResponse(List<Ingredient> ingredientList) {
        if(myIngredientList.size() == 0){
            myIngredientList.addAll(ingredientList);
            getData();
        }


    }

    private void getData() {

        ingredientManager.getIngredients(new IngredientManager.GetIngredientsCallback() {
            @Override
            public void onSuccess(ArrayList<Ingredient> ingredientArrayList) {
                    ingredientList.addAll(ingredientArrayList);
                    for(int i=0; i<ingredientArrayList.size();i++) {
                        for(Ingredient ingredient : myIngredientList) {
                            if (Objects.equals(ingredientArrayList.get(i).getName(), ingredient.getName())) {
                                ingredientList.remove(ingredientArrayList.get(i));
                            }
                        }
                    }




                addIngredientsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }

    /* getData only firebase
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

     */

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
        compositeDisposable.add(ingredientDao.insert(ingredientList.get(position))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            myIngredientList.add(ingredientList.get(position));
                            // This block is called on success
                            ingredientList.remove(position);
                            addIngredientsAdapter.notifyItemRemoved(position);
                            Log.d("RxJava", "Delete successful");
                        },
                        throwable -> {
                            // This block is called on error
                            Log.e("RxJava", "Delete failed", throwable);
                        }
                ));
    }

    /*
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

     */


}