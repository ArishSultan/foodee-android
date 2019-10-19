package foodie.app.rubikkube.foodie.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs
import com.stfalcon.frescoimageviewer.ImageViewer
import foodie.app.rubikkube.foodie.JavaUtils

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.EditProfileActivity
import foodie.app.rubikkube.foodie.activities.PostActivity
import foodie.app.rubikkube.foodie.adapter.ProfileFoodAdapter
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.*
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.age
import kotlinx.android.synthetic.main.fragment_profile.view.age_title
import kotlinx.android.synthetic.main.fragment_profile.view.city
import kotlinx.android.synthetic.main.fragment_profile.view.contribution
import kotlinx.android.synthetic.main.fragment_profile.view.contribution_txt
import kotlinx.android.synthetic.main.fragment_profile.view.default_cover
import kotlinx.android.synthetic.main.fragment_profile.view.divider_contribution
import kotlinx.android.synthetic.main.fragment_profile.view.divider_food_like
import kotlinx.android.synthetic.main.fragment_profile.view.food_like
import kotlinx.android.synthetic.main.fragment_profile.view.friend_like_food
import kotlinx.android.synthetic.main.fragment_profile.view.profile_cover
import kotlinx.android.synthetic.main.fragment_profile.view.profile_desc
import kotlinx.android.synthetic.main.fragment_profile.view.profile_name
import kotlinx.android.synthetic.main.fragment_profile.view.profile_pic
import kotlinx.android.synthetic.main.fragment_profile.view.rv_my_posts
import kotlinx.android.synthetic.main.fragment_profile.view.twenty_precent_crd
import kotlinx.android.synthetic.main.fragment_profile.view.view_shadow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.java


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : androidx.fragment.app.Fragment() {

    private lateinit var profileAdapter: ProfileFoodAdapter
    private var pd: KProgressHUD? = null
    private var pd1: KProgressHUD? = null
    private  lateinit var intent: Intent
    private var contribution: String? = null
    private var dialog: androidx.appcompat.app.AlertDialog? = null

    private lateinit var timeLineAdapter: TimelineAdapter
    private var feedData:ArrayList<FeedData>?= ArrayList()
    var builder: AlertDialog.Builder? = null
    var foodList: ArrayList<Food> = ArrayList()

    override fun onStart() {
        super.onStart()
        Log.d("on start", "prun")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume", "prun")
        getMe(this!!.view!!)
        getListOfFood(this!!.view!!)
        getMyPost()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("da","test")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false) as View
        setUpRecyclerView(view)
        builder = AlertDialog.Builder(activity)
        intent = Intent(activity, EditProfileActivity::class.java)
        view.setting_icon.setOnClickListener {
            view.context.startActivity(intent)
        }

        view.add_post.setOnClickListener {
            view.context.startActivity(Intent(activity, PostActivity::class.java))
        }

//        view.profile_desc.setOnClickListener {
//
//            JavaUtils.showDetailDialog(context,"About",view.profile_desc.text.toString())
//        }

        /*if(Prefs.getBoolean("comingFromTimelineAdapter",false)) {
            val navigation = activity!!.findViewById(R.id.navigation) as BottomNavigationView
            navigation.selectedItemId = R.id.navigation_profile
        }*/
//        getMe(view)
//        getListOfFood()

        view.profile_desc.setOnClickListener {

            addAboutBuilder(view)
        }
        return view
    }

    private fun setUpRecyclerView(view: View) {

        profileAdapter = ProfileFoodAdapter(context!!,foodList,"ComingFromProfileFragment")
        view.friend_like_food.setHasFixedSize(false)
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        view.friend_like_food.layoutManager = layoutManager
        view.friend_like_food.adapter = profileAdapter

        timeLineAdapter = TimelineAdapter(context!!,feedData,true)
        view.rv_my_posts.setHasFixedSize(false)
        val layoutManager1 = androidx.recyclerview.widget.LinearLayoutManager(activity)
        layoutManager1.orientation = LinearLayout.VERTICAL
        view.rv_my_posts.layoutManager = layoutManager1
        view.rv_my_posts.adapter = timeLineAdapter
    }

    private fun getMe(view: View) {
        val mService = ApiUtils.getSOService() as SOService
        val hm = HashMap<String, String>()
        pd = Utils.progressDialog(context!!, "", "Please wait").show()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"
        mService.getProfile(Prefs.getString(Constant.USERID,""),hm).enqueue(object : Callback<MeResponse> {
            override fun onFailure(call: Call<MeResponse>?, t: Throwable?) {
                pd?.dismiss()
                Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<MeResponse>?, response: Response<MeResponse>?) {
                pd?.dismiss()
                if (response!!.isSuccessful) {
                    intent.putExtra("meResponse", response.body())
                    if (response.body().profile == null || response.body().profile.message == null) {

                        builder!!.setTitle("Info for new users")

                        //Setting message manually and performing action on button click
                        builder!!.setMessage("Please enter your profile details to perform further actions.\n Do you want to edit your Profile ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { dialog, id ->
                                    dialog.dismiss()
                                    startActivity(intent)
                                }
                                .setNegativeButton("No") { dialog, id ->
                                    //  Action for 'NO' Button
                                    dialog.cancel()
                                }
                        //Creating dialog box
                        val alert = builder!!.create()
                        //Setting the title manually
                        alert.setTitle("Info for new users")
                        alert.show()
                    } else {
                        view.findViewById<ScrollView>(R.id.main_view).visibility = View.VISIBLE
                    }
                    setValue(view, response.body())
                } else {
                    Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setValue(view: View, me: MeResponse) {
        if (me.profile == null) {
            view.profile_name.text = me.username.toString()
        } else {
            view.default_cover.visibility = View.GONE
            view.view_shadow.visibility = View.VISIBLE

            val requestOptionsCover = RequestOptions()
            requestOptionsCover.placeholder(R.drawable.cover_background_two)
            requestOptionsCover.error(R.drawable.cover_background_two)
            if (me.profile.cover != null) {
                Glide.with(view).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/media/cover/" + me.id + "/" + me.profile.cover).into(view.profile_cover)
            } else {
                Glide.with(view).setDefaultRequestOptions(requestOptionsCover).load(R.drawable.cover_background_two).into(view.profile_cover)
            }

            val requestOptionsAvatar = RequestOptions()
            requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
            requestOptionsAvatar.error(R.drawable.profile_avatar)
            if (me.profile.avatar != null) {
                Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(view.profile_pic)
            } else {
                Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(view.profile_pic)

            }
            view.profile_name.text = me.username.toString()
            if(!me.profile.isAgePrivate)
            {
                view.age.visibility = View.INVISIBLE
                view.age_title.visibility = View.INVISIBLE
            }
            else
            {
                view.age.visibility = View.VISIBLE
                view.age_title.visibility = View.VISIBLE
                view.age.text = me.profile.age.toString()
            }

            if(me.profile.location == null)
            {
                view.city.visibility = View.GONE
            }
            else
            {
                view.city.visibility = View.VISIBLE
                view.city.text = me.profile.location
            }

            if(me.profile.message == null)
            {
                view.profile_desc.visibility = View.GONE
            }
            else
            {
                view.profile_desc.visibility = View.VISIBLE
                view.profile_desc.text = me.profile.message.toString()
            }

            if (me.profile.contribution == null) {
                view.contribution.visibility = View.GONE
                view.twenty_precent_crd.visibility = View.GONE
                view.divider_contribution.visibility = View.GONE
            }
            else {

                view.contribution.visibility = View.VISIBLE
                view.twenty_precent_crd.visibility = View.VISIBLE
                view.divider_contribution.visibility = View.VISIBLE
                view.contribution_txt.text = me.profile.contribution.toString()
            }
//            if (me.profile.interest.equals("Male")) {
//                view.female_card.visibility = View.VISIBLE
//            } else if (me.profile.interest.equals("Female")) {
//                view.male_card.visibility = View.VISIBLE
//            }
        }


        profile_pic.setOnClickListener {

            val imgs : MutableList<String>? = arrayListOf()

            if (me.profile.avatar != null) {
                imgs?.add(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar.toString())

            } else {
                imgs?.add("https://s3.amazonaws.com/37assets/svn/765-default-avatar.png")

            }
            Fresco.initialize(context)
            ImageViewer.Builder(context, imgs)
                    .show()
        }
    }

    private fun getMyPost(){
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        pd1 = Utils.progressDialog(context!!, "", "Get my post").show()
        mService.getMyPost(Prefs.getString(Constant.USERID,""),hm)
            .enqueue(object : Callback<FeedResponse> {
                override fun onFailure(call: Call<FeedResponse>?, t: Throwable?) {
                    pd1?.dismiss()
                    Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<FeedResponse>?, response: Response<FeedResponse>?) {
                    pd1?.dismiss()
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

    private fun getListOfFood(view: View){
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.getMyFoodList(hm)
            .enqueue(object : Callback<ArrayList<Food>>{
                override fun onFailure(call: Call<ArrayList<Food>>?, t: Throwable?) {
                    Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<ArrayList<Food>>?, response: Response<ArrayList<Food>>?) {
                    if(response!!.isSuccessful){
                        intent.putExtra("foodList", response.body())
                        if(response.body().size!=0) {
                            view.food_like.visibility = View.VISIBLE
                            view.divider_food_like.visibility = View.VISIBLE
                            foodList = response!!.body()
                            profileAdapter.update(foodList)
                        }
                        else{
                            view.food_like.visibility = View.GONE
                            view.divider_food_like.visibility = View.GONE
                        }
                    }
                }
            })
    }


    fun addAboutBuilder(view: View) {


        val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
        val inflater = LayoutInflater.from(context)

        val dialog_layout = inflater.inflate(R.layout.show_bio_dialog_layout_new, null)
        builder.setView(dialog_layout)

        var edit_text = dialog_layout.findViewById<View>(R.id.bio_et) as TextView
        edit_text.setText(view.profile_desc.text.toString())
        edit_text.movementMethod = ScrollingMovementMethod()
        var done_btn = dialog_layout.findViewById<View>(R.id.btn_done) as TextView

        done_btn.setOnClickListener {

            dialog?.dismiss()
        }

        dialog = builder.create()
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_corner)
        dialog!!.show()
    }


}// Required empty public constructor
