package foodie.app.rubikkube.foodie.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import foodie.app.rubikkube.foodie.R;
import foodie.app.rubikkube.foodie.classes.rvTimelineAdapter;

public class TimelineRV extends AppCompatActivity {

    //https://www.youtube.com/watch?v=IGGT_jfZQrA
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_rv);

        RecyclerView recyclerView = findViewById(R.id.rvTimeline);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // recyclerView.setAdapter(new rvTimelineAdapter(data));

    }
}
