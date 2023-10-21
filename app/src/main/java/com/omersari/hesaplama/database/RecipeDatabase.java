package com.omersari.hesaplama.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.omersari.hesaplama.model.Recipe;


@Database(entities = {Recipe.class}, version = 1)
public abstract class RecipeDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
}
