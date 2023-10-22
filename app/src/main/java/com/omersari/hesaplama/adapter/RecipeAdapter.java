package com.omersari.hesaplama.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.omersari.hesaplama.R;
import com.omersari.hesaplama.databinding.RecipeRowBinding;
import com.omersari.hesaplama.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {
    private final RecipeRecyclerViewInterface recyclerViewInterface;
    private FirebaseAuth auth;

    //public RecipeAdapter(ArrayList<Recipe> postArrayList) {
     //   this.recipeArrayList = postArrayList;
    //}


    public RecipeAdapter(ArrayList<Recipe> recipeList, RecipeRecyclerViewInterface recyclerViewInterface) {
        this.recipeList = recipeList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    private ArrayList<Recipe> recipeList;

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecipeRowBinding recyclerRowBinding = RecipeRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        auth = FirebaseAuth.getInstance();
        return new RecipeHolder(recyclerRowBinding, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        holder.binding.textView1.setText(recipeList.get(position).name);
        holder.binding.textView2.setText(recipeList.get(position).prepTime + " Dakika Hazırlama");
        holder.binding.textView3.setText(recipeList.get(position).cookTime + " Dakika Pişirme");
        Picasso.get().load(recipeList.get(position).downloadUrl).into(holder.binding.imageView);

        if(auth.getCurrentUser().getEmail().equals("omersari@hotmail.com")) {
            holder.binding.deleteImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.binding.deleteImageButton.setVisibility(View.INVISIBLE);
        }



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
