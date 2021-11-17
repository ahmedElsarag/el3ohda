package com.example.secretnotes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.SurfaceControl;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.secretnotes.databinding.ActivityCountainerBinding;

public class ContainerActivity extends AppCompatActivity implements View.OnClickListener,NavigationHost{

    ActivityCountainerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_countainer);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        binding.addBtn.setOnClickListener(this);
        binding.favouritBtn.setOnClickListener(this);
        binding.homeBtn.setOnClickListener(this);

        if (!isNetworkAvailable()){
            FavouritFragment fragment = new FavouritFragment();
            navigateTo(fragment,false);
        }
    }

    @Override
    public void onClick(View view) {
        Fragment selectedFragment = null;
        switch (view.getId()){
            case R.id.home_btn:{
                selectedFragment = new HomeFragment();
                break;
            }
            case R.id.favourit_btn:{
                selectedFragment = new FavouritFragment();
                break;
            }
            case R.id.add_btn:{
                selectedFragment = new AddNotesFragment();
                Variables.isAdd=true;
                break;
            }
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {

        FragmentTransaction transaction =
                getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,fragment);
        if (addToBackstack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) ContainerActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (cap == null) return false;
            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            for (Network n: networks) {
                NetworkInfo nInfo = cm.getNetworkInfo(n);
                if (nInfo != null && nInfo.isConnected()) return true;
            }
        } else {
            NetworkInfo[] networks = cm.getAllNetworkInfo();
            for (NetworkInfo nInfo: networks) {
                if (nInfo != null && nInfo.isConnected()) return true;
            }
        }

        return false;
    }
}
