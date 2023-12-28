package com.omersari.hesaplama.model;




//int id color category, string name meaning synonym exapmle type

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Recipe {

    private String id;
    private String name;
    private String preparation;
    private String prepTime;
    private String cookTime;


    private String ingredients;
    private String downloadUrl;
    private int matchedIngredient=0;
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

    public int getMatchedIngredient() {
        return matchedIngredient;
    }

    public void setMatchedIngredient(int matchedIngredient) {
        this.matchedIngredient = matchedIngredient;
    }

    public ArrayList<String> getWhoFavorited() {
        return whoFavorited;
    }

    public void setWhoFavorited(ArrayList<String> whoFavorited) {
        this.whoFavorited = whoFavorited;
    }






}
