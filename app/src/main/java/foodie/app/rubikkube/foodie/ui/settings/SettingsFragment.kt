package foodie.app.rubikkube.foodie.ui.settings

import android.os.Bundle
import android.view.View
import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Toolbar
import foodie.app.rubikkube.foodie.R
import androidx.fragment.app.Fragment
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.ViewPagerAdapter
import foodie.app.rubikkube.foodie.activities.LoginActivity
import kotlinx.android.synthetic.main.activity_settings.view.*

class SettingsFragment : Fragment() {

    private var billingProcess: BillingProcessor? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_settings, container, false)

        this.billingProcess = BillingProcessor(
            context, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE",
            object: BillingProcessor.IBillingHandler {
                override fun onBillingInitialized() {}
                override fun onPurchaseHistoryRestored() {}
                override fun onBillingError(errorCode: Int, error: Throwable?) {}
                override fun onProductPurchased(productId: String, details: TransactionDetails?) {}
            })


        view.notif.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package",activity?.packageName)
                intent.putExtra("app_uid", activity?.applicationInfo?.uid)
                intent.putExtra("android.provider.extra.APP_PACKAGE", activity?.packageName)
                startActivity(intent)
            }
        }

        view.settings_toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.settings_toolbar_logout -> {
                    ViewPagerAdapter.fragments[0] = null
                    ViewPagerAdapter.fragments[4] = null

                    Prefs.clear()
                    startActivity(Intent(view.context, LoginActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                    activity?.finish()

                    true
                }
                else -> false
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (this.billingProcess?.handleActivityResult(requestCode, resultCode, data) != true) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}