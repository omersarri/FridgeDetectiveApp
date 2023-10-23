package com.omersari.hesaplama.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.databinding.AddIngredientRowBinding;
import com.omersari.hesaplama.databinding.IngredientRowBinding;
import com.omersari.hesaplama.model.Ingredient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AddIngredientsAdapter extends RecyclerView.Adapter<AddIngredientsAdapter.IngredientHolder> {
    private final IngredientRecyclerViewInterface recyclerViewInterface;
    private FirebaseAuth auth;



    public AddIngredientsAdapter(ArrayList<Ingredient> ingredientList, IngredientRecyclerViewInterface recyclerViewInterface) {
        this.ingredientList = ingredientList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    private ArrayList<Ingredient> ingredientList;

    @NonNull
    @Override
    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddIngredientRowBinding addIngredientRowBinding = AddIngredientRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        auth = FirebaseAuth.getInstance();
        return new IngredientHolder(addIngredientRowBinding, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        holder.binding.gridItemText.setText(ingredientList.get(position).name);
        Picasso.get().load(ingredientList.get(position).downloadUrl).into(holder.binding.gridItemImage);
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
        private AddIngredientRowBinding binding;

        public IngredientHolder(AddIngredientRowBinding binding, IngredientRecyclerViewInterface recyclerViewInterface) {
            super(binding.getRoot());
            this.binding = binding;



            itemView.findViewById(R.id.addImageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.addImageButtonClick(pos);
                        }
                    }
                }
            });



        }
    }




}
