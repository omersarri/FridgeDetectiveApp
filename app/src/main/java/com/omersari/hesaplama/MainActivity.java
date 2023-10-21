package com.omersari.hesaplama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationBarView;
import com.omersari.hesaplama.bottomnavfragments.FavoriteFragment;
import com.omersari.hesaplama.bottomnavfragments.FridgeFragment;
import com.omersari.hesaplama.bottomnavfragments.HomeFragment;
import com.omersari.hesaplama.bottomnavfragments.ProfileFragment;
import com.omersari.hesaplama.bottomnavfragments.SearchFragment;
import com.omersari.hesaplama.database.RecipeDao;
import com.omersari.hesaplama.database.RecipeDatabase;
import com.omersari.hesaplama.databinding.ActivityMainBinding;
import com.omersari.hesaplama.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    RecipeDao recipeDao;

    RecipeDatabase db;

    private final CompositeDisposable compositeDisposable =new CompositeDisposable();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db = Room.databaseBuilder(this, RecipeDatabase.class, "Recipe").build();
        recipeDao = db.recipeDao();



        Recipe recipe1 = new Recipe("Sulu Köfte", "Önce köfteleri hazırlayalım tepsiye biraz un serpelim.\n" +
                "Kıymaya soğanı rendeleyelim bulguru ,karabiberi ,tuzu ve kimyonu ekleyip yoğuralım.\n" +
                "Küçük köfteler yapıp unlanmış tepsiye koyalım.\n" +
                "Tepsiyi sallayalım.\n" +
                "Tencereye yağları alıp salçayı ekleyip kavuralım.\n" +
                "Tencerenin yarısına kadar sıcak su ekleyelim.\n" +
                "Doğranmış patates ve havuçları ekleyip 5 dakika pişirelim.\n" +
                "Ardından köfteleri ekleyip suyu özleşene kadar orta ateşte pişirelim enson kuru nane serpelim (afiyet olsun).", 30, 35, "300 gram kıyma\n" +
                "1 soğan\n" +
                "2 yemek kaşığı bulgur\n" +
                "Karabiber kimyon\n" +
                "Tuz\n" +
                "Biraz un (Tepsiye serpmek için)\n" +
                "4 patates\n" +
                "3 havuç\n" +
                "1 yemek kaşığı salça\n" +
                "Yarım çay bardağından az sıvı yağ\n" +
                "1 yemek kaşığı tereyağ\n" +
                "Kuru nane");

        compositeDisposable.add(recipeDao.insert(recipe1)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe());



        openFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(R.id.bottom_home == item.getItemId()){
                            openFragment(new HomeFragment());
                            item.setChecked(true);
                        } else if(R.id.bottom_search == item.getItemId()){
                            openFragment(new SearchFragment());
                            item.setChecked(true);
                        } else if(R.id.bottom_favorites == item.getItemId()){
                            openFragment(new FavoriteFragment());
                            item.setChecked(true);
                        } else if(R.id.bottom_fridge == item.getItemId()){
                            openFragment(new FridgeFragment());
                            item.setChecked(true);
                        } else if(R.id.bottom_profile == item.getItemId()){
                            openFragment(new ProfileFragment());
                            item.setChecked(true);
                        }
                        return false;
                    }
                }
        );


    }





    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager1.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}