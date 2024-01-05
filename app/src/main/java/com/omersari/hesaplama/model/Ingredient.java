package com.omersari.hesaplama.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class Ingredient implements Serializable {

    @PrimaryKey
    @NonNull
    @SerializedName("ingredientName")
    private String name;

    @SerializedName("downloadurl")
    private String downloadUrl;
    @SerializedName("whoAdded")
    private ArrayList<String> whoAdded = new ArrayList<>();


    public Ingredient() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public ArrayList<String> getWhoAdded() {
        return whoAdded;
    }

    public void setWhoAdded(ArrayList<String> whoAdded) {
        this.whoAdded = whoAdded;
    }


}

