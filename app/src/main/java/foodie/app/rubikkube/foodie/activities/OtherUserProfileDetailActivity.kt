package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.hawk.Hawk
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ProfileFoodAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Food
import foodie.app.rubikkube.foodie.model.LatLngData
import foodie.app.rubikkube.foodie.model.MeResponse
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class OtherUserProfileDetailActivity : AppCompatActivity() {

    private lateinit var profileAdapter: ProfileFoodAdapter
    var foodList: List<Food> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile_detail)
        setUpRecyclerView()

        var intent = getIntent()

        if (intent != null) {
            var id = intent.getStringExtra("id")
            Log.d("id", id)
            if (Hawk.contains(id)) {
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
        }
        close_icon.setOnClickListener {
            finish()
        }

        chat_icon.setOnClickListener {
            startActivity(Intent(this,ChatActivity::class.java))
        }
    }

    fun dataBind(me: LatLngData) {

        default_cover.visibility = View.GONE
        view_shadow.visibility = View.VISIBLE

        val requestOptionsCover = RequestOptions()
        requestOptionsCover.placeholder(R.drawable.cover_picture)
        requestOptionsCover.error(R.drawable.cover_picture)
        Glide.with(this).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/media/cover/" + me.id + "/" + me.cover).into(profile_cover)

        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)
        Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.avatar).into(profile_pic)

        profile_name.text = me.username.toString()
        age.text = me.age.toString()
        city.text = me.location

        profile_desc.text = me.message.toString()

        twenty_precent_crd.visibility = View.VISIBLE
        contribution_txt.text = me.contribution.toString()

//        foodList = me.profile.foods!!
        profileAdapter.update(foodList)


    }

    fun dataBindMe(me: MeResponse) {

        default_cover.visibility = View.GONE
        view_shadow.visibility = View.VISIBLE

        val requestOptionsCover = RequestOptions()
        requestOptionsCover.placeholder(R.drawable.cover_picture)
        requestOptionsCover.error(R.drawable.cover_picture)
        Glide.with(this).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/media/cover/" + me.id + "/" + me.profile.cover).into(profile_cover)

        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)
        Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(profile_pic)

        profile_name.text = me.username.toString()
        age.text = me.profile.age.toString()
        city.text = me.profile.location

        profile_desc.text = me.profile.message.toString()

        twenty_precent_crd.visibility = View.VISIBLE
        contribution_txt.text = me.profile.contribution.toString()
        foodList = me.profile.foods!!
        profileAdapter.update(foodList)


    }


    private fun setUpRecyclerView() {

        profileAdapter = ProfileFoodAdapter(this, foodList)
        friend_like_food.setHasFixedSize(false)
        val layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        friend_like_food.layoutManager = layoutManager
        friend_like_food.adapter = profileAdapter

    }
}
