package foodie.app.rubikkube.foodie

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import java.util.*
import retrofit2.Call
import android.util.Log
import android.os.Bundle
import android.view.Menu
import retrofit2.Response
import retrofit2.Callback
import android.widget.Toast
import android.view.MenuItem
import com.orhanobut.hawk.Hawk
import androidx.lifecycle.observe
import com.google.gson.JsonObject
import androidx.lifecycle.LiveData
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.viewpager.widget.ViewPager
import androidx.fragment.app.FragmentManager
import com.pixplicity.easyprefs.library.Prefs
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import foodie.app.rubikkube.foodie.utilities.Utils
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.models.MeResponse
import foodie.app.rubikkube.foodie.apiUtils.SOService
import kotlinx.android.synthetic.main.activity_home.*
import androidx.fragment.app.FragmentStatePagerAdapter
import foodie.app.rubikkube.foodie.utilities.Constants
import foodie.app.rubikkube.foodie.ui.home.HomeFragment
import com.google.android.gms.location.LocationServices
import foodie.app.rubikkube.foodie.ui.chats.ChatFragment
import foodie.app.rubikkube.foodie.ui.nearby.NearByFragment
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.ui.settings.SettingsFragment
import foodie.app.rubikkube.foodie.ui.chats.NotificationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import foodie.app.rubikkube.foodie.models.LatLngResponse
import java.lang.Exception


class MainActivity : AppCompatActivity(), Observer {

    companion object {
        var context: Context? = null
        var navView: BottomNavigationView? = null

        var navPager: ViewPager? = null
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    private var lastSelected: Int? = null
    private lateinit var navHost: NavController
    private lateinit var navView: BottomNavigationView
    private lateinit var notificationBell: MenuItem
    private lateinit var currentNavController: LiveData<NavController>
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.getMe()
        this.navView = findViewById(R.id.nav_view)
        navPager = findViewById(R.id.nav_view_pager)

        this.fusedLocationProvider = LocationServices
                .getFusedLocationProviderClient(this)

        if (checkPermissions()) sendLocation()
        else requestPermissions()

        this.navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> navPager?.currentItem = 0
                R.id.navigation_chat -> navPager?.currentItem = 3
                R.id.navigation_nearby -> navPager?.currentItem = 1
                R.id.navigation_profile -> navPager?.currentItem = 2
                R.id.navigation_settings -> navPager?.currentItem = 4
            }

            false
        }
        navPager?.adapter = ViewPagerAdapter(supportFragmentManager)
        navPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (lastSelected != null) {
                    navView.menu.getItem(lastSelected!!).isChecked = false
                } else {
                    navView.menu.getItem(0).isChecked = false
                }

                navView.menu.getItem(position).isChecked = true
                lastSelected = position
            }
        })

        NotificationViewModel.chats.observe(this@MainActivity) {
            if (Prefs.getInt("chatCount", 0) > 0)
                JavaUtils.showBadge(this@MainActivity, navView, R.id.navigation_chat, Prefs.getInt("chatCount", 0).toString())
            else JavaUtils.removeBadge(navView, R.id.navigation_chat)
        }

        if (Prefs.getInt("chatCount", 0) > 0)
            JavaUtils.showBadge(this@MainActivity, navView, R.id.navigation_chat, Prefs.getInt("chatCount", 0).toString())
        else JavaUtils.removeBadge(navView, R.id.navigation_chat)

        MainActivity.navView = navView
        MainActivity.context = this@MainActivity
    }

    private fun sendLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        this.fusedLocationProvider.lastLocation.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                Log.d("Latitude", it.result?.latitude.toString())
                Log.d("Longitude", it.result?.longitude.toString())

                val json = JsonObject().apply {
                    addProperty("lat", it.result?.latitude)
                    addProperty("lng", it.result?.longitude)
                }.toString()

                Log.d("LAST_LOCATION_DATA", json)

                ApiUtils.getSOService()?.sendCurrentLatLng(
                    header = mapOf(Pair("Authorization", Prefs.getString(Constants.TOKEN, ""))),
                    requestBody = Utils.getRequestBody(json)
                )?.enqueue(object: Callback<Any> {
                    override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                        Log.d("Recieved Response", response?.message() ?: "asdsad")
                    }

                    override fun onFailure(call: Call<Any>?, t: Throwable?) {
                        Log.d("Recieved Response", t?.message ?: "asdsad")
                    }
                })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)

        this.notificationBell = menu!!.findItem(R.id.action_bar_notification)
        this.notificationBell.isVisible = Prefs.getBoolean("showRingBell", false)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController.value?.navigateUp() ?: false
    }

    override fun update(p0: Observable?, p1: Any?) {
        this.notificationBell.isVisible = Prefs.getBoolean("showRingBell", false)


        if (p1 is Boolean) {
            if (p1) this.navHost.navigate(R.id.navigation_profile)
        } else if (p1 is String) {
            if (p1 == "showChatBadge") {
                JavaUtils.showBadge(this@MainActivity, navigation, R.id.navigation_chat, "2")
                Prefs.putBoolean("showChatBadge", true)
            } else {
                JavaUtils.removeBadge(navigation, R.id.navigation_chat)
                Prefs.putBoolean("showChatBadge", false)
            }
        }
    }

    private fun getMe() {
        val mService = ApiUtils.getSOService() as SOService
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        mService.getProfile(Prefs.getString(Constants.USER_ID,""),hm).enqueue(object : Callback<MeResponse> {
            override fun onFailure(call: Call<MeResponse>?, t: Throwable?) {
                Toast.makeText(this@MainActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<MeResponse>?, response: Response<MeResponse>?) {
                if (response!!.isSuccessful) {
                    Hawk.put("profileResponse",response.body())
                } else {
                    Toast.makeText(this@MainActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        startLocationPermissionRequest()
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when (PackageManager.PERMISSION_GRANTED) {
                grantResults[0] -> this.sendLocation()
                else -> {
                }
            }
        }
    }
}

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        val fragments = arrayOfNulls<Fragment>(5)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                try {
                    if (fragments[0] == null) {
                        fragments[0] = HomeFragment()
                    }
                } catch (e: Exception) {
                    fragments[0] = HomeFragment()
                }

                fragments[0]!!
            }
            1 -> NearByFragment()
            2 -> ProfileFragment()
            3 -> ChatFragment()
            4 -> {
                try {
                    if (fragments[4] == null) {
                        fragments[4] = SettingsFragment()
                    }
                } catch (e: Exception) {
                    fragments[4] = SettingsFragment()
                }

                fragments[4]!!
            }
            else -> fragments[0]!!
        }
    }

    override fun getCount(): Int {
        return 5
    }
}
