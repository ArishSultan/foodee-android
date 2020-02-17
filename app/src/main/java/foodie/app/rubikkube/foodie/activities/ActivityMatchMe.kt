package foodie.app.rubikkube.foodie.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import foodie.app.rubikkube.foodie.R
import kotlinx.android.synthetic.main.activity_match_me.*

class ActivityMatchMe : AppCompatActivity() {


    var title_toolbar: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_me)


        title_toolbar = toolbar_id!!.findViewById(R.id.toolbar_title)
        title_toolbar!!.text = "Match Me Results..."
    }
}
