package com.omersari.hesaplama.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Ingredient implements Serializable {

    private String id;

    private String name;

    private String downloadUrl;
    private ArrayList<String> whoAdded = new ArrayList<>();


    public Ingredient() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

