package foodie.app.rubikkube.foodie.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import foodie.app.rubikkube.foodie.adapter.PostCommentAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.*
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_timeline_post_detail.*
import okhttp3.MultipartBody
import org.json.JSONObject
import org.w3c.dom.Comment
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
    var listCommentData:List<CommentData>? = ArrayList()
    var commentData:CommentData? = null
    var me: MeResponse? = null
    var user: User? = null
    var profile: Profile? = null
    private lateinit var postCommentAdapter: PostCommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_post_detail)

        imageSlider = findViewById(R.id.imageSlider)
        like_icon = findViewById(R.id.like_icon)
        txt_view_more_comments = findViewById(R.id.txt_view_more_comments)
        edt_msg = findViewById(R.id.edt_msg)
        btn_send_msg = findViewById(R.id.btn_send_msg)

        Glide.with(this).load(R.drawable.ic_keyboard_backspace_black_24dp).into(back_icon)

        if (Hawk.contains("DetailPost")) {
            timeLinePost = Hawk.get("DetailPost", "") as FeedData
            dataBindMe(timeLinePost!!)
        }

        setUpRecyclerView()

        Log.d("Post_id",""+timeLinePost!!.id)
        //getAllComments(timeLinePost!!.id,this)

        back_icon!!.setOnClickListener {
            finish()
        }

        like_icon!!.setOnClickListener {
            var imageFlat = timeLinePost?.isLiked
            if (imageFlat!!) {
                likeAndUnlike(timeLinePost!!.id, this)
                like_icon!!.setImageResource(R.drawable.like)
                timeLinePost?.isLiked = false
                timeLinePost!!.likescount -= 1
                like_txt.text = (timeLinePost!!.likescount).toString()
            } else {
                likeAndUnlike(timeLinePost!!.id, this)
                like_icon!!.setImageResource(R.drawable.ic_liked)
                timeLinePost?.isLiked = true
                timeLinePost!!.likescount += 1
                like_txt.text = (timeLinePost!!.likescount).toString()

                if(Prefs.getString(Constant.USERID,"").toInt() != timeLinePost!!.user.id) {
                    val myName = Prefs.getString(Constant.NAME,"")

                    if(!timeLinePost!!.user.device_token.isNullOrEmpty()) {

                        Utils.sentSimpleNotification(this@TimelinePostDetailActivity,"Foodee","$myName likes your post",timeLinePost!!.user.device_token,"nothing")
                    }

                }
            }
        }

        btn_send_msg!!.setOnClickListener {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (edt_msg!!.text.toString().equals("")) {
                Toasty.error(this,"Enter Comment first.").show()
                imm.hideSoftInputFromWindow(edt_msg!!.windowToken, 0)
            }
            else
            {
                addComment(edt_msg!!.text.toString(), timeLinePost!!.id.toString(), this)
                timeLinePost!!.commentsCount += 1
                comment_txt.text = timeLinePost!!.commentsCount.toString()
                edt_msg!!.text.clear()
                imm.hideSoftInputFromWindow(edt_msg!!.windowToken, 0)
            }
        }

        profile_image.setOnClickListener {
            if (timeLinePost!!.userId.toString().equals(Prefs.getString(Constant.USERID, ""))) {
                //val activity: HomeActivity = mContext as HomeActivity
                //val myFragment = ProfileFragment()
                //activity.supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, myFragment).addToBackStack(null).commit()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                Prefs.putBoolean("comingFromPostDetail",true)
            } else {
                val intent = Intent(this, OtherUserProfileDetailActivity::class.java)
                intent.putExtra("id", timeLinePost!!.userId.toString())
                startActivity(intent)
            }
        }

        user_name.setOnClickListener {
            if (timeLinePost!!.userId.toString().equals(Prefs.getString(Constant.USERID, ""))) {
                //val activity: HomeActivity = mContext as HomeActivity
                //val myFragment = ProfileFragment()
                //activity.supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, myFragment).addToBackStack(null).commit()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                Prefs.putBoolean("comingFromPostDetail",true)
            } else {
                val intent = Intent(this, OtherUserProfileDetailActivity::class.java)
                intent.putExtra("id", timeLinePost!!.userId.toString())
                startActivity(intent)
            }
        }

        txt_tagged_user.setOnClickListener {
                val intent = Intent(this, OtherUserProfileDetailActivity::class.java)
                intent.putExtra("id", timeLinePost!!.tags[0].pivot.userId.toString())
                startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getAllComments(timeLinePost!!.id,this)
    }

    private fun setUpRecyclerView() {

        postCommentAdapter = PostCommentAdapter(this,listCommentData)
        rv_post_comments.setHasFixedSize(false)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayout.VERTICAL

        rv_post_comments.layoutManager = layoutManager
        rv_post_comments.adapter = postCommentAdapter

        rv_post_comments.isNestedScrollingEnabled = true

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

            if(timeLinePost.tags.size > 0){
                txt_tagged_user.visibility = View.VISIBLE
                //txt_is_with.visibility = View.VISIBLE
                img_is_with.visibility = View.VISIBLE
                txt_tagged_user.text = timeLinePost.tags.get(0).username
            }
            else{
                txt_tagged_user.visibility = View.GONE
                //txt_is_with.visibility = View.GONE
                img_is_with.visibility = View.GONE
            }

//            if (timeLinePost.commentsCount > 0) {
//                txt_view_more_comments!!.visibility = View.VISIBLE
//            } else {
//                txt_view_more_comments!!.visibility = View.GONE
//            }
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
                    me = Hawk.get("profileResponse")
                    commentData = CommentData()
                    commentData!!.id = response.body().id
                    commentData!!.content = response.body().content
                    commentData!!.createdAt = "Just now"
                    commentData!!.postId = response.body().postId.toInt()
                    user = User()
                    profile = Profile()
                    profile!!.avatar = me!!.profile.avatar
                    user!!.username = me!!.username
                    user!!.id = me!!.id
                    user!!.profile = profile
                    commentData!!.user = user
                    (listCommentData as java.util.ArrayList<CommentData>).add(commentData!!)
                    postCommentAdapter.update(listCommentData)

                    if(Prefs.getString(Constant.USERID,"").toInt() != timeLinePost!!.user.id) {
                        val myName = Prefs.getString(Constant.NAME,"")

                        if(!timeLinePost!!.user.device_token.isNullOrEmpty()) {
                            Utils.sentSimpleNotification(this@TimelinePostDetailActivity,"Foodee","$myName commented your post",timeLinePost!!.user.device_token,"nothing")
                        }
                    }
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

        fun getAllComments(post_id:Int,context: Context) {
            val mService = ApiUtils.getSOService() as SOService
            val hm = java.util.HashMap<String, String>()
            hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
            hm["X-Requested-With"] = "XMLHttpRequest"
            mService.getComments(post_id,hm).enqueue(object : Callback<GetCommentResponse> {
                override fun onFailure(call: Call<GetCommentResponse>?, t: Throwable?) {
                    Toasty.error(context,"There is a Network Connectivity issue.").show()
                }
                override fun onResponse(call: Call<GetCommentResponse>?, response: Response<GetCommentResponse>?) {
                    if (response!!.isSuccessful) {
                        listCommentData = ArrayList()
                        listCommentData = response!!.body().data
                        postCommentAdapter.update(listCommentData)
                    }else{
                        Log.d("Response","Response Failed")
                    }
                }
            })
        }
}
