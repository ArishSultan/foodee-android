package foodie.app.rubikkube.foodie.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.pixplicity.easyprefs.library.Prefs

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.fragments.TimelineFragment
import foodie.app.rubikkube.foodie.utilities.Constant
import java.util.ArrayList

class SplashScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        initViews()
        Handler().postDelayed({

            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

            Permissions.check(this/*context*/, permissions, null, null, object : PermissionHandler() {
                override fun onGranted() {
                    if (Prefs.getString(Constant.IS_LOGIN, "") == "true") {
                        val intent = Intent(this@SplashScreen, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashScreen, Login::class.java)
                        startActivity(intent)
                        finish()
                    }

                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                    Toast.makeText(this@SplashScreen, "Permission Denial", Toast.LENGTH_LONG).show()
                    finish()
                }
            })

        }, 3000)
    }

    private fun initViews() {

        Glide.with(this).load(R.drawable.splash).into(findViewById(R.id.ivBG))
        Glide.with(this).load(R.drawable.logo_main).into(findViewById(R.id.ivLogo))

    }
}
