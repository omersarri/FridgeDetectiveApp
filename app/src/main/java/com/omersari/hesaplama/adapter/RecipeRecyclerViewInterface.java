package com.omersari.hesaplama.adapter;

public interface RecipeRecyclerViewInterface {
    void onItemClick(int position);
    void onItemLongClick(int position);
    void deleteImageButtonClick( int position);
    void favImageButtonClick(int position);
}
