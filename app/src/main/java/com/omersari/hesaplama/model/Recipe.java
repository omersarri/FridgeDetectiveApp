package com.omersari.hesaplama.model;




//int id color category, string name meaning synonym exapmle type

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class Recipe implements Serializable {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;
    @SerializedName("recipeName")
    private String name;
    @SerializedName("preparation")
    private String preparation;
    @SerializedName("prepTime")
    private String prepTime;
    @SerializedName("cookTime")
    private String cookTime;


    @SerializedName("serving")
    private String serving;


    @SerializedName("ingredients")
    private String ingredients;
    @SerializedName("downloadUrl")
    private String downloadUrl;


    @Ignore
    private ArrayList<Ingredient> matchedIngredient = new ArrayList<>();
    private ArrayList<String> whoFavorited = new ArrayList<>();

    public Recipe() {


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }


    public ArrayList<String> getWhoFavorited() {
        return whoFavorited;
    }

    public void setWhoFavorited(ArrayList<String> whoFavorited) {
        this.whoFavorited = whoFavorited;
    }

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }

    public void setMatchedIngredient(ArrayList<Ingredient> matchedIngredient) {
        this.matchedIngredient = matchedIngredient;
    }

    public ArrayList<Ingredient> getMatchedIngredient() {
        return matchedIngredient;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Recipe other = (Recipe) obj;
        return id != null ? id.equals(other.id) : other.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }




}
