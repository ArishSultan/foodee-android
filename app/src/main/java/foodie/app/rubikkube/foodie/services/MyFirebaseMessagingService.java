package foodie.app.rubikkube.foodie.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orhanobut.hawk.Hawk;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import foodie.app.rubikkube.foodie.R;
import foodie.app.rubikkube.foodie.activities.HomeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    int uniqueinteger = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

    public static final String NOTIFICATION_CHANNEL_ID = "4655";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Hawk.init(this).build();

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */
                    true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }


//        if(remoteMessage.getData().get("for") != null) {
//
//            if(remoteMessage.getData().get("for").equals("singleChat")){
//                String toUserId = remoteMessage.getData().get("toUserId");
//            }
//        }

        simplePushNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());


    }


    @Override
    public void onNewToken(String token) {
        Prefs.putString("fcmToken",token);
        Log.d("fcmToken",token);

    }


//    private void scheduleJob() {
//        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
//        // [END dispatch_job]
//    }


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    //for silent chat notification
    private void simplePushNotification(String title, String message) {

        Intent i = new Intent(this, HomeActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "abcd",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("abcd");
            manager.createNotificationChannel(channel);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }


        manager.notify(uniqueinteger, builder.build());
    }


    //for  chat notification with sound
    private void showNotificationforchat(String title, String message ) {

        Intent i = new Intent(this, HomeActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(alarmSound)
                .setVibrate(new long[]{1000, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "abcd",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("abcd");
            manager.createNotificationChannel(channel);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        manager.notify(uniqueinteger, builder.build());
    }




    //this for normal notification withimage
    private void showNotification(String title, String message, String url, RemoteMessage remoteMessage) {

        Intent i;
        i = new Intent(this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        if(remoteMessage.getData().get("for")  != null) {
//             i = new Intent(this,ActivityNotification.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        }else {
//            i = new Intent(this,Home.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        }


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setVibrate(new long[]{1000, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "abcd",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("abcd");
            manager.createNotificationChannel(channel);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        manager.notify(uniqueinteger, builder.build());
    }

    //for showing image in normal notification
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //for silent chat notification single
    private void showNotificationsilentforsinglechat(String message, String from_user_id, String to_user_id, String userName) {

        Intent i = new Intent(this, HomeActivity.class);

//        Prefs.putString("toUserId", "");
//        Prefs.putString("threadId","");
//        Prefs.putString("userName","");
//        Prefs.putString("avatarUser","");
//        Prefs.putString("avatar","");

//        Prefs.putString("userName",inboxUserList!![position].username)
//        Prefs.putString("avatar",inboxUserList!![position].avatar)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("You have new message")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "abcd",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("abcd");
            manager.createNotificationChannel(channel);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        manager.notify(uniqueinteger, builder.build());
    }

    //for  chat notification with sound single
    private void showNotificationforsinglechat(String message, String from_user_id, String to_user_id, String userName) {

        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("other_user_id", to_user_id);
        i.putExtra("user_id", from_user_id);
        i.putExtra("user_name", userName);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        Intent i = new Intent(this, Singal_User_Chat.class);
//
//        i.putExtra("other_user_id", id);
//        i.putExtra("user_id", userId);
//        i.putExtra("user_name", userName);
//
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("You have new message")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(alarmSound)
                .setVibrate(new long[]{1000, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "abcd",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("abcd");
            manager.createNotificationChannel(channel);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }

        manager.notify(uniqueinteger, builder.build());
    }


    //checking method app is alive or not
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}


