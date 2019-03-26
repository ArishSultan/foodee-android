package foodie.app.rubikkube.foodie.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.fragments.ChatFragment
import foodie.app.rubikkube.foodie.fragments.NearByFragment
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.fragments.SettingsFragment
import foodie.app.rubikkube.foodie.fragments.TimelineFragment

class HomeActivity : AppCompatActivity() {

    private val mTextMessage: TextView? = null
    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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


}
