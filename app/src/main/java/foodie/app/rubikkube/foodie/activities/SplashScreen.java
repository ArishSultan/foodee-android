package foodie.app.rubikkube.foodie.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import foodie.app.rubikkube.foodie.R;

public class SplashScreen extends AppCompatActivity {

    ImageView ivBG, ivLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initViews();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                Intent in = new Intent(SplashScreen.this, Login.class);
                startActivity(in);
                finish();
            }
        },4000);
    }

    private void initViews() {
        ivBG = findViewById(R.id.ivBG);
        ivLogo = findViewById(R.id.ivLogo);
        Glide.with(this).load(R.drawable.splash).into(ivBG);
        Glide.with(this).load(R.drawable.logo_main).into(ivLogo);

    }
}
