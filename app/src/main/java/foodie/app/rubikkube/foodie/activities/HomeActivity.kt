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
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
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
import com.pixplicity.easyprefs.library.Prefs

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.fragments.ChatFragment
import foodie.app.rubikkube.foodie.fragments.NearByFragment
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.fragments.SettingsFragment
import foodie.app.rubikkube.foodie.fragments.TimelineFragment
import java.util.ArrayList

class HomeActivity : AppCompatActivity() , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private val mTextMessage: TextView? = null
    var fragment: Fragment? = null

    private var REQUEST_CHECK_SETTINGS = 1000
    private var googleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var request: LocationRequest? = null
    var lat : Double? = null
    var lng : Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupLocationManager()
        fragment = TimelineFragment()
        loadFragment(fragment)

        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.navigation_timeline -> {
                    fragment = TimelineFragment()
                    loadFragment(fragment)
//                    toast("Timeline")
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
    }


    private fun loadFragment(fragment: Fragment?): Boolean {
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
            // here to request the missing permissions, and then overriding
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

}
