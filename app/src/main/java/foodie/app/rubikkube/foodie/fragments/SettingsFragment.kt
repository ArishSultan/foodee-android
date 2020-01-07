package foodie.app.rubikkube.foodie.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
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
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails









class SettingsFragment : androidx.fragment.app.Fragment() , BillingProcessor.IBillingHandler  {

    var bp: BillingProcessor? = null

    override fun onBillingInitialized() {

    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {

    }

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

        bp = BillingProcessor(context, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this)
        bp?.initialize()

        view.notif.setOnClickListener {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"

//for Android 5-7
                intent.putExtra("app_package",activity?.packageName)
                intent.putExtra("app_uid", activity?.applicationInfo?.uid)

// for Android 0
                intent.putExtra("android.provider.extra.APP_PACKAGE", activity?.packageName)

                startActivity(intent)
            }
        }

        view.purchase.setOnClickListener {

            bp?.purchase(activity, "android.test.purchased")
        }

        return view
    }


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (bp?.handleActivityResult(requestCode, resultCode, data) != true) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}// Required empty public constructor




