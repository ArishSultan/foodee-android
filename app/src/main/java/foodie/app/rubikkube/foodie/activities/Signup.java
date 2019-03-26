package foodie.app.rubikkube.foodie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import foodie.app.rubikkube.foodie.R;

public class Signup extends AppCompatActivity {

    ImageView ivSignpBg, ivSignpLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        initViews();

        TextView tvSuLogin = findViewById(R.id.tvSuLogin);

        tvSuLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in= new Intent(Signup.this, Login.class);
                startActivity(in);
                finish();
            }
        });

    }

    private void initViews()
    {
        ivSignpBg = findViewById(R.id.ivSignupBg);
        ivSignpLogo = findViewById(R.id.ivSignupLogo);

        Glide.with(this).load(R.drawable.bg_sml).into(ivSignpBg);
        Glide.with(this).load(R.drawable.logo_sml).into(ivSignpLogo);
    }
}
