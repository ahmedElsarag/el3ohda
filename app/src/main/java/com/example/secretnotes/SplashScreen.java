package com.example.secretnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.secretnotes.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    Animation topAnim,bottomAnim;
    ActivitySplashScreenBinding binding;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);

        topAnim= AnimationUtils.loadAnimation(this,R.anim.top);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom);

        binding.imageView.setAnimation(topAnim);
        binding.textView.setAnimation(bottomAnim);
        binding.textView2.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getBoolean("LOGINSTATUS",false)){
                    Intent intent = new Intent(SplashScreen.this,ContainerActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },2000);

    }
}
