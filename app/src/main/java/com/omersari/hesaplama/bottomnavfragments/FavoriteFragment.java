package com.omersari.hesaplama.bottomnavfragments;


import android.app.AlertDialog;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.omersari.hesaplama.R;
import com.omersari.hesaplama.adapter.FavoritesAdapter;
import com.omersari.hesaplama.adapter.RecipeAdapter;
import com.omersari.hesaplama.adapter.RecyclerViewInterface;
import com.omersari.hesaplama.database.RecipeDao;
import com.omersari.hesaplama.database.RecipeDatabase;
import com.omersari.hesaplama.databinding.FragmentFavoriteBinding;
import com.omersari.hesaplama.model.Recipe;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FavoriteFragment extends Fragment implements RecyclerViewInterface {

    private FavoritesAdapter favoritesAdapter;
    private List<Recipe> recipeList;


    FragmentFavoriteBinding binding;
    private CompositeDisposable compositeDisposable =new CompositeDisposable();
    RecipeDatabase db;
    RecipeDao recipeDao;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater,container,false);

        db = Room.databaseBuilder(getActivity(),RecipeDatabase.class, "Recipe").build();

        recipeDao = db.recipeDao();

        getRecipes();
        return binding.getRoot();


    }

    public void getRecipes() {
        compositeDisposable.add(recipeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FavoriteFragment.this::handleResponse)
        );
    }


    private void handleResponse(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoritesAdapter = new FavoritesAdapter(recipeList, (RecyclerViewInterface) FavoriteFragment.this);
        binding.recyclerView2.setAdapter(favoritesAdapter);


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

    public void recipeDetailsAlert(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        final View customLayout = getLayoutInflater().inflate(R.layout.recipe_details_custom,null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        TextView nameText = customLayout.findViewById(R.id.detailsNameText);
        TextView ingredientsText = customLayout.findViewById(R.id.detailsIngredientsText);
        TextView prepartionText = customLayout.findViewById(R.id.detailsPreparationText);


        nameText.setText(recipeList.get(position).name);
        ingredientsText.setText(recipeList.get(position).ingredients);
        prepartionText.setText(recipeList.get(position).preparation);


        dialog.show();
    }
}