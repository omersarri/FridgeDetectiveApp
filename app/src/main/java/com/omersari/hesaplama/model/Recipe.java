package com.omersari.hesaplama.model;




//int id color category, string name meaning synonym exapmle type

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {

    private String id;
    private String name;
    private String preparation;
    private String prepTime;
    private String cookTime;



    private String serving;


    private String ingredients;
    private String downloadUrl;



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






}
