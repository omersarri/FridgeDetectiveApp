package com.omersari.hesaplama.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omersari.hesaplama.databinding.FavRecipeRowBinding;
import com.omersari.hesaplama.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.RecipeHolder> {
    private final FavoriteRecyclerViewInterface favoriteRecyclerViewInterface;



    public FavoritesAdapter(List<Recipe> recipeList, FavoriteRecyclerViewInterface favoriteRecyclerViewInterface) {
        this.recipeList = recipeList;
        this.favoriteRecyclerViewInterface = favoriteRecyclerViewInterface;
    }

    private List<Recipe> recipeList;

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavRecipeRowBinding favRecipeRowBinding = FavRecipeRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeHolder(favRecipeRowBinding, favoriteRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        holder.binding.textView1.setText(recipeList.get(position).getName());
        holder.binding.textView2.setText(recipeList.get(position).getPrepTime() + " Dakika Hazırlık");
        holder.binding.textView3.setText(recipeList.get(position).getCookTime() + " Dakika Pişirme");
        Picasso.get().load(recipeList.get(position).getDownloadUrl()).into(holder.binding.imageView);
        //holder.binding.cardView.setCardBackgroundColor(wordList.get(position).color);
        holder.binding.textView4.setText(recipeList.get(position).getServing() + " Kişilik");
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }


    public class RecipeHolder extends RecyclerView.ViewHolder {
        private FavRecipeRowBinding binding;
        public RecipeHolder(FavRecipeRowBinding binding, FavoriteRecyclerViewInterface favoriteRecyclerViewInterface) {
            super(binding.getRoot());
            this.binding = binding;

            binding.favDeleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(favoriteRecyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            favoriteRecyclerViewInterface.deleteImageButtonClick(pos);
                        }
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(favoriteRecyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            favoriteRecyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(favoriteRecyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            favoriteRecyclerViewInterface.onItemLongClick(pos);
                        }
                    }
                    return true;
                }
            });

        }
    }


}
