package foodie.app.rubikkube.foodie.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import foodie.app.rubikkube.foodie.JavaUtils;
import foodie.app.rubikkube.foodie.R;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(this);

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(TestActivity.this, "I am click Listenere", Toast.LENGTH_SHORT).show();
//            }
//        });


    }

    @Override
    public void onClick(View v) {


        JavaUtils.checkUserId("here is the url copy function test\n" +
                "\n" +
                "http://34.220.151.44/user/3");


    }
}
