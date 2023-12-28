package com.omersari.hesaplama.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.databinding.IngredientRowBinding;
import com.omersari.hesaplama.databinding.RecipeRowBinding;
import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientHolder> {
    private final IngredientRecyclerViewInterface recyclerViewInterface;
    private FirebaseAuth auth;



    public IngredientsAdapter(ArrayList<Ingredient> ingredientList, IngredientRecyclerViewInterface recyclerViewInterface) {
        this.ingredientList = ingredientList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    private ArrayList<Ingredient> ingredientList;

    @NonNull
    @Override
    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IngredientRowBinding ingredientRowBinding = IngredientRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        auth = FirebaseAuth.getInstance();
        return new IngredientHolder(ingredientRowBinding, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        holder.binding.gridItemText.setText(ingredientList.get(position).getName());
        Picasso.get().load(ingredientList.get(position).getDownloadUrl()).into(holder.binding.gridItemImage);
        /*
        if(auth.getCurrentUser().getEmail().equals("omersari@hotmail.com")) {
            holder.binding.deleteImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.binding.deleteImageButton.setVisibility(View.INVISIBLE);
        }

         */



    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }


    public class IngredientHolder extends RecyclerView.ViewHolder {
        private IngredientRowBinding binding;

        public IngredientHolder(IngredientRowBinding binding, IngredientRecyclerViewInterface recyclerViewInterface) {
            super(binding.getRoot());
            this.binding = binding;



            itemView.findViewById(R.id.deleteImageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.deleteImageButtonClick(pos);
                        }
                    }
                }
            });



        }
    }




}
