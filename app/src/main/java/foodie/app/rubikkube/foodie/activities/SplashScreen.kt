package foodie.app.rubikkube.foodie.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.MainActivity

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.utilities.Constants
import java.util.ArrayList

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful)
                        Prefs.putString(Constants.FCM_TOKEN, task.result?.token)

                    return@OnCompleteListener
                })

        Glide.with(this).load(R.drawable.splash).into(findViewById(R.id.ivBG))
        Glide.with(this).load(R.drawable.logo_main).into(findViewById(R.id.ivLogo))

        Handler().postDelayed({
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

            Permissions.check(this, permissions, null, null, object : PermissionHandler() {
                override fun onGranted() {
                    this@SplashScreen.startActivity(
                        if (Prefs.getString(Constants.IS_LOGIN, "") == "true")
                            Intent(this@SplashScreen, MainActivity::class.java)
                        else Intent(this@SplashScreen, LoginActivity::class.java)
                    )

                    this@SplashScreen.finish()
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                    Toast.makeText(this@SplashScreen, "Permission Denial", Toast.LENGTH_LONG).show()
                    this@SplashScreen.finish()
                }
            })

        }, 3000)
    }
}
