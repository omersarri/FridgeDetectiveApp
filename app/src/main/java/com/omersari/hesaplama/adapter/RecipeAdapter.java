package com.omersari.hesaplama.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.databinding.RecipeRowBinding;
import com.omersari.hesaplama.model.Recipe;
import com.omersari.hesaplama.model.UserManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {
    private final RecipeRecyclerViewInterface recyclerViewInterface;
    private UserManager userManager = UserManager.getInstance();
    private FirebaseAuth auth;



    public RecipeAdapter(ArrayList<Recipe> recipeList, ArrayList<Recipe> favorites, RecipeRecyclerViewInterface recyclerViewInterface) {
        this.recipeList = recipeList;
        this.recyclerViewInterface = recyclerViewInterface;
        this.favorites = favorites;
    }

    private final ArrayList<Recipe> recipeList;
    private final ArrayList<Recipe> favorites;

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecipeRowBinding recyclerRowBinding = RecipeRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        auth = FirebaseAuth.getInstance();
        return new RecipeHolder(recyclerRowBinding, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        holder.binding.textView1.setText(recipeList.get(position).getName());
        holder.binding.textView2.setText(recipeList.get(position).getPrepTime() + " Dakika Hazırlık");
        holder.binding.textView3.setText(recipeList.get(position).getCookTime() + " Dakika Pişirme");
        Picasso.get().load(recipeList.get(position).getDownloadUrl()).into(holder.binding.imageView);
        holder.binding.recipeMatched.setText(recipeList.get(position).getMatchedIngredient().size() + " Malzeme İle\nEşleşti");
        holder.binding.textView4.setText(recipeList.get(position).getServing()+ " Kişilik");

        //ArrayList<String> whoFavorited = recipeList.get(position).getWhoFavorited();

        if(auth.getCurrentUser().getEmail().equals("omersari@hotmail.com")) {
            holder.binding.deleteImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.binding.deleteImageButton.setVisibility(View.INVISIBLE);
        }
        /*
        if(whoFavorited != null){
            if(whoFavorited.contains(userManager.getCurrentUser().getEmail())){
                holder.binding.favDeleteImageButton.setImageResource(R.drawable.heart_fill_icon);
            }else{
                holder.binding.favDeleteImageButton.setImageResource(R.drawable.heart_icon);
            }
        }

         */

        System.out.println("favorites size: " + favorites.size());

        if(favorites.contains(recipeList.get(position))){
            System.out.println("içinde var");
            holder.binding.favDeleteImageButton.setImageResource(R.drawable.heart_fill_icon);

        }else{
            System.out.println("içinde yok");
            holder.binding.favDeleteImageButton.setImageResource(R.drawable.heart_icon);
        }





        /*
        for(Recipe recipe :favorites){
            if(recipe.getId().equals(recipeList.get(position).getId())){
                holder.binding.favDeleteImageButton.setImageResource(R.drawable.heart_fill_icon);
            }
        }

         */
        /*
        String favoritesNames = "";
        for (Recipe recipe : favorites){
            favoritesNames = favoritesNames + " "+ recipe.getId();
        }

        if(favoritesNames.contains(recipeList.get(position).getId())){
            holder.binding.favDeleteImageButton.setImageResource(R.drawable.heart_fill_icon);
        }else{
            holder.binding.favDeleteImageButton.setImageResource(R.drawable.heart_icon);
        }

         */




    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }


    public class RecipeHolder extends RecyclerView.ViewHolder {
        private RecipeRowBinding binding;

        public RecipeHolder(RecipeRowBinding binding, RecipeRecyclerViewInterface recyclerViewInterface) {
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

            itemView.findViewById(R.id.favDeleteImageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.favImageButtonClick(pos);
                        }
                    }
                }
            });


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
