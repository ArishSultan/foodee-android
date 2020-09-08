package foodie.app.rubikkube.foodie.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import foodie.app.rubikkube.foodie.R;

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
