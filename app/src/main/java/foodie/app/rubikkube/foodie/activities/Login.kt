package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import foodie.app.rubikkube.foodie.R
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {

//    internal var ivLoginBG: ImageView ?= null
//    internal var ivLoginLogo: ImageView ?= null
//    internal var loginBtn: Button ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        loginBtn = findViewById(R.id.btnLogin)

        btnLogin?.setOnClickListener { startActivity(Intent(this@Login, HomeActivity::class.java)) }

        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        initViews()

        tvSignUp.setOnClickListener {
            val `in` = Intent(this@Login, Signup::class.java)
            startActivity(`in`)
        }

    }

    private fun initViews() {
//        ivLoginBG = findViewById(R.id.ivLoginBG)
//        ivLoginLogo = findViewById(R.id.ivLoginLogo)

        Glide.with(this).load(R.drawable.bg_mid).into(ivLoginBG)
        Glide.with(this).load(R.drawable.logo_main).into(ivLoginLogo)

    }
}
