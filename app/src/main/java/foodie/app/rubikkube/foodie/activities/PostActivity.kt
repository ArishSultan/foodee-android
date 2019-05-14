package foodie.app.rubikkube.foodie.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import foodie.app.rubikkube.foodie.R
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        back_icon.setOnClickListener {
            finish()
        }
    }
}
