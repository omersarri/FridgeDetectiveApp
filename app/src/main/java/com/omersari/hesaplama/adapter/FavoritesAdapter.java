package com.omersari.hesaplama.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omersari.hesaplama.databinding.FavRecipeRowBinding;
import com.omersari.hesaplama.databinding.RecipeRowBinding;
import com.omersari.hesaplama.model.Recipe;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.RecipeHolder> {
    private final RecyclerViewInterface recyclerViewInterface;



    public FavoritesAdapter(List<Recipe> recipeList, RecyclerViewInterface recyclerViewInterface) {
        this.recipeList = recipeList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    private List<Recipe> recipeList;

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavRecipeRowBinding favRecipeRowBinding = FavRecipeRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeHolder(favRecipeRowBinding, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        holder.binding.textView1.setText(recipeList.get(position).name);
        holder.binding.textView2.setText(recipeList.get(position).prepTime + "Preparation");
        holder.binding.textView3.setText(recipeList.get(position).cookTime + "Cooking");
        //holder.binding.cardView.setCardBackgroundColor(wordList.get(position).color);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }


    public class RecipeHolder extends RecyclerView.ViewHolder {
        private FavRecipeRowBinding binding;
        public RecipeHolder(FavRecipeRowBinding binding, RecyclerViewInterface recyclerViewInterface) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemLongClick(pos);
                        }
                    }
                    return true;
                }
            });

        }
    }


}
