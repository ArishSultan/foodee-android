package foodie.app.rubikkube.foodie.activities

import android.app.Activity
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import com.smarteist.autoimageslider.DefaultSliderView
import com.smarteist.autoimageslider.SliderLayout
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.CommentResponse
import foodie.app.rubikkube.foodie.model.FeedData
import foodie.app.rubikkube.foodie.model.LikeResponse
import foodie.app.rubikkube.foodie.model.MeResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_timeline_post_detail.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimelinePostDetailActivity : Activity() {

    var imageSlider: SliderLayout? = null
    var like_icon: ImageView? = null
    var txt_view_more_comments: TextView? = null
    var edt_msg: EditText? = null
    var btn_send_msg: Button? = null
    var timeLinePost: FeedData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_post_detail)

        imageSlider = findViewById(R.id.imageSlider)
        like_icon = findViewById(R.id.like_icon)
        txt_view_more_comments = findViewById(R.id.txt_view_more_comments)
        edt_msg = findViewById(R.id.edt_msg)
        btn_send_msg = findViewById(R.id.btn_send_msg)

        if (Hawk.contains("DetailPost")) {
            timeLinePost = Hawk.get("DetailPost", "") as FeedData
            dataBindMe(timeLinePost!!)
        }

        like_icon!!.setOnClickListener {
            var imageFlat = timeLinePost?.isLiked
            if (imageFlat!!) {
                likeAndUnlike(timeLinePost!!.id, this)
                like_icon!!.setImageResource(R.drawable.like)
                timeLinePost?.isLiked = false
            } else {
                likeAndUnlike(timeLinePost!!.id, this)
                like_icon!!.setImageResource(R.drawable.ic_liked)
                timeLinePost?.isLiked = true
            }
        }

        btn_send_msg!!.setOnClickListener {
            addComment(edt_msg!!.text.toString(), timeLinePost!!.id.toString(), this)
            edt_msg!!.text.clear()
        }

    }


    private fun dataBindMe(timeLinePost: FeedData) {
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        if (timeLinePost!!.user.profile.avatar != null) {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + timeLinePost!!.user.profile.userId + "/" + timeLinePost!!.user.profile.avatar).into(profile_image)
            Log.d("userProfileImage", "" + timeLinePost!!.user.profile.userId + "/" + timeLinePost!!.user.profile.avatar)
        } else {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(profile_image)
        }

        imageSlider!!.clearSliderViews()
        if (timeLinePost!!.photos != null) {
            if (!(timeLinePost!!.photos.contains(""))) {
                Log.d("EMpty", "" + timeLinePost!!.photos.contains(""))
                Log.d("content", timeLinePost!!.content)
                for (i in timeLinePost!!.photos.indices) {
                    imageSlider!!.visibility = View.VISIBLE
                    val sliderView = DefaultSliderView(this)
                    sliderView.imageUrl = ApiUtils.BASE_URL + "/storage/media/post/" + timeLinePost!!.photos.get(i)
                    imageSlider!!.addSliderView(sliderView)
                    //sliderView.setImageScaleType(ImageView.ScaleType.FIT_XY)
                    Log.d("ImageURL", ApiUtils.BASE_URL + "/storage/media/post/" + timeLinePost!!.photos.get(i) + " Size " + timeLinePost!!.photos.size)
                }
            } else {
                imageSlider!!.visibility = View.GONE
            }

            user_name.text = timeLinePost.user.username
            time_ago.text = timeLinePost.createdAt
            txt_content.text = timeLinePost.content
            comment_txt.text = timeLinePost.commentsCount.toString()
            like_txt.text = timeLinePost.likescount.toString()

            if (timeLinePost.isLiked) {
                like_icon?.setImageResource(R.drawable.ic_liked)
            } else {
                like_icon?.setImageResource(R.drawable.like)
            }

            if (timeLinePost.commentsCount > 0) {
                txt_view_more_comments!!.visibility = View.VISIBLE
            } else {
                txt_view_more_comments!!.visibility = View.GONE
            }
        }
    }

        fun addComment(content: String, post_id: String, context: Context) {
            val mService = ApiUtils.getSOService() as SOService
            val hm = java.util.HashMap<String, String>()
            hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
            hm["X-Requested-With"] = "XMLHttpRequest"
            val jsonObject = JSONObject()
            jsonObject.put("content", content)
            jsonObject.put("post_id", post_id)
            mService.addNewComment(hm, Utils.getRequestBody(jsonObject.toString())).enqueue(object : Callback<CommentResponse> {
                override fun onFailure(call: Call<CommentResponse>?, t: Throwable?) {
                    Toasty.error(context, "There is a Network Connectivity issue.").show()
                }

                override fun onResponse(call: Call<CommentResponse>?, response: Response<CommentResponse>?) {
                    Log.d("content", response!!.body().content)
                    Log.d("CreatedAt", response!!.body().createdAt)
                    Log.d("UpdatedAt", response!!.body().updatedAt)
                    Log.d("post_id", "" + response.body().postId)
                    Log.d("user_id", "" + response.body().userId)
                    Log.d("comment_id", "" + response.body().id)
                    Toasty.success(context, "Comment Posted Successfully").show()
                }
            })
        }


        fun likeAndUnlike(post_id: Int, context: Context) {
            val mService = ApiUtils.getSOService() as SOService
            val hm = java.util.HashMap<String, String>()
            var flag = false
            hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
            hm["X-Requested-With"] = "XMLHttpRequest"
            mService.likeAndUnlike(post_id, hm).enqueue(object : Callback<LikeResponse> {
                override fun onFailure(call: Call<LikeResponse>?, t: Throwable?) {
                    Toasty.error(context, "There is a Network Connectivity issue.").show()
                }

                override fun onResponse(call: Call<LikeResponse>?, response: Response<LikeResponse>?) {
                    Log.d("status", response!!.body().status.toString())
                    Log.d("post_count", response!!.body().postCount.toString())
                }
            })
        }
}
