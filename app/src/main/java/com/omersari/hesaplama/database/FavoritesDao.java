package com.omersari.hesaplama.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.omersari.hesaplama.model.Ingredient;
import com.omersari.hesaplama.model.Recipe;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface FavoritesDao {

    @Query("SELECT * FROM Recipe")
    Flowable<List<Recipe>> getAll();

    @Insert(onConflict = OnConflictStrategy.FAIL)
    Completable insert(Recipe recipe);

    @Delete
    Completable delete(Recipe recipe);



}