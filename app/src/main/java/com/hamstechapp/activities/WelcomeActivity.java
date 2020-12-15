package com.hamstechapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hamstechapp.R;
import com.hamstechapp.utils.UserDataConstants;

public class WelcomeActivity extends AppCompatActivity {

    TextView txtUserName;
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.wellcome_screen);
        initView();
    }

    private void initView(){
        txtUserName = findViewById(R.id.txtUserName);
        txtUserName.setText(UserDataConstants.userName);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
                WelcomeActivity.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
