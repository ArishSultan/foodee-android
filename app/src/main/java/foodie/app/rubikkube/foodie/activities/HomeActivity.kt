package foodie.app.rubikkube.foodie.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.AppClass
import foodie.app.rubikkube.foodie.JavaUtils

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.R.id.navigation_profile
import foodie.app.rubikkube.foodie.classes.ObservableObject
import foodie.app.rubikkube.foodie.fragments.ChatFragment
import foodie.app.rubikkube.foodie.fragments.NearByFragment
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.fragments.SettingsFragment
import foodie.app.rubikkube.foodie.fragments.TimelineFragment
import foodie.app.rubikkube.foodie.model.MessageListResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONObject
import java.util.*

class HomeActivity : AppCompatActivity() , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Observer {


    private val mTextMessage: TextView? = null
    var fragment: androidx.fragment.app.Fragment? = null

    private var REQUEST_CHECK_SETTINGS = 1000
    private var googleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var request: LocationRequest? = null
    var lat : Double? = null
    var lng : Double? = null

    private var mSocket: Socket?= null
    private var onMessageReceived: Emitter.Listener? = null
    private var onNotificationReceived: Emitter.Listener? = null

    private var gson: Gson?= null
    private var messageListResponse: MessageListResponse?= null
    private var fromUserId:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        ObservableObject.getInstance().addObserver(this)
        fromUserId = Prefs.getString(Constant.USERID, "")
        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        Log.d("Bool",""+Prefs.getBoolean("comingPostCommentAdapter",false))
        val app = application as AppClass
        mSocket = app.socket

        // mSocket?.on("user-global-$fromUserId:new_message",onMessageReceived)

        mSocket!!.connect()

        socketListener()
        setupLocationManager()
        if(Prefs.getBoolean("comingPostCommentAdapter",false) || Prefs.getBoolean("comingFromTimelineAdapter",false)||Prefs.getBoolean("comingFromPostDetail",false)){
            navigation.selectedItemId = R.id.navigation_profile
            navigation.performClick()
            fragment = ProfileFragment()
            loadFragment(fragment)
            Prefs.putBoolean("comingPostCommentAdapter",false)
            Prefs.putBoolean("comingFromTimelineAdapter",false)
            Prefs.putBoolean("comingFromPostDetail",false)
        }
        else
        {
            fragment = TimelineFragment()
            loadFragment(fragment)
        }


        navigation.setOnNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.navigation_timeline -> {
                    fragment = TimelineFragment()
                    loadFragment(fragment)
                }

