package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ProfileFoodAdapter
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.*
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import kotlin.math.log


class OtherUserProfileDetailActivity : AppCompatActivity() {

    private lateinit var profileAdapter: ProfileFoodAdapter
    var foodList: ArrayList<Food> = ArrayList()
    var meResponse:MeResponse? = null
    var id:String? = null
    private lateinit var timeLineAdapter: TimelineAdapter
    private var feedData:ArrayList<FeedData>?= ArrayList()
    private var pd1: KProgressHUD? = null
    private var userName:String?= null
    private var avatar:String?= null
    private var toUserId:String?= null
    private var toUserFcmToken:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile_detail)
        setUpRecyclerView()

        var intent = getIntent()

        if (intent != null) {
            id = intent.getStringExtra("id")
            //Log.d("id", id)
            getMe(id!!)
            getMyPost(id!!)
            /*if (Hawk.contains(id)) {
                if(Hawk.contains("SendingFromNearBy")) {
                    if (Hawk.contains("resp")) {
                        val res = Hawk.get("resp", "")
                        val profile = Hawk.get(id, "") as MeResponse
                        Log.d("profile", profile.username)
                        dataBindMe(profile)
                    } else {
                        val profile = Hawk.get(id, "") as LatLngData
                        Log.d("profile", profile.username)
                        dataBind(profile)
                    }
                }
            }*/

        }

        close_icon.setOnClickListener {
            finish()
        }

        chat_icon.setOnClickListener {
            Prefs.putString("toUserId", toUserId)
            Prefs.putString("fromUserId",Prefs.getString(Constant.USERID,""))
            Prefs.putString("avatarUser",toUserId)
            Prefs.putString("userName",userName)
            Prefs.putString("avatar",avatar)
            Prefs.putString("toUserFcmToken",toUserFcmToken)

            startActivity(Intent(this,ChatActivity::class.java))
        }
    }

    fun dataBindMe(me: MeResponse) {
        default_cover.visibility = View.GONE
        view_shadow.visibility = View.VISIBLE
        userName = me.username.toString()
        avatar = me.profile.avatar
        toUserId = me.id.toString()
        toUserFcmToken = me.device_token

        val requestOptionsCover = RequestOptions()
        requestOptionsCover.placeholder(R.drawable.cover_background_two)
        requestOptionsCover.error(R.drawable.cover_background_two)
        Glide.with(this).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/media/cover/" + me.id + "/" + me.profile.cover).into(profile_cover)

        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        if(me.profile.avatar!=null) {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(profile_pic)
            Log.d("PRofileLink",ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar)
        }
        else
        {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(profile_pic)

        }

        Log.d("agePrivate",""+me.profile.isAgePrivate+" "+me.profile.age);
        if(!me.profile.isAgePrivate) {
            age_title.visibility = View.INVISIBLE
            age.visibility = View.INVISIBLE
            age.text = me.profile.age.toString() }
        else{
            age_title.visibility = View.VISIBLE
            age.visibility = View.VISIBLE
            age.text = me.profile.age.toString() }
        profile_name.text = me.username.toString()

        if(me.profile.location == null)
        {
            city.visibility = View.GONE
        }
        else
        {
            city.visibility = View.VISIBLE
            city.text = me.profile.location
        }

        if(me.profile.message == null)
        {
            profile_desc.visibility = View.GONE
        }
        else
        {
            profile_desc.visibility = View.VISIBLE
            profile_desc.text = me.profile.message.toString()
        }

        if (me.profile.contribution == null) {
            contribution.visibility = View.GONE
            twenty_precent_crd.visibility = View.GONE
        }
        else {

            contribution.visibility = View.VISIBLE
            twenty_precent_crd.visibility = View.VISIBLE
            contribution_txt.text = me.profile.contribution.toString()
        }

        foodList = me.profile.foods!!

        if(foodList.size!=0) {
            food_like.visibility = View.VISIBLE
            profileAdapter.update(foodList)
        }
        else{
            food_like.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView() {

        profileAdapter = ProfileFoodAdapter(this, foodList,"")
        friend_like_food.setHasFixedSize(false)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation =  LinearLayout.HORIZONTAL
        friend_like_food.layoutManager = layoutManager
        friend_like_food.adapter = profileAdapter

        timeLineAdapter = TimelineAdapter(this,feedData,true)
        rv_my_posts.setHasFixedSize(false)
        val layoutManager1 = LinearLayoutManager(this)
        layoutManager1.orientation = LinearLayout.VERTICAL
        rv_my_posts.layoutManager = layoutManager1
        rv_my_posts.adapter = timeLineAdapter
    }

    private fun getMe(id:String) {
        val mService = ApiUtils.getSOService() as SOService
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        mService.getProfile(id,hm).enqueue(object : Callback<MeResponse> {
            override fun onFailure(call: Call<MeResponse>?, t: Throwable?) {
                Toast.makeText(applicationContext, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<MeResponse>?, response: Response<MeResponse>?) {
                meResponse = MeResponse()
                if (response!!.isSuccessful) {
                    meResponse = response.body()
                    dataBindMe(meResponse!!)
                    //setValue(view, response.body())
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getMyPost(id:String){
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        //pd1 = Utils.progressDialog(applicationContext, "", "Get my post").show()
        mService.getMyPost(id,hm)
        .enqueue(object : Callback<FeedResponse> {
            override fun onFailure(call: Call<FeedResponse>?, t: Throwable?) {
                //pd1?.dismiss()
                Toast.makeText(applicationContext, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<FeedResponse>?, response: Response<FeedResponse>?) {
               // pd1?.dismiss()
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
}
