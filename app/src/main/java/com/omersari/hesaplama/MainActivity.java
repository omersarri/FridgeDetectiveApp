package com.omersari.hesaplama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.omersari.hesaplama.bottomnavfragments.FridgeFragment;
import com.omersari.hesaplama.bottomnavfragments.HomeFragment;
import com.omersari.hesaplama.bottomnavfragments.ProfileFragment;
import com.omersari.hesaplama.bottomnavfragments.SearchFragment;

import com.omersari.hesaplama.databinding.ActivityMainBinding;
import com.omersari.hesaplama.model.RecipeManager;
import com.omersari.hesaplama.model.UserManager;


import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private UserManager userManager;
    private RecipeManager recipeManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        userManager = UserManager.getInstance();
        recipeManager = RecipeManager.getInstance();

        recipeManager.getFavorites(this, new RecipeManager.AddRecipeCallback() {
            @Override
            public void onSuccess() {
                System.out.println("veriler çekildi");
                getUserInfo();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                getUserInfo();
            }
        });











    }

    public void getUserInfo() {
        userManager.getUserInfo(new UserManager.GetUserInfoCallback() {
            @Override
            public void onSuccess() {
                openFragment(new HomeFragment());
                bottomNavBarSelector();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bottomNavBarSelector() {
        binding.bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.isChecked()) {
                            return false; // Zaten seçiliyse bir şey yapma
                        }

                        int itemId = item.getItemId();

                        if (itemId == R.id.bottom_home) {
                            openFragment(new HomeFragment());
                        } else if (itemId == R.id.bottom_search) {
                            openFragment(new SearchFragment());
                        } else if (itemId == R.id.bottom_fridge) {
                            openFragment(new FridgeFragment());
                        } else if (itemId == R.id.bottom_profile) {
                            openFragment(new ProfileFragment());
                        }

                        item.setChecked(true);
                        return true;
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

}