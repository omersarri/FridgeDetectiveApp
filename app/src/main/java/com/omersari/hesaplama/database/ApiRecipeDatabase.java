package com.omersari.hesaplama.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.omersari.hesaplama.model.ApiResponseRecipe;

@Database(entities = {ApiResponseRecipe.class}, version = 1)
@TypeConverters(StringListConverter.class)
public abstract class ApiRecipeDatabase extends RoomDatabase {
    public abstract ApiRecipeDao apiRecipeDao();
}
