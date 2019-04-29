package foodie.app.rubikkube.foodie.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.Nullable
import android.text.TextUtils
import android.util.Patterns
import com.kaopiz.kprogresshud.KProgressHUD
import okhttp3.MediaType
import okhttp3.RequestBody

class Utils {

    companion object {

        fun getSimpleTextBody(param: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), param)
        }
        fun getRequestBody(body : String) : RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)

        fun progressDialog(
                ctx: Context, @Nullable _title: String,
                _message: String/*, @DrawableRes @Nullable int icon*/
        ): KProgressHUD {
            return KProgressHUD.create(ctx)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(_message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

        }

        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun isConnectedOnline(context: Context): Boolean {

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return if (netInfo != null && netInfo.isConnectedOrConnecting) {
                true
            } else {
                false
            }
        }

    }
}