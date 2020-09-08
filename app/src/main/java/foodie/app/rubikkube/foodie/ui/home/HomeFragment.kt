package foodie.app.rubikkube.foodie.ui.home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mikepenz.actionitembadge.library.ActionItemBadge
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.NotificationCenterActivity
import foodie.app.rubikkube.foodie.activities.PostActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.apiUtils.SOService
import foodie.app.rubikkube.foodie.databinding.FragmentHomeBinding
import foodie.app.rubikkube.foodie.models.UpdateFcmTokenResponse
import foodie.app.rubikkube.foodie.ui.chats.NotificationViewModel
import foodie.app.rubikkube.foodie.utilities.Constants
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private var feedsViewModel: HomeViewModel? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        this.binding = FragmentHomeBinding.inflate(inflater, container, false)
        Hawk.init(this.context).build()


        this.binding.homeAppBar.setOnClickListener {
            if (this.binding.homeTabs.selectedTabPosition == 0) {
                PagerAdapter.featuredFeeds.scrollToTop()
            } else {
                PagerAdapter.newFeeds.scrollToTop()
            }
        }

        this.binding.homeTabsPager.adapter = PagerAdapter(activity!!)

        TabLayoutMediator(binding.homeTabs, binding.homeTabsPager) { tab, position ->
            tab.text = when (position) {
                0 -> "New"
                1 -> "Featured"
                else -> "New"
            }
        }.attach()

        this.binding.bellNotification.setOnClickListener {
            NotificationViewModel.notifications.postValue(0)
            Prefs.putInt("notification", 0)
            startActivity(Intent(activity, NotificationCenterActivity::class.java))
        }

        NotificationViewModel.notifications.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                this.binding.notificationCount.isVisible = false
            } else {
                this.binding.notificationCount.isVisible = true
                this.binding.notificationCountText.text = it.toString()
            }
        })

        this.binding.post.setOnClickListener {
            this.context?.startActivity(Intent(activity, PostActivity::class.java))
        }

        return this.binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("Testing Testing Testing", "asd")
        ActionItemBadge.update(activity, menu.findItem(R.id.action_bar_notification), Drawable.createFromPath("/home/arish/foodie-android/app/src/main/res/drawable/ic_notifications_white_24dp.xml"), ActionItemBadge.BadgeStyles.DARK_GREY, 10)
//        ActionItemBadge.hide(menu.findItem(R.id.action_bar_notification))

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        this.updateFcmToken()
    }

    private fun updateFcmToken() {
        val mService = ApiUtils.getSOService() as SOService
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        val jsonObject = JSONObject()
        jsonObject.put("device_token", Prefs.getString(Constants.FCM_TOKEN,""))
        mService.updateFcmToken(hm, Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<UpdateFcmTokenResponse> {
                    override fun onFailure(call: Call<UpdateFcmTokenResponse>?, t: Throwable?) {
                        Toast.makeText(activity, t!!.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<UpdateFcmTokenResponse>?, response: Response<UpdateFcmTokenResponse>?) {
                    }
                })
    }
}


class PagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    companion object {
        val newFeeds = NewFeedsFragment()
        val featuredFeeds = FeaturedFeedsFragment()
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return featuredFeeds
        } else if (position == 1) {
            return newFeeds
        }

        return newFeeds
    }
}
