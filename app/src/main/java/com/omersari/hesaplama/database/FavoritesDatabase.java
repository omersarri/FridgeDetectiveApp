package com.omersari.hesaplama.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.Recipe;

@Database(entities = {Recipe.class}, version = 1)
@TypeConverters(StringListConverter.class)
public abstract class FavoritesDatabase extends RoomDatabase {
    public abstract FavoritesDao favoritesDao();
}
