package com.omersari.hesaplama.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

//int id color category, string name meaning synonym exapmle type
@Entity
public class Recipe implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "preparation")
    public String preparation;

    @ColumnInfo(name = "prepTime")
    public int prepTime;
    @ColumnInfo(name = "cookTime")
    public int cookTime;

    @ColumnInfo(name = "ingredients")
    public String ingredients;




    public Recipe(String name, String preparation, int prepTime, int cookTime, String ingredients) {
        this.name = name;
        this.preparation = preparation;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.ingredients = ingredients;

    }
}
