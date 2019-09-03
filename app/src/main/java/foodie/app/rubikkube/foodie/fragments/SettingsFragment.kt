package foodie.app.rubikkube.foodie.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat.finishAffinity
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pixplicity.easyprefs.library.Prefs

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.Login
import kotlinx.android.synthetic.main.activity_settings.view.*
import android.widget.Toast
import android.R.attr.country
import android.content.DialogInterface
import android.app.AlertDialog
import android.content.ContextWrapper


class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_settings, container, false) as View

        view.logout_btn.setOnClickListener {
            Prefs.clear()
            val intent = Intent(view.context, Login::class.java) as Intent
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity?.finish()
        }

        view.notif.setOnClickListener {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"

//for Android 5-7
                intent.putExtra("app_package",activity?.packageName)
                intent.putExtra("app_uid", activity?.applicationInfo?.uid)

// for Android O
                intent.putExtra("android.provider.extra.APP_PACKAGE", activity?.packageName)

                startActivity(intent)
            }
        }

        return view
    }



}// Required empty public constructor




