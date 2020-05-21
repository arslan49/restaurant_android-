package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.foodmanagment.R;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;

public class SplashActivity extends AppCompatActivity {

    SystemPrefs systemPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SystemUtils.setActivity(this);
        systemPrefs = new SystemPrefs(SystemUtils.getActivity());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (systemPrefs.isLogin()){
                    startActivity(new Intent(SystemUtils.getActivity(),HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                }else {
                    startActivity(new Intent(SystemUtils.getActivity(), LoginActivity.class));
                }
                finish();
            }
        }, 2000);
    }
}
