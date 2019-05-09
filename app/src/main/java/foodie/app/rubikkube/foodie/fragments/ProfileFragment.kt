package foodie.app.rubikkube.foodie.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.EditProfileActivity
import foodie.app.rubikkube.foodie.activities.HomeActivity
import foodie.app.rubikkube.foodie.activities.Signup
import foodie.app.rubikkube.foodie.adapter.ProfileFoodAdapter
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Food
import foodie.app.rubikkube.foodie.model.LoginSignUpResponse
import foodie.app.rubikkube.foodie.model.MeResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.java


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var profileAdapter: ProfileFoodAdapter
    private var pd: KProgressHUD? = null
    private lateinit var intent: Intent
    private var contribution: String? = null
    var foodList: ArrayList<Food> = ArrayList()



    override fun onStart() {
        super.onStart()
        Log.d("on start", "prun")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume", "prun")

        getMe(this!!.view!!)
        getListOfFood()

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
        intent = Intent(activity, EditProfileActivity::class.java)

        pd = Utils.progressDialog(context!!, "", "Please wait").show()

        view.setting_icon.setOnClickListener {
            view.context.startActivity(intent)
        }

//        getMe(view)
//        getListOfFood()
        return view
    }

    private fun setUpRecyclerView(view: View) {

        profileAdapter = ProfileFoodAdapter(context!!,foodList)
        view.friend_like_food.setHasFixedSize(false)
        val layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        view.friend_like_food.layoutManager = layoutManager
        view.friend_like_food.adapter = profileAdapter

    }

    private fun getMe(view: View) {
        val mService = ApiUtils.getSOService() as SOService

        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"


        mService.me(hm).enqueue(object : Callback<MeResponse> {
            override fun onFailure(call: Call<MeResponse>?, t: Throwable?) {
                pd?.dismiss()
                Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<MeResponse>?, response: Response<MeResponse>?) {
                pd?.dismiss()
                if (response!!.isSuccessful) {
                    intent.putExtra("meResponse", response.body())
                    if (response.body().profile == null || response.body().profile.message == null) {
                        startActivity(intent)
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
            requestOptionsCover.placeholder(R.drawable.cover_picture)
            requestOptionsCover.error(R.drawable.cover_picture)
            Glide.with(view).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/media/cover/" + me.id + "/" + me.profile.cover).into(view.profile_cover)

            val requestOptionsAvatar = RequestOptions()
            requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
            requestOptionsAvatar.error(R.drawable.profile_avatar)
                Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(view.profile_pic)

            view.profile_name.text = me.username.toString()
            view.age.text = me.profile.age.toString()
            view.city.text = me.profile.location

            view.profile_desc.text = me.profile.message.toString()

//            if (me.profile.interest.equals("Male")) {
//                view.female_card.visibility = View.VISIBLE
//            } else if (me.profile.interest.equals("Female")) {
//                view.male_card.visibility = View.VISIBLE
//            }

            view.age_val.text = me.profile.ages.toString()
            view.twenty_precent_crd.visibility = View.VISIBLE
            view.contribution_txt.text = me.profile.contribution.toString()

        }
    }

    private fun getListOfFood(){
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
                            foodList = response!!.body()
                            profileAdapter.update(foodList)
                        }
                    }
                })

    }
}// Required empty public constructor
