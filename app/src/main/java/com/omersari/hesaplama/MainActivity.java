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
import com.omersari.hesaplama.model.UserManager;


import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private UserManager userManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        userManager = UserManager.getInstance();


        userManager.getUserInfo(new UserManager.GetUserInfoCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });



        openFragment(new HomeFragment());
        bottomNavBarSelector();




    }

    public void bottomNavBarSelector() {
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

}