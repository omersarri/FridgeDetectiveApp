package com.omersari.hesaplama.model;




//int id color category, string name meaning synonym exapmle type

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class ApiResponseRecipe implements Serializable {



    @PrimaryKey
    @NonNull
    @SerializedName("recipeName")
    private String name;
    @SerializedName("preparation")
    private String preparation;
    @SerializedName("preparationTime")
    private String prepTime;
    @SerializedName("cookTime")
    private String cookTime;


    @SerializedName("serving")
    private String serving;


    @SerializedName("ingredients")

    private ArrayList<String> ingredients;


    public ApiResponseRecipe() {


    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }







}
