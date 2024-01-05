package com.omersari.hesaplama.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.omersari.hesaplama.model.ApiResponseRecipe;
import com.omersari.hesaplama.model.Ingredient;

@Database(entities = {Ingredient.class}, version = 1)
@TypeConverters(StringListConverter.class)
public abstract class IngredientDatabase extends RoomDatabase {
    public abstract IngredientDao ingredientDao();
}
