package foodie.app.rubikkube.foodie.utilities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.annotation.Nullable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaopiz.kprogresshud.KProgressHUD
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.pixplicity.easyprefs.library.Prefs
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import foodie.app.rubikkube.foodie.MainActivity
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.models.NotificationData
import foodie.app.rubikkube.foodie.models.NotificationRequestModel
import okhttp3.MediaType
import okhttp3.RequestBody
import java.nio.charset.Charset
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object Utils {
    /// TIME Related Utilities ...

    fun timeStringToLong(time: String): Long {
        return try {
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(time).time
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    fun millisToTimeString(millis: Long) : String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(millis)
    }

    fun timeAgo(sDate: String): String {

        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ENGLISH)
        val date = formatter.parse(sDate)
        var timeAgo = date.time / 1000

        Log.d("serverTime", timeAgo.toString())
        val curTime = Calendar.getInstance().timeInMillis / 1000
        Log.d("currentTime", curTime.toString())
        val timeElapsed = curTime - timeAgo
        val minutes = (timeElapsed / 60).toFloat().roundToInt()
        val hours = (timeElapsed / 3600).toFloat().roundToInt()
        val days = (timeElapsed / 86400).toFloat().roundToInt()
        val weeks = (timeElapsed / 604800).toFloat().roundToInt()
        val months = (timeElapsed / 2600640).toFloat().roundToInt()
        val years = (timeElapsed / 31207680).toFloat().roundToInt()

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
        }
    }

    /// Firebase Related Utilities ...

    fun getFcmToken(): String {
        var fcmToken: String? = ""

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("fcmToken", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token
                    fcmToken = token
                })

        return fcmToken!!
    }

    fun sendSimpleNotification(context: Context, title: String, message: String, toFcmToken: String, clickAction: String, type: String, postId: String = "") {
        val notificationData = NotificationData()
        val notificationRequestModel = NotificationRequestModel()

        notificationData.type = type
        notificationData.setmBody(message)
        notificationData.setmBadge(1)
        notificationData.setmContent(1)
        notificationData.postId = postId
//        notificationData
        notificationData.setmSound("default")
        notificationData.setmTitle(title)
        if (clickAction != "nothing")
            notificationData.setmClickAction(clickAction)

        notificationRequestModel.data = notificationData;
        notificationRequestModel.to = toFcmToken
        notificationRequestModel.setmPrioriy("high")

        val gson =  Gson()
        val type = object : TypeToken<NotificationRequestModel>() {
        }.type

        val json = gson.toJson(notificationRequestModel, type);

        val client = AsyncHttpClient()
        val entity =  StringEntity(json)
        val data = entity.toString()
        client.addHeader("Content-Type","application/json")
        client.addHeader("Authorization","key=AAAAKKbOhEU:APA91bHQkeeHSBgZf6zp8VcPcMaJxbwbv2ANCgDV-McS7r0TZsDBzSM27UfWdgCliH41qyxvKoeGmUva3kzJ3iV-mV4L79reweSpZdJBSF8EJsZ3GNkRbZAA0gOp3NkaGmXiE1RHjAPG")

        client.post(context,"https://fcm.googleapis.com/fcm/send",entity,"application/json",object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {

                val str = responseBody?.toString(Charset.defaultCharset())
                Log.d("fcm",str)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                // val str = responseBody?.toString(Charset.defaultCharset())
                Log.d("fcm",error?.message)
            }

        })
    }



    fun sendMessageNotification(
            context: Context,
            title: String,
            message: String,
            toUserId: String,
            fromUserId: String,
            myProfilePicture: String,
            toFcmToken: String,
            clickAction: String,
            fromName: String,
            toName: String,
            type: String
    ) {

        val notificationData = NotificationData()
        val notificationRequestModel = NotificationRequestModel()

        notificationData.type = type
        notificationData.setmBadge(1)
        notificationData.setmContent(1)
        notificationData.setmTitle(title)
        notificationData.setmBody(message)
        notificationData.setmToUserName(toName)
        notificationData.setmToUserId(toUserId)
        notificationData.setmFromUserID(fromUserId)
        notificationData.setmFromUserName(fromName)

        if (clickAction != "nothing")
            notificationData.setmClickAction(clickAction)

        notificationRequestModel.to = toFcmToken
        notificationRequestModel.data = notificationData

        notificationData.setmSound("default")
        notificationData.setmFor("singleChat")
        notificationRequestModel.setmPrioriy("high")

        notificationData.setmMyProfilePicture(myProfilePicture)

        val gson = Gson()
        val type = object: TypeToken<NotificationRequestModel>() {}.type

        val json = gson.toJson(notificationRequestModel, type);

        val client = AsyncHttpClient()
        val entity =  StringEntity(json)

        client.addHeader("Content-Type","application/json")
        client.addHeader("Authorization","key=AAAAKKbOhEU:APA91bHQkeeHSBgZf6zp8VcPcMaJxbwbv2ANCgDV-McS7r0TZsDBzSM27UfWdgCliH41qyxvKoeGmUva3kzJ3iV-mV4L79reweSpZdJBSF8EJsZ3GNkRbZAA0gOp3NkaGmXiE1RHjAPG")

        client.post(context, "https://fcm.googleapis.com/fcm/send", entity, "application/json", object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val str = responseBody?.toString(Charset.defaultCharset())
                Log.d("fcm",str)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.d("fcm",error?.message)
            }
        })
    }

    fun progressDialog(context: Context, @Nullable title: String, message: String): KProgressHUD =
            KProgressHUD.create(context)
                    .setLabel(message)
                    .setDimAmount(0.5f)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)

    /// Network Related Utilities
    fun isConnectedOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun getSimpleTextBody(param: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), param)
    }

    fun getRequestBody(body: String): RequestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)


    /// Text Related Utilities
    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun navigateToUserProfile(context: Context, id: String) {
        if (id == Prefs.getString(Constants.USER_ID, ""))
            MainActivity.navPager?.currentItem = 2
        else context.startActivity(Intent(context, OtherUserProfileDetailActivity::class.java)
                .putExtra("id", id))
    }
}