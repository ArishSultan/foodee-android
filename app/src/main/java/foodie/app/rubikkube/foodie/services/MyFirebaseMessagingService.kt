package foodie.app.rubikkube.foodie.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.ChatActivity
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.activities.WhoLikesActivity
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.ui.chats.NotificationViewModel
import foodie.app.rubikkube.foodie.utilities.Constants

class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            val type = remoteMessage.data["type"].toString()
            val message = remoteMessage.data["body"].toString()
            val toId = remoteMessage.data["toUserId"].toString()
            val toName = remoteMessage.data["toName"].toString()
            val avatar = remoteMessage.data["avatar"].toString()
            val fromId = remoteMessage.data["fromUserId"].toString()
            val fromName = remoteMessage.data["fromName"].toString()
            val postId = remoteMessage.data["postId"].toString()

            Prefs.putBoolean("new-msg-$fromId",true)
            sendNotification(message, toId, fromId, toName, fromName, type, postId)

            if (type == "chat") {
                val count = Prefs.getInt("chatCount", 0) + 1
                NotificationViewModel.chats.postValue(count)
                Prefs.putInt("chatCount", count)
            }

            val count = Prefs.getInt("notification", 0) + 1
            NotificationViewModel.notifications.postValue(count)
            Prefs.putInt("notification", count)

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        Prefs.putString("fcmToken",token)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String, toId: String, fromId: String, toName: String, fromName: String, type: String, postId: String) {
        val intent = when (type) {
            "chat" -> {
                Intent(this, ChatActivity::class.java)
                    .putExtra("toUserId",toId)
                    .putExtra("fromNotification", true)
                    .putExtra("avatarUser",fromId)
                    .putExtra("recieverName",toName)
                    .putExtra("senderName",fromName)
                    .putExtra("comingFromFCM",true)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            "review" -> Intent(this, OtherUserProfileDetailActivity::class.java).putExtra("id", Prefs.getString(Constants.USER_ID, "")).putExtra("fromNotification", true)
            "post" -> Intent(this, TimelinePostDetailActivity::class.java).putExtra("PostID", postId).putExtra("fromNotification", true)
            "like" -> Intent(this, WhoLikesActivity::class.java).putExtra("postId", postId).putExtra("fromNotification", true)
            else -> null
        }

        val count = Prefs.getInt("notification", 0) - 1
        NotificationViewModel.chats.postValue(if (count >= 0) count else count)
        Prefs.putInt("chatCount", NotificationViewModel.chats.value ?: 0)

        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_main)
                .setContentIntent(pendingIntent)
                .setContentText(messageBody)
                .setContentTitle("Foodee")
                .setSound(defaultSoundUri)
                .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "android",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}