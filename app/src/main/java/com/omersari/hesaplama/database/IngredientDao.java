package com.omersari.hesaplama.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.omersari.hesaplama.model.ApiResponseRecipe;
import com.omersari.hesaplama.model.Ingredient;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface IngredientDao {

    @Query("SELECT * FROM Ingredient ORDER BY Ingredient.name ASC")
    Flowable<List<Ingredient>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Ingredient ingredient);

    @Delete
    Completable delete(Ingredient ingredient);



}