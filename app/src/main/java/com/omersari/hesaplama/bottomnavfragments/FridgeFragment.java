package com.omersari.hesaplama.bottomnavfragments;

import static android.content.ContentValues.TAG;


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
import com.omersari.hesaplama.database.IngredientDao;
import com.omersari.hesaplama.database.IngredientDatabase;
import com.omersari.hesaplama.database.NetworkUtils;
import com.omersari.hesaplama.databinding.FragmentFridgeBinding;
import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.IngredientManager;
import com.omersari.hesaplama.model.User;
import com.omersari.hesaplama.model.UserManager;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FridgeFragment extends Fragment implements IngredientRecyclerViewInterface {
    FragmentFridgeBinding binding;

    ArrayList<Ingredient> ingredientArrayList;

    IngredientsAdapter ingredientsAdapter;

    IngredientDatabase ingredientDatabase;
    IngredientDao ingredientDao;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private IngredientManager ingredientManager;
    private UserManager userManager;
    private User currentUser;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredientManager = IngredientManager.getInstance();
        userManager = UserManager.getInstance();
        currentUser = userManager.getCurrentUser();
        ingredientDatabase = Room.databaseBuilder(getActivity(),IngredientDatabase.class, "Ingredient").build();
        ingredientDao = ingredientDatabase.ingredientDao();
        ingredientArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFridgeBinding.inflate(inflater,container,false);
        binding.infoText.setText("Dolabınız Boş!");
        binding.infoText.setVisibility(View.VISIBLE);

        getMyIngredients();


        //getData(); //getData from firebase
        floatingActionButton();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        ingredientsAdapter = new IngredientsAdapter(ingredientArrayList, (IngredientRecyclerViewInterface) FridgeFragment.this);
        binding.recyclerView.setAdapter(ingredientsAdapter);






        return binding.getRoot();
        // Inflate the layout for this fragment
    }


    private void getMyIngredients() {
        compositeDisposable.add(ingredientDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FridgeFragment.this::handleResponse));
    }

    private void handleResponse(List<Ingredient> ingredientList) {
        if(ingredientList.size() == 0){
            binding.infoText.setVisibility(View.VISIBLE);
        }else if(ingredientArrayList.size() == 0){
            binding.infoText.setVisibility(View.GONE);
            ingredientArrayList.addAll(ingredientList);
            ingredientsAdapter.notifyDataSetChanged();
        }



    }

    private void floatingActionButton() {

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtils.isNetworkAvailable(getActivity())){
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    AddIngredientFragment addIngredientFragment = new AddIngredientFragment();

                    transaction.replace(R.id.fragment_container, addIngredientFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else{
                    Toast.makeText(getActivity(), "Dolabınıza malzeme eklemek için çevrimiçi olun!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void deleteImageButtonClick(int position) {
        compositeDisposable.add(ingredientDao.delete(ingredientArrayList.get(position))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            // This block is called on success

                            ingredientArrayList.remove(position);
                            ingredientsAdapter.notifyItemRemoved(position);
                            //ingredientsAdapter.notifyItemRangeChanged(position, ingredientArrayList.size());

                            Log.d("RxJava", "Delete successful");
                        },
                        throwable -> {
                            // This block is called on error
                            Log.e("RxJava", "Delete failed", throwable);
                        }
                ));
    }

    @Override
    public void addImageButtonClick(int position) {

    }
    /*

    private void getData() {

        ingredientManager.getIngredients(new IngredientManager.GetIngredientsCallback() {
            @Override
            public void onSuccess(ArrayList<Ingredient> ingredientArrayList) {
                for(int i = 0; i< ingredientArrayList.size(); i++){
                    if(ingredientArrayList.get(i).getWhoAdded().contains(currentUser.getEmail())){
                        ingredientArrayList.add(ingredientArrayList.get(i));
                    }
                }
                binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
                ingredientsAdapter = new IngredientsAdapter(ingredientArrayList, (IngredientRecyclerViewInterface) FridgeFragment.this);
                binding.recyclerView.setAdapter(ingredientsAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void deleteImageButtonClick(int position) {
        Ingredient clickedIngredient = ingredientList.get(position);
        ArrayList<String> whoAdded = clickedIngredient.getWhoAdded();
        whoAdded.remove(currentUser.getEmail());

        ingredientManager.updateIngredient(clickedIngredient.getName(), "whoAdded", whoAdded, new IngredientManager.UpdateIngredientCallback() {
            @Override
            public void onSuccess() {
                ingredientList.remove(clickedIngredient);
                ingredientsAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

    }

    @Override
    public void addImageButtonClick(int position) {

    }

     */


}