                R.id.navigation_nearby -> {
                    fragment = NearByFragment()
                    loadFragment(fragment)
//                    toast("NearBy")
                }
                R.id.navigation_profile -> {
                    fragment = ProfileFragment()
                    loadFragment(fragment)
//                    toast("Profile")
                }
                R.id.navigation_chat -> {
                    fragment = ChatFragment()
                    loadFragment(fragment)
                    JavaUtils.removeBadge(navigation,R.id.navigation_chat)

//                    toast("Chat")
                }
                R.id.navigation_settings -> {
                    fragment = SettingsFragment()
                    loadFragment(fragment)
//                    toast("Settings")
                }


            }
            true
        }


        if(Prefs.getBoolean("showChatBadge",false)) {
            JavaUtils.showBadge(this@HomeActivity,navigation,R.id.navigation_chat,"0")
        }else {
            JavaUtils.removeBadge(navigation,R.id.navigation_chat)
        }


    }

    fun socketListener(){



        onNotificationReceived = Emitter.Listener { args ->
            runOnUiThread {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    Log.d("SocketResponse",""+jsonObject)
                    Prefs.putBoolean("showRingBell",true)
                    ObservableObject.getInstance().updateValue("showRingBell")
                }catch (ex : Exception) {
                    ex.printStackTrace()
                }
            }
        }

        onMessageReceived = Emitter.Listener { args ->

            runOnUiThread {

                try {

                    val jsonObject = JSONObject(args[0].toString())
                    Log.d("SocketResponse",""+jsonObject)
                    messageListResponse = MessageListResponse()
                    gson = Gson()
                    messageListResponse = gson!!.fromJson(jsonObject.toString(), MessageListResponse::class.java)
                    ObservableObject.getInstance().updateValue(messageListResponse)
                    Log.d("MessageResponse",""+messageListResponse)


                    JavaUtils.showBadge(this@HomeActivity,navigation,R.id.navigation_chat,"2")
                    Prefs.putBoolean("showChatBadge",true)


                }catch (ex : Exception) {
                    ex.printStackTrace()
                }
            }

        }
    }


    override fun onResume() {
        super.onResume()

        if(mSocket != null) {

            mSocket?.on("user-global-$fromUserId:new_message",onMessageReceived)
            mSocket?.on("user-global-${Prefs.getString(Constant.USERID,"")}:new_notification",onNotificationReceived)

        }
    }

    override fun onPause() {
        super.onPause()

        if(mSocket != null) {

            mSocket?.off("user-global-$fromUserId:new_message",onMessageReceived)
            mSocket?.off("user-global-${Prefs.getString(Constant.USERID,"")}:new_notification",onNotificationReceived)

        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(mSocket != null) {

            mSocket?.off("user-global-$fromUserId:send_msg",onMessageReceived)
            mSocket?.off("user-global-${Prefs.getString(Constant.USERID,"")}:new_notification",onNotificationReceived)

        }
    }


    private fun loadFragment(fragment: androidx.fragment.app.Fragment?): Boolean {
        if (fragment != null) {

            supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, fragment).commit()

            return true
        }
        return false
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun  setupLocationManager() {
        //buildGoogleApiClient();
        if (googleApiClient == null) {

            googleApiClient =  GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
            //mGoogleApiClient = new GoogleApiClient.Builder(this);
        }
        googleApiClient?.connect()

    }

    private fun createLocationRequest() {

        request =  LocationRequest()
        request?.smallestDisplacement = 100f
        request?.fastestInterval = 500000
        request?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request?.numUpdates = 3

        val builder =  LocationSettingsRequest.Builder()
                .addLocationRequest(request!!)
        builder.setAlwaysShow(true)

        val result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build())


        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {

                LocationSettingsStatusCodes.SUCCESS -> setInitialLocation()

                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                this@HomeActivity,
                                REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }// Location settings are not satisfied. However, we have no way
            // to fix the settings so we won't show the dialog.
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    setInitialLocation()
                    Toast.makeText(this@HomeActivity, "Location enabled by user!", Toast.LENGTH_LONG).show()
                    // mRequestingLocationUpdates = true
                }
                Activity.RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                    finish()
                    Toast.makeText(this@HomeActivity, "You can not use Foodie with our your current location", Toast.LENGTH_LONG).show()
                    //mRequestingLocationUpdates = false
                }

                else -> {
                }
            }
        }
    }


    private fun setInitialLocation() {

        if (ActivityCompat.checkSelfPermission(this@HomeActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@HomeActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the mis0sing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request) { location ->


            mLastLocation = location
            this.lat = mLastLocation?.latitude
            this.lng = mLastLocation?.longitude

            mLastLocation?.latitude?.let { Prefs.putDouble("currentLat", it) }
            mLastLocation?.longitude?.let { Prefs.putDouble("currentLng",it) }


            if(googleApiClient != null)
                googleApiClient?.disconnect()

        }
    }

    override fun onConnected(p0: Bundle?) {


        createLocationRequest()

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    override fun update(p0: Observable?, p1: Any?) {
        if(p1 is Boolean){
            if(p1){

          //      fragment = ProfileFragment()
//                loadFragment(fragment)

                navigation.selectedItemId = R.id.navigation_profile
                navigation.performClick()

            }
        }else if(p1 is String) {


            if(p1.equals("showChatBadge")) {
                JavaUtils.showBadge(this@HomeActivity,navigation,R.id.navigation_chat,"2")
                Prefs.putBoolean("showChatBadge",true)
            }else  {
                JavaUtils.removeBadge(navigation,R.id.navigation_chat)
                Prefs.putBoolean("showChatBadge",false)
            }


        }
    }
}
