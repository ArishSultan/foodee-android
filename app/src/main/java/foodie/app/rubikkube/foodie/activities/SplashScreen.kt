package foodie.app.rubikkube.foodie.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.pixplicity.easyprefs.library.Prefs

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.fragments.TimelineFragment
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import java.util.ArrayList

class SplashScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        //Getting fcmToken
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.d("fcm", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    // Log and toast
                    //val msg = "Token $token"
                    Prefs.putString(Constant.FCM_TOKEN,token)
                    Log.d("fcm", token)
                    Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
                })


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
