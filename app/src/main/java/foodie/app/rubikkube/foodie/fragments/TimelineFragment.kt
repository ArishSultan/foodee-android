package foodie.app.rubikkube.foodie.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.AppClass

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.NotificationCenterActivity
import foodie.app.rubikkube.foodie.activities.PostActivity
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.*
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.HashMap

class TimelineFragment : androidx.fragment.app.Fragment() {

    private var pd: KProgressHUD? = null
    private var rv_grid: androidx.recyclerview.widget.RecyclerView? = null
    private var feedData:ArrayList<FeedData>?= ArrayList()
    private var mSocket:Socket? = null
    private var onNotificationReceived: Emitter.Listener? = null
    private var img_bell:ImageView? = null
    private var img_notification_dot:ImageView? = null
    private var title_toolbar:TextView? = null

    private lateinit var timeLineAdapter: TimelineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = activity!!.application as AppClass
        mSocket = app.socket

        mSocket!!.connect()

        socketListener()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View =  inflater.inflate(R.layout.fragment_timeline, container, false)
//        view.toolbar_title.setText("TimeLine")
//        view.text.setText("TimeLine")

        Hawk.init(context!!).build()

        img_bell = view.toolbar_id!!.findViewById(R.id.img_notification_bell)
        img_notification_dot = view.toolbar_id!!.findViewById(R.id.img_notification_dot)
        title_toolbar = view.toolbar_id!!.findViewById(R.id.toolbar_title)
        title_toolbar!!.text = "Timeline"

        img_bell!!.visibility = View.VISIBLE
        img_bell!!.setOnClickListener {
            img_notification_dot!!.visibility = View.GONE
            val intent = Intent(context, NotificationCenterActivity::class.java)
            startActivity(intent)
        }
        setUpRecyclerView(view)


        Log.d("Latitude",""+Prefs.getDouble("currentLat", 0.0))
        Log.d("Longitude",""+Prefs.getDouble("currentLng", 0.0))
        view.post.setOnClickListener {
            view.context.startActivity(Intent(activity,PostActivity::class.java))
        }

        return view;
    }

    override fun onResume() {
        super.onResume()
        updateFcmToken()
        getMe(this!!.view!!)

        if(mSocket != null) {

            mSocket?.on("user-global-${Prefs.getString(Constant.USERID,"")}:new_notification",onNotificationReceived)
        }
    }

    override fun onPause() {
        super.onPause()
        if(mSocket != null) {

            mSocket?.off("user-global-${Prefs.getString(Constant.USERID,"")}:new_notification",onNotificationReceived)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mSocket != null) {

            mSocket?.off("user-global-${Prefs.getString(Constant.USERID,"")}:new_notification",onNotificationReceived)
        }
    }

    fun socketListener(){
        onNotificationReceived = Emitter.Listener { args ->
            activity!!.runOnUiThread {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    Log.d("SocketResponse",""+jsonObject)
                    img_notification_dot!!.visibility = View.VISIBLE
                }catch (ex : Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private fun setUpRecyclerView(view: View) {

        timeLineAdapter = TimelineAdapter(context!!,feedData,false)
        view.timeline_recyclerview.setHasFixedSize(false)

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayout.VERTICAL

        view.timeline_recyclerview.layoutManager = layoutManager
        view.timeline_recyclerview.adapter = timeLineAdapter

    }

    private fun getTimelinePost(){
        pd = Utils.progressDialog(context!!, "", "Getting Timeline post").show()
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.getTimelinePost(hm)
            .enqueue(object : Callback<FeedResponse> {
                override fun onFailure(call: Call<FeedResponse>?, t: Throwable?) {
                    pd?.dismiss()
                    Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<FeedResponse>?, response: Response<FeedResponse>?) {
                    pd?.dismiss()
                    if(response!!.isSuccessful){
                        Log.d("Response", Gson().toJson(response))
                        feedData = response.body().data as ArrayList<FeedData>?
                        //intent.putExtra("foodList", response.body())
                        //foodList = response!!.body()
                        timeLineAdapter.update(feedData)
                    }
                }
            })
    }

    private fun getMe(view: View) {
        val mService = ApiUtils.getSOService() as SOService
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        mService.getProfile(Prefs.getString(Constant.USERID,""),hm).enqueue(object : Callback<MeResponse> {
            override fun onFailure(call: Call<MeResponse>?, t: Throwable?) {
                Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<MeResponse>?, response: Response<MeResponse>?) {
                if (response!!.isSuccessful) {
                    Hawk.put("profileResponse",response.body())
                    setValue(view, response.body())
                } else {
                    Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setValue(view: View, me: MeResponse) {
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        if(me.profile.avatar!=null) {
            Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(view.profile_image)
            Log.d("PRofileLink",ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar)
        }
        else
        {
            Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(view.profile_image)

        }
    }

    private fun updateFcmToken() {
        val mService = ApiUtils.getSOService() as SOService
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        val jsonObject = JSONObject()
        jsonObject.put("device_token", Prefs.getString(Constant.FCM_TOKEN,""))
        mService.updateFcmToken(hm,Utils.getRequestBody(jsonObject.toString()))
            .enqueue(object : Callback<UpdateFcmTokenResponse> {
                override fun onFailure(call: Call<UpdateFcmTokenResponse>?, t: Throwable?) {
                    Toast.makeText(activity, t!!.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<UpdateFcmTokenResponse>?, response: Response<UpdateFcmTokenResponse>?) {
                    if (response!!.isSuccessful) {
                        getTimelinePost()
                    }
                }
            })
    }

}
