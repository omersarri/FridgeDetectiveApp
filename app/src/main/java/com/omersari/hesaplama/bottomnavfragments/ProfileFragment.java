package com.omersari.hesaplama.bottomnavfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.omersari.hesaplama.LoginActivity;
import com.omersari.hesaplama.UploadActivity;
import com.omersari.hesaplama.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;

    private FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater,container,false);
        auth = FirebaseAuth.getInstance();

        binding.textView9.setText(auth.getCurrentUser().getEmail().toString());

        if(auth.getCurrentUser().getEmail().equals("omersari@hotmail.com") ){
            binding.button2.setVisibility(View.VISIBLE);
        }else{
            binding.button2.setVisibility(View.INVISIBLE);
        }
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();

                Intent intentToLogin = new Intent(getActivity(), LoginActivity.class);
                startActivity(intentToLogin);
                getActivity().finish();
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToUpload = new Intent(getActivity(), UploadActivity.class);
                startActivity(intentToUpload);
            }
        });



        return binding.getRoot();
    }
}