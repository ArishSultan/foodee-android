package foodie.app.rubikkube.foodie.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import foodie.app.rubikkube.foodie.apiUtils.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.drawee.backends.pipeline.Fresco
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import com.smarteist.autoimageslider.SliderView
import com.stfalcon.frescoimageviewer.ImageViewer
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.JavaUtils
import foodie.app.rubikkube.foodie.MainActivity
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapters.PostCommentAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.models.*
import foodie.app.rubikkube.foodie.ui.chats.NotificationViewModel
import foodie.app.rubikkube.foodie.ui.home.SliderAdapterExample
import foodie.app.rubikkube.foodie.utilities.Constants
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_timeline_post_detail.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class TimelinePostDetailActivity : Activity() {

    var _profile: RelativeLayout? = null
    var loading: ProgressBar? = null
    var imageSlider: SliderView? = null
    var like_icon: ImageView? = null
    var txt_view_more_comments: TextView? = null
    var postID: String? = null
    var edt_msg: EditText? = null
    var btn_send_msg: Button? = null
    var timeLinePost: FeedData? = null
    var listCommentData: MutableList<CommentData>? = ArrayList()
    var deletePostResponse: DeleteFoodAndPostResponse = DeleteFoodAndPostResponse()
    var commentData: CommentData? = null
    var me: MeResponse? = null
    var user: User? = null
    var profile: Profile? = null
    private lateinit var postCommentAdapter: PostCommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_post_detail)
        intent = getIntent()
        imageSlider = findViewById(R.id.imageSlider)
        like_icon = findViewById(R.id.like_icon)
        txt_view_more_comments = findViewById(R.id.txt_view_more_comments)
        edt_msg = findViewById(R.id.edt_msg)
        btn_send_msg = findViewById(R.id.btn_send_msg)
        loading = findViewById(R.id.loading)
        _profile = findViewById(R.id.profile)

        if (intent.getBooleanExtra("fromNotification", false)) {
            val count2 = Prefs.getInt("notification", 0) - 1
            NotificationViewModel.notifications.postValue(if (count2 >= 0) count2 else 0)
            Prefs.putInt("notification", count2)
        }

        Glide.with(this).load(R.drawable.ic_keyboard_backspace_black_24dp).into(back_icon)
        postID = intent.getStringExtra("PostID")
        Log.d("OnTimelinePostDetail", postID.toString())

        getPostById(postID!!)
        setUpRecyclerView()

        back_icon!!.setOnClickListener {
            finish()
        }


        show_img_slide.setOnClickListener {

            val imgs : MutableList<String>? = arrayListOf()


            for(i in timeLinePost?.photos?.indices!!) {
                imgs?.add(  ApiUtils.BASE_URL + "/storage/media/post/" + timeLinePost?.photos!![i].toString())

            }

            Fresco.initialize(this@TimelinePostDetailActivity)
            ImageViewer.Builder(this@TimelinePostDetailActivity, imgs)
            .show()


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

                if (Prefs.getString(Constants.USER_ID, "").toInt() != timeLinePost!!.user.id) {
                    val myName = Prefs.getString(Constants.NAME, "")

                    if (timeLinePost!!.user.deviceToken != null) {
                        Utils.sendSimpleNotification(this@TimelinePostDetailActivity, "Foodee", "$myName likes your post", timeLinePost!!.user.deviceToken ?: "Data", "nothing", "like")
                    }
                }
            }
        }

        btn_send_msg!!.setOnClickListener {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (edt_msg!!.text.toString() == "") {
                Toasty.error(this, "Enter Comment first.").show()
                imm.hideSoftInputFromWindow(edt_msg!!.windowToken, 0)
            } else {
                addComment(edt_msg!!.text.toString(), timeLinePost!!.id.toString(), this)
                timeLinePost!!.commentsCount += 1
                comment_txt.text = timeLinePost!!.commentsCount.toString()
                edt_msg!!.text.clear()
                imm.hideSoftInputFromWindow(edt_msg!!.windowToken, 0)
            }
        }

        profile_image.setOnClickListener {
            Utils.navigateToUserProfile(this@TimelinePostDetailActivity, timeLinePost!!.userId.toString())

//            if (timeLinePost!!.userId.toString() == Prefs.getString(Constants.USER_ID, "")) {
//                //val activity: HomeActivity = mContext as HomeActivity
//                //val myFragment = ProfileFragment()
//                //activity.supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, myFragment).addToBackStack(null).commit()
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                Prefs.putBoolean("comingFromPostDetail", true)
//            } else {
//                val intent = Intent(this, OtherUserProfileDetailActivity::class.java)
//                intent.putExtra("id", timeLinePost!!.userId.toString())
//                startActivity(intent)
//            }
        }

        user_name.setOnClickListener {
            Utils.navigateToUserProfile(this@TimelinePostDetailActivity, timeLinePost!!.userId.toString())

//            if (timeLinePost!!.userId.toString() == Prefs.getString(Constants.USER_ID, "")) {
//                //val activity: HomeActivity = mContext as HomeActivity
//                //val myFragment = ProfileFragment()
//                //activity.supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, myFragment).addToBackStack(null).commit()
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                Prefs.putBoolean("comingFromPostDetail", true)
//            } else {
//                val intent = Intent(this, OtherUserProfileDetailActivity::class.java)
//                intent.putExtra("id", timeLinePost!!.userId.toString())
//                startActivity(intent)
//            }
        }

        txt_tagged_user.setOnClickListener {
            Utils.navigateToUserProfile(this@TimelinePostDetailActivity, timeLinePost!!.userId.toString())

//            val intent = Intent(this, OtherUserProfileDetailActivity::class.java)
//            intent.putExtra("id", timeLinePost!!.tags[0].pivot.userId.toString())
//            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setUpRecyclerView() {

        postCommentAdapter = PostCommentAdapter(this, listCommentData)
        rv_post_comments.setHasFixedSize(false)

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL

        rv_post_comments.layoutManager = layoutManager
        rv_post_comments.adapter = postCommentAdapter

        rv_post_comments.isNestedScrollingEnabled = true

    }


    private fun dataBindMe(timeLinePost: FeedData) {
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        if (timeLinePost.user.profile?.avatar != null) {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + timeLinePost!!.user.profile!!.userId + "/" + timeLinePost!!.user.profile!!.avatar).into(profile_image)
            Log.d("userProfileImage", "" + timeLinePost.user.profile!!.userId + "/" + timeLinePost.user!!.profile!!.avatar)
        } else {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(profile_image)
        }

        if (timeLinePost.photos?.isNotEmpty() == true) {
            Log.d("IMAGES_ON_PROFILE", timeLinePost.photos?.size.toString())
            imageSlider?.isVisible = true
            imageSlider?.setSliderAdapter(SliderAdapterExample(ArrayList(timeLinePost.photos!!), this@TimelinePostDetailActivity, null))
        } else imageSlider?.isVisible = false


        user_name.text = timeLinePost.user.username
            time_ago.text = timeLinePost.createdAt


        val url = JavaUtils.checkUserId(timeLinePost.content)
        if(!url.equals("")) {

            txt_content.text = timeLinePost.content.replace(url!!,"")

            txt_user_link.visibility = View.VISIBLE
            txt_user_link.setText(url)

        }else{
            txt_user_link.visibility = View.GONE
            txt_content.text = timeLinePost.content

        }



        txt_user_link.setOnClickListener {

            val mURl = JavaUtils.checkUserId(timeLinePost.content)
            val uID = mURl?.split("/")
            val intent = Intent(this@TimelinePostDetailActivity, OtherUserProfileDetailActivity::class.java)
            intent.putExtra("id", uID!![4])
            startActivity(intent)
        }

            comment_txt.text = timeLinePost.commentsCount.toString()
            like_txt.text = timeLinePost.likescount.toString()

            if (timeLinePost.isLiked) {
                like_icon?.setImageResource(R.drawable.ic_liked)
            } else {
                like_icon?.setImageResource(R.drawable.like)
            }



            if (timeLinePost.tags.size > 0) {
                txt_tagged_user.visibility = View.VISIBLE
                //txt_is_with.visibility = View.VISIBLE
                img_is_with.visibility = View.VISIBLE
                txt_tagged_user.text = timeLinePost.tags.get(0).username
            } else {
                txt_tagged_user.visibility = View.GONE
                //txt_is_with.visibility = View.GONE
                img_is_with.visibility = View.GONE
            }

            if (timeLinePost.userId.toString() == Prefs.getString(Constants.USER_ID, "")) {
                txtViewOptions.visibility = View.VISIBLE
            } else {
                txtViewOptions.visibility = View.GONE
            }

            txtViewOptions.setOnClickListener {
                //creating a popup menu
                val popup: PopupMenu = PopupMenu(this, txtViewOptions)
                //inflating menu from xml resource
                popup.inflate(R.menu.post_option_menu)
                //adding click listener
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.update_post -> {
//                            Hawk.put("EditPostObject", timeLinePost)
                            startActivity(Intent(this, EditPostActivity::class.java))
                        }
                        R.id.delete_post -> {

                            val alert = AlertDialog.Builder(this@TimelinePostDetailActivity)
                            alert.setTitle("Delete Post")
                            alert.setMessage("Are you sure you want to delete the Post?")
                            alert.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                                deletePost(timeLinePost.id)
                            }
                            alert.setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                                dialog.cancel()
                            }
                            alert.show()
                        }
                    }
                    true
                })
                popup.show()
            }
    }

    fun addComment(content: String, post_id: String, context: Context) {

        val mService = ApiUtils.getSOService() as SOService
        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
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
                profile!!.userId = me!!.id
                profile!!.avatar = me!!.profile?.avatar
                user!!.username = me!!.username
                user!!.id = me!!.id
                user!!.profile = profile
                commentData!!.user = user
                (listCommentData as java.util.ArrayList<CommentData>).add(commentData!!)
                postCommentAdapter.update(listCommentData!!)

                if (Prefs.getString(Constants.USER_ID, "").toInt() != timeLinePost!!.user.id) {
                    val myName = Prefs.getString(Constants.NAME, "")

                    if (!timeLinePost!!.user.deviceToken.isNullOrEmpty()) {
                        Utils.sendSimpleNotification(this@TimelinePostDetailActivity, "Foodee", "$myName commented your post", timeLinePost!!.user.deviceToken ?: "No token", "none", "post")
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
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
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

    fun getAllComments(post_id: Int, context: Context) {
        val mService = ApiUtils.getSOService() as SOService
        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"
        mService.getComments(post_id, hm).enqueue(object : Callback<GetCommentResponse> {
            override fun onFailure(call: Call<GetCommentResponse>?, t: Throwable?) {
                Toasty.error(context, "There is a Network Connectivity issue.").show()
            }

            override fun onResponse(call: Call<GetCommentResponse>?, response: Response<GetCommentResponse>?) {
                if (response!!.isSuccessful) {
                    listCommentData = ArrayList()
                    listCommentData = response!!.body().data
                    postCommentAdapter.update(listCommentData!!)
                    postCommentAdapter.updateFeed(timeLinePost!!)
                } else {
                    Log.d("Response", "Response Failed")
                }
            }
        })
    }

    private fun deletePost(post_id: Int) {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        val jsonObject = JSONObject()
        jsonObject.put("_method", "DELETE")
        mService.deletePost(post_id, hm, Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<DeleteFoodAndPostResponse> {
                    override fun onFailure(call: Call<DeleteFoodAndPostResponse>?, t: Throwable?) {
                        Toasty.error(this@TimelinePostDetailActivity, "" + t!!.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<DeleteFoodAndPostResponse>?, andPostResponse: Response<DeleteFoodAndPostResponse>?) {
                        if (andPostResponse!!.isSuccessful) {
                            deletePostResponse = andPostResponse.body()
                            Toasty.success(this@TimelinePostDetailActivity, deletePostResponse.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@TimelinePostDetailActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                })
    }

    private fun getPostById(post_id: String) {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.getPostById(post_id, hm)
                .enqueue(object : Callback<FeedData> {
                    override fun onFailure(call: Call<FeedData>?, t: Throwable?) {
                        Toasty.error(this@TimelinePostDetailActivity, "" + t!!.message, Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<FeedData>?, feedDataResponse: Response<FeedData>?) {
                        if (feedDataResponse!!.isSuccessful) {
                            timeLinePost = feedDataResponse.body()
                            _profile?.isVisible = true
                            loading?.isVisible = false
                            dataBindMe(timeLinePost!!)
                            getAllComments(timeLinePost!!.id, this@TimelinePostDetailActivity)
                            //finish()
                        }
                    } })
    }
}
