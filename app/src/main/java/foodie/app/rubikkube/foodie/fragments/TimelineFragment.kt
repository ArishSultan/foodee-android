//package foodie.app.rubikkube.foodie.fragments
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
//import com.google.gson.Gson
//import com.kaopiz.kprogresshud.KProgressHUD
//import com.orhanobut.hawk.Hawk
//import com.pixplicity.easyprefs.library.Prefs
//import foodie.app.rubikkube.foodie.R
//import foodie.app.rubikkube.foodie.activities.NotificationCenterActivity
//import foodie.app.rubikkube.foodie.activities.PostActivity
//import foodie.app.rubikkube.foodie.adapters.TimelineAdapter
//import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
//import foodie.app.rubikkube.foodie.apiUtils.SOService
//import foodie.app.rubikkube.foodie.classes.ObservableObject
//import foodie.app.rubikkube.foodie.models.FeedData
//import foodie.app.rubikkube.foodie.models.FeedResponse
//import foodie.app.rubikkube.foodie.models.MeResponse
//import foodie.app.rubikkube.foodie.models.UpdateFcmTokenResponse
//import foodie.app.rubikkube.foodie.utilities.Constants
//import foodie.app.rubikkube.foodie.utilities.Utils
//import kotlinx.android.synthetic.main.fragment_timeline.view.*
//import org.json.JSONObject
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.util.*
//
//class TimelineFragment : androidx.fragment.app.Fragment(), Observer {
//
//    override fun update(o: Observable?, arg: Any?) {
//        if (arg is String) {
//            if (arg == "showRingBell")
//                img_notification_dot?.visibility = View.VISIBLE
//        }
//    }
//
//    private var pd: KProgressHUD? = null
//    private var rv_grid: androidx.recyclerview.widget.RecyclerView? = null
//    private var feedData:ArrayList<FeedData>?= ArrayList()
//
//    private var img_bell:ImageView? = null
//    private var img_notification_dot:ImageView? = null
//    private var title_toolbar:TextView? = null
//    private var tabLayout: TabLayout? = null
//
//    private lateinit var timeLineAdapter: Any
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        ObservableObject.getInstance().addObserver(this)
//
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//
//        val view: View =  inflater.inflate(R.layout.fragment_timeline, container, false)
////        view.toolbar_title.setText("TimeLine")
////        view.text.setText("TimeLine")
//
//        Hawk.init(context!!).build()
//
//        tabLayout = view.tabs_id!!.findViewById<TabLayout>(R.id.tabs_id)
//
//        img_bell = view.toolbar_id!!.findViewById(R.id.img_notification_bell)
//        img_notification_dot = view.toolbar_id!!.findViewById(R.id.img_notification_dot)
//        title_toolbar = view.toolbar_id!!.findViewById(R.id.toolbar_title)
//        title_toolbar!!.text = "Timeline"
//
//        if (Prefs.getBoolean("showRingBell",false))
//            img_notification_dot?.visibility = View.VISIBLE
//        else
//            img_notification_dot?.visibility = View.INVISIBLE
//
//        img_bell!!.visibility = View.VISIBLE
//        img_bell!!.setOnClickListener {
//            img_notification_dot!!.visibility = View.GONE
//            Prefs.putBoolean("showRingBell",false)
//            val intent = Intent(context, NotificationCenterActivity::class.java)
//            startActivity(intent)
//        }
//        setUpRecyclerView(view)
//
//        Log.d("Latitude",""+Prefs.getDouble("currentLat", 0.0))
//        Log.d("Longitude",""+Prefs.getDouble("currentLng", 0.0))
//        view.post.setOnClickListener {
//            view.context.startActivity(Intent(activity,PostActivity::class.java))
//        }
//        view.swipe_refresh.setOnRefreshListener {
//            getTimelinePost(true)
//        }
//
//        this.tabLayout?.setOnTabSelectedListener(object : OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                getTimelinePost(false)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
//
//        return view;
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        getTimelinePost(false)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        updateFcmToken()
//        getMe(this!!.view!!)
//    }
//
//    private fun setUpRecyclerView(view: View) {
//
//        timeLineAdapter = TimelineAdapter(context!!,feedData,false)
//        view.timeline_recyclerview.setHasFixedSize(false)
//
//        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
//        layoutManager.orientation = RecyclerView.VERTICAL
//
//        view.timeline_recyclerview.layoutManager = layoutManager
//        view.timeline_recyclerview.adapter = timeLineAdapter
//
//    }
//
//    private fun getTimelinePost(isRefresh : Boolean){
////        feedData?.clear()
////
////        val hm = HashMap<String, String>()
////        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
////        val mService = ApiUtils.getSOService() as SOService
////
////        Log.d("asd", this.tabLayout!!.selectedTabPosition.toString())
////
////        (if (this.tabLayout!!.selectedTabPosition == 0)
////            mService.getTimelinePost(hm)
////        else mService.getFeaturedTimelinePost(hm))
////            .enqueue(object : Callback<FeedResponse> {
////                override fun onFailure(call: Call<FeedResponse>?, t: Throwable?) {
////                    pd?.dismiss()
////                    Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
//////                    Toast.makeText(activity, t?.message, Toast.LENGTH_SHORT).show()
////                    Log.d("asd", t?.message)
////                    if(isRefresh) {
////                        view?.swipe_refresh?.isRefreshing = false
////                    }
////                }
////
////                override fun onResponse(call: Call<FeedResponse>?, response: Response<FeedResponse>?) {
////                    pd?.dismiss()
////                    if (response!!.isSuccessful){
////                        Log.d("Response", Gson().toJson(response))
////                        feedData = response.body().data as ArrayList<FeedData>?
////                        timeLineAdapter.update(feedData)
////                    } else {
////                        Toast.makeText(activity, response?.raw().toString(), Toast.LENGTH_SHORT).show()
////                    }
////
////                    if (isRefresh) {
////                        view?.swipe_refresh?.isRefreshing = false
////                    }
////                }
////            })
//    }
//
//    private fun getMe(view: View) {
//        val mService = ApiUtils.getSOService() as SOService
//        val hm = HashMap<String, String>()
//        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
//        hm["X-Requested-With"] = "XMLHttpRequest"
//
//        mService.getProfile(Prefs.getString(Constants.USER_ID,""),hm).enqueue(object : Callback<MeResponse> {
//            override fun onFailure(call: Call<MeResponse>?, t: Throwable?) {
//                Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onResponse(call: Call<MeResponse>?, response: Response<MeResponse>?) {
//                if (response!!.isSuccessful) {
//                    Hawk.put("profileResponse",response.body())
//                    setValue(view, response.body())
//                } else {
//                    Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//    }
//
//    private fun setValue(view: View, me: MeResponse) {
//        val requestOptionsAvatar = RequestOptions()
//        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
//        requestOptionsAvatar.error(R.drawable.profile_avatar)
//
//        if(me.profile.avatar!=null) {
//            Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(view.profile_image)
//            Log.d("PRofileLink",ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar)
//        }
//        else
//        {
//            Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(view.profile_image)
//
//        }
//    }
//
//    private fun updateFcmToken() {
//        val mService = ApiUtils.getSOService() as SOService
//        val hm = HashMap<String, String>()
//        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
//        hm["X-Requested-With"] = "XMLHttpRequest"
//
//        val jsonObject = JSONObject()
//        jsonObject.put("device_token", Prefs.getString(Constants.FCM_TOKEN,""))
//        mService.updateFcmToken(hm,Utils.getRequestBody(jsonObject.toString()))
//            .enqueue(object : Callback<UpdateFcmTokenResponse> {
//                override fun onFailure(call: Call<UpdateFcmTokenResponse>?, t: Throwable?) {
//                    Toast.makeText(activity, t!!.message, Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onResponse(call: Call<UpdateFcmTokenResponse>?, response: Response<UpdateFcmTokenResponse>?) {
//                    if (response!!.isSuccessful) {
//                        //getTimelinePost(false)
//                    }
//                }
//            })
//    }
//
//}
