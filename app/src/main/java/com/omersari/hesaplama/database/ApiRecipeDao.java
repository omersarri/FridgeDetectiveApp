package com.omersari.hesaplama.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.omersari.hesaplama.model.ApiResponseRecipe;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ApiRecipeDao {

    @Query("SELECT * FROM ApiResponseRecipe")
    Flowable<List<ApiResponseRecipe>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(ApiResponseRecipe recipe);

    @Delete
    Completable delete(ApiResponseRecipe recipe);



}