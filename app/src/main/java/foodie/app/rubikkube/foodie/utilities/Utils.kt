package foodie.app.rubikkube.foodie.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.Nullable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.kaopiz.kprogresshud.KProgressHUD
import okhttp3.MediaType
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*

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

        fun fromMillisToTimeString(millis: Long) : String {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return format.format(millis)
        }


        fun timeAgo(sDate: String): String {

            val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ENGLISH)
            val date = formatter.parse(sDate)
            var timeAgo = date.time
            timeAgo /= 1000
            Log.d("serverTime",""+timeAgo)
            val curTime = Calendar.getInstance().timeInMillis / 1000
            Log.d("currentTime",""+curTime)
            val timeElapsed = curTime - timeAgo
            val minutes = Math.round((timeElapsed / 60).toFloat())
            val hours = Math.round((timeElapsed / 3600).toFloat())
            val days = Math.round((timeElapsed / 86400).toFloat())
            val weeks = Math.round((timeElapsed / 604800).toFloat())
            val months = Math.round((timeElapsed / 2600640).toFloat())
            val years = Math.round((timeElapsed / 31207680).toFloat())

            // Seconds
            return if (timeElapsed <= 60) {
                "just now"
            } else if (minutes <= 60) {
                if (minutes == 1) {
                    "one minute ago"
                } else {
                    "$minutes minutes ago"
                }
            } else if (hours <= 24) {
                if (hours == 1) {
                    "an hour ago"
                } else {
                    "$hours hrs ago"
                }
            } else if (days <= 7) {
                if (days == 1) {
                    "yesterday"
                } else {
                    "$days days ago"
                }
            } else if (weeks <= 4.3) {
                if (weeks == 1) {
                    "a week ago"
                } else {
                    "$weeks weeks ago"
                }
            } else if (months <= 12) {
                if (months == 1) {
                    "a month ago"
                } else {
                    "$months months ago"
                }
            } else {
                if (years == 1) {
                    "one year ago"
                } else {
                    "$years years ago"
                }
            }//Years
            //Months
            //Weeks
            //Days
            //Hours
            //Minutes
        }
    }
}