package foodie.app.rubikkube.foodie.activities

import android.content.ClipData
import android.content.ClipboardManager;
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import com.stfalcon.frescoimageviewer.ImageViewer
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.JavaUtils
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ProfileFoodAdapter
import foodie.app.rubikkube.foodie.adapter.ReviewAdapter
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.*
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.*
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.age
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.age_title
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.city
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.contribution
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.contribution_txt
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.default_cover
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.food_like
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.friend_like_food
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.profile_cover
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.profile_desc
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.profile_name
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.profile_pic
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.ratingLayout
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.rating_title
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.rv_my_posts
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.twenty_precent_crd
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.view_shadow
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import kotlinx.android.synthetic.main.show_add_review_dialog.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import kotlin.math.log


class OtherUserProfileDetailActivity : AppCompatActivity() {

    private var dialog: androidx.appcompat.app.AlertDialog? = null
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
    private var reviews : Reviews? = null
    private var toUserFcmToken:String?= null

    val requestOptionsAvatar = RequestOptions()

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
            id?.toInt()?.let { getUserReviews(it) }
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

        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)
        close_icon.setOnClickListener {
            finish()
        }

        menu_icon.setOnClickListener { it ->
            val popup =  PopupMenu(this@OtherUserProfileDetailActivity, it)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    if(it.itemId == R.id.send_message) {

                        Prefs.putString("toUserId", toUserId)
                        Prefs.putString("fromUserId",Prefs.getString(Constant.USERID,""))
                        Prefs.putString("avatarUser",toUserId)
                        Prefs.putString("userName",userName)
                        Prefs.putString("avatar",avatar)
                        Prefs.putString("toUserFcmToken",toUserFcmToken)
                        startActivity(Intent(this,ChatActivity::class.java))

                    }else if(it.itemId == R.id.copy_link) {

                        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("userLink", meResponse?.link)
                        clipboard.setPrimaryClip(clip)

                    }else if(it.itemId == R.id.add_feedback) {

                        addFeedBackDialog()
                    }

                    return@OnMenuItemClickListener false
                })
                popup.inflate(R.menu.popup_menu);
                popup.show()
        }


        profile_desc.setOnClickListener {

            addAboutBuilder()
                //JavaUtils.showDetailDialog(this@OtherUserProfileDetailActivity,"About",profile_desc.text.toString())
        }


        ratingLayout.setOnClickListener {

            addShowReviewDialog()
        }

        profile_pic.setOnClickListener {

            val imgs : MutableList<String>? = arrayListOf()

            if (meResponse?.profile?.avatar != null) {
                imgs?.add(ApiUtils.BASE_URL + "/storage/media/avatar/" + meResponse?.id + "/" + meResponse?.profile?.avatar.toString())

            } else {
                imgs?.add("https://s3.amazonaws.com/37assets/svn/765-default-avatar.png")

            }
            Fresco.initialize(this@OtherUserProfileDetailActivity)
            ImageViewer.Builder(this@OtherUserProfileDetailActivity, imgs)
                    .show()
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
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.orientation =  LinearLayout.HORIZONTAL
        friend_like_food.layoutManager = layoutManager
        friend_like_food.adapter = profileAdapter

        timeLineAdapter = TimelineAdapter(this,feedData,true)
        rv_my_posts.setHasFixedSize(false)
        val layoutManager1 = androidx.recyclerview.widget.LinearLayoutManager(this)
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

    fun addAboutBuilder() {


        val builder = androidx.appcompat.app.AlertDialog.Builder(this@OtherUserProfileDetailActivity)
        val inflater = LayoutInflater.from(this@OtherUserProfileDetailActivity)

        val dialog_layout = inflater.inflate(R.layout.show_bio_dialog_layout_new, null)
        builder.setView(dialog_layout)

        var edit_text = dialog_layout.findViewById<View>(R.id.bio_et) as TextView
        edit_text.setText(profile_desc.text.toString())
        edit_text.movementMethod = ScrollingMovementMethod()
        var done_btn = dialog_layout.findViewById<View>(R.id.btn_done) as TextView

        done_btn.setOnClickListener {

            dialog?.dismiss()
        }

        dialog = builder.create()
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_corner)
        dialog!!.show()
    }


    fun addFeedBackDialog() {

         var nunumberOfStars : Int? = 0
        var messageTest = ""
        val builder = AlertDialog.Builder(this@OtherUserProfileDetailActivity)

        val inflater = LayoutInflater.from(this@OtherUserProfileDetailActivity)

        val dialog_layout = inflater.inflate(R.layout.show_add_review_dialog, null)
        builder.setView(dialog_layout)

        var imageView = dialog_layout.findViewById<View>(R.id.img_toUser) as ImageView
        var ratingBar = dialog_layout.findViewById<View>(R.id.ratingBar) as RatingBar
        var review_message = dialog_layout.findViewById<View>(R.id.test_message) as TextView
        var subtmitBtn = dialog_layout.findViewById<View>(R.id.btn_post_review) as CardView



        if(meResponse?.profile?.avatar!=null) {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + meResponse?.id + "/" + meResponse?.profile?.avatar).into(imageView)
        }
        else
        {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(profile_pic)

        }

        subtmitBtn.setOnClickListener {




                nunumberOfStars = ratingBar.rating.toInt()
                messageTest = review_message.text.toString().trim()

                if(nunumberOfStars!! <= 0) {
                    Toast.makeText(this@OtherUserProfileDetailActivity,"Please rate your foodee friend",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else if(messageTest.isNullOrEmpty()) {
                    Toast.makeText(this@OtherUserProfileDetailActivity,"Please type your message",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else {
                    Toast.makeText(this@OtherUserProfileDetailActivity,"daz",Toast.LENGTH_SHORT).show()
                    postReview(id!!,messageTest,nunumberOfStars.toString(),dialog!!)
                }


        }
//
        dialog = builder.create()

        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_corner)
        dialog!!.show()
    }

    fun postReview(toId : String,feeback : String, rateNumber : String, dialog : AlertDialog) {


        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        val jsonObject = JSONObject()
        jsonObject.put("to_id",toId)
        jsonObject.put("feedback",feeback)
        jsonObject.put("rate",rateNumber)

        mService.AddReview(hm,Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<SimpleResponse> {
                    override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                        Toast.makeText(this@OtherUserProfileDetailActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {

                        dialog.dismiss()
                        if(response?.isSuccessful!!) {

                            if(response.body().success) {
                                Toast.makeText(this@OtherUserProfileDetailActivity,"Done",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@OtherUserProfileDetailActivity,response.message(),Toast.LENGTH_SHORT).show()
                            }

                        }else {
                            Toast.makeText(this@OtherUserProfileDetailActivity,response.message(),Toast.LENGTH_SHORT).show()

                        }

                    }

                })
    }


    private fun getUserReviews(userID : Int) {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.getMyReviews(userID,hm)
                .enqueue(object : Callback<Reviews> {
                    override fun onFailure(call: Call<Reviews>?, t: Throwable?) {

                        Toast.makeText(this@OtherUserProfileDetailActivity,t?.message,Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<Reviews>?, response: Response<Reviews>?) {

                        if(response?.isSuccessful!!) {
                            reviews = response.body()

                            if(reviews?.data?.isEmpty()!!) {
                                ratingLayout.visibility = View.INVISIBLE
                            }else {

                                var totalRatings = 0
                                var fiveStar = 0
                                var fourStar = 0
                                var threeStar = 0
                                var twotar = 0
                                var oneStar = 0
                                for(i in reviews?.data?.indices!!) {


                                    if(reviews?.data!![i].rate.toInt() == 5) {
                                        fiveStar++
                                    }else if(reviews?.data!![i].rate.toInt() == 4) {
                                        fourStar++
                                    }else if(reviews?.data!![i].rate.toInt() == 3) {
                                        threeStar++
                                    }else if(reviews?.data!![i].rate.toInt() == 2) {
                                        twotar++
                                    }else if(reviews?.data!![i].rate.toInt() == 1) {
                                        oneStar++
                                    }
//                                    totalRatings += reviews?.data!![i].rate.toInt()


                                }


                                var sum = 5*fiveStar + 4*fourStar + 3*threeStar + 2*twotar + oneStar
                                var totalNoOfRatings = fiveStar+fourStar+threeStar+twotar+oneStar
                                var userRatings =sum.div(totalNoOfRatings).toFloat()

                                rating_title.setText(userRatings.toString())
                                //ratings.numStars = currentRatings
                                ratingLayout.visibility = View.VISIBLE


                            }

                        }else {

                            Toast.makeText(this@OtherUserProfileDetailActivity,response?.message(),Toast.LENGTH_SHORT).show()

                        }
                    }

                })


    }



    fun addShowReviewDialog() {


        val builder = AlertDialog.Builder(this@OtherUserProfileDetailActivity)

        val inflater = LayoutInflater.from(this@OtherUserProfileDetailActivity)

        val dialog_layout = inflater.inflate(R.layout.show_review_list, null)
        builder.setView(dialog_layout)

        var listView = dialog_layout.findViewById<View>(R.id.reviewList) as ListView
        var click_to_feedback = dialog_layout.findViewById<View>(R.id.click_to_feedback) as TextView

        listView.adapter = ReviewAdapter(this@OtherUserProfileDetailActivity,reviews?.data!!)


        click_to_feedback.setOnClickListener {
            dialog?.dismiss()

            addFeedBackDialog()
        }
//
        dialog = builder.create()

        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_corner)
        dialog!!.show()
    }




}
