package com.omersari.hesaplama.model;




//int id color category, string name meaning synonym exapmle type

public class Recipe {
    public String id;
    public String name;
    public String preparation;
    public String prepTime;
    public String cookTime;

    public String ingredients;
    public String downloadUrl;




    public Recipe(String id,String name, String ingredients,String preparation, String prepTime, String cookTime, String downloadUrl) {
        this.id = id;
        this.name = name;
        this.preparation = preparation;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.ingredients = ingredients;
        this.downloadUrl = downloadUrl;

    }
}
