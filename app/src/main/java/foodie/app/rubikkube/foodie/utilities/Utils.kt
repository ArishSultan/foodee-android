package foodie.app.rubikkube.foodie.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.Nullable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaopiz.kprogresshud.KProgressHUD
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import foodie.app.rubikkube.foodie.model.NotificationData
import foodie.app.rubikkube.foodie.model.NotificationRequestModel
import okhttp3.MediaType
import okhttp3.RequestBody
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {



        fun getfcmToken() : String {

            var fcmToken : String? = ""
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

            return  fcmToken!!
        }

        fun sentSimpleNotification(context: Context,title: String, message : String, toFcmToken : String, clickAction : String) {

            val notificationRequestModel =  NotificationRequestModel()
            val  notificationData =  NotificationData()

            notificationData.setmBody(message)
            notificationData.setmBadge(1)
            notificationData.setmContent(1)
            notificationData.setmSound("default")
            notificationData.setmTitle(title)
            if(!clickAction.equals("nothing")) {
                notificationData.setmClickAction(clickAction)
            }
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



        fun sentMessageNotification(context: Context,title: String, message : String, toUserId : String,fromUserId : String,myProfilePicture : String, toFcmToken : String, clickAction : String) {

            val notificationRequestModel =  NotificationRequestModel()
            val  notificationData =  NotificationData()

            notificationData.setmBody(message)
            notificationData.setmBadge(1)
            notificationData.setmContent(1)
            notificationData.setmSound("default")
            notificationData.setmTitle(title)
            notificationData.setmFor("singleChat")
            notificationData.setmToUserId(toUserId)
            notificationData.setmToUserId(fromUserId)
            notificationData.setmToUserId(myProfilePicture)
            if(!clickAction.equals("nothing")) {
                notificationData.setmClickAction(clickAction)
            }
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