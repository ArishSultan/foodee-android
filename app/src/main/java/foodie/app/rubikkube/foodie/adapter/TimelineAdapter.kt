package foodie.app.rubikkube.foodie.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import foodie.app.rubikkube.foodie.activities.HomeActivity
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.model.*
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_timeline_post_detail.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TimelineAdapter(context: Context, feedDate: List<FeedData>?, isMyProfile:Boolean) : RecyclerView.Adapter<TimelineAdapter.TimelineHolder>(){

    val mContext = context
    var listFeedData = feedDate
    var imageFlat = false
    var commentData:CommentData? = null
    var me: MeResponse? = null
    var user: User? = null
    var profile: Profile? = null
    var isMyProfile: Boolean = isMyProfile


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineHolder {
        val inflater = LayoutInflater.from(parent?.context)
        Hawk.init(mContext).build()
        return TimelineHolder(inflater.inflate(R.layout.holder_timelinefragment, parent, false))
    }

    override fun getItemCount(): Int {
        return listFeedData!!.size
    }

    override fun onBindViewHolder(holder: TimelineHolder, position: Int) {
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        if(listFeedData?.get(position)!!.user.profile.avatar!=null) {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + listFeedData?.get(position)!!.user.profile.userId + "/" + listFeedData?.get(position)!!.user.profile.avatar).into(holder.profile_image)
            Log.d("userProfileImage",""+listFeedData?.get(position)!!.user.profile.userId + "/" + listFeedData?.get(position)!!.user.profile.avatar)
        }
        else {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(holder.profile_image)
        }
        holder.imageSlider.clearSliderViews()
        if(listFeedData?.get(position)!!.photos!=null ){
            if(!(listFeedData?.get(position)!!.photos.contains(""))) {
                Log.d("EMpty", "" + listFeedData?.get(position)!!.photos.contains(""))
                Log.d("content",listFeedData?.get(position)!!.content)
                for (i in listFeedData?.get(position)!!.photos.indices) {
                    holder.imageSlider.visibility = View.VISIBLE
                    val sliderView = DefaultSliderView(mContext)
                    sliderView.imageUrl = ApiUtils.BASE_URL + "/storage/media/post/" + listFeedData?.get(position)!!.photos.get(i)
                    holder.imageSlider.addSliderView(sliderView)
                    //sliderView.setImageScaleType(ImageView.ScaleType.FIT_XY)
                    Log.d("ImageURL", ApiUtils.BASE_URL + "/storage/media/post/" + listFeedData?.get(position)!!.photos.get(i) + " Size " + listFeedData?.get(position)!!.photos.size)
                }
            }
            else {
                holder.imageSlider.visibility = View.GONE
            }
        }
        if (listFeedData!!.get(position).isLiked) {
            holder.like_icon.setImageResource(R.drawable.ic_liked)
        }
        else {
            holder.like_icon.setImageResource(R.drawable.like)
        }

        /*if(listFeedData!!.get(position).commentsCount>1){
            holder.txt_view_more_comments.visibility = View.VISIBLE
        }
        else{
            holder.txt_view_more_comments.visibility = View.GONE
        }*/

        if(listFeedData!!.get(position).tags.size > 0){
            holder.txt_tagged_user.visibility = View.VISIBLE
            //holder.txt_is_with.visibility = View.VISIBLE
            holder.img_is_with.visibility = View.VISIBLE
            holder.txt_tagged_user.text = listFeedData!!.get(position).tags.get(0).username
        }
        else{
            holder.txt_tagged_user.visibility = View.GONE
            holder.img_is_with.visibility = View.GONE
            //holder.txt_is_with.visibility = View.GONE
        }

        if(listFeedData!!.get(position).commentsCount>0){
            holder.lyt_comment.visibility = View.VISIBLE
            val requestCommentOptionsAvatar = RequestOptions()
            requestCommentOptionsAvatar.placeholder(R.drawable.profile_avatar)
            requestCommentOptionsAvatar.error(R.drawable.profile_avatar)

            if(listFeedData?.get(position)!!.comments.get(listFeedData?.get(position)!!.comments.size-1).user.profile.avatar!=null) {
                Glide.with(mContext).setDefaultRequestOptions(requestCommentOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + listFeedData?.get(position)!!.comments.get(listFeedData?.get(position)!!.comments.size-1).user.id + "/" + listFeedData?.get(position)!!.comments.get(listFeedData?.get(position)!!.comments.size-1).user.profile.avatar).into(holder.comment_profile_image)
                Log.d("userProfileImage",""+listFeedData?.get(position)!!.user.profile.userId + "/" + listFeedData?.get(position)!!.user.profile.avatar)
            }
            else
            {
                Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(holder.comment_profile_image)
            }
            holder.comment_user_name.text = listFeedData!!.get(position).comments.get(listFeedData?.get(position)!!.comments.size-1).user.username
            holder.comment_time_ago.text = listFeedData!!.get(position).comments.get(listFeedData?.get(position)!!.comments.size-1).createdAt
            holder.txt_comment_content.text = listFeedData!!.get(position).comments.get(listFeedData?.get(position)!!.comments.size-1).content
        }
        else{
            holder.lyt_comment.visibility = View.GONE
        }

        holder.user_name.text = listFeedData!!.get(position).user.username
        holder.time_ago.text = listFeedData!!.get(position).createdAt
        holder.comment_txt.text = listFeedData!!.get(position).commentsCount.toString()
        holder.like_txt.text = listFeedData!!.get(position).likescount.toString()

        var content = listFeedData!!.get(position).content
        holder.txt_content.text = content


        holder.btn_send_msg.setOnClickListener {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(holder.edt_msg.text.toString().equals("")){
                Toasty.error(mContext,"Enter Comment first.").show()
                //holder.edt_msg.onEditorAction(EditorInfo.IME_ACTION_DONE)
                imm.hideSoftInputFromWindow(holder.view.windowToken, 0)
            }
            else {
                addComment(holder.edt_msg.text.toString(), listFeedData!!.get(position).id.toString(), mContext, position)
                listFeedData!!.get(position).commentsCount += 1
                holder.comment_txt.text = listFeedData!!.get(position).commentsCount.toString()
                //holder.edt_msg.onEditorAction(EditorInfo.IME_ACTION_DONE)
                holder.edt_msg.text.clear()
                imm.hideSoftInputFromWindow(holder.view.windowToken, 0)

            }
        }

        holder.like_icon.setOnClickListener {
            var imageFlat = listFeedData?.get(position)?.isLiked
            if(imageFlat!!) {
                likeAndUnlike(listFeedData!!.get(position).id,mContext,position)
                holder.like_icon.setImageResource(R.drawable.like)
                listFeedData?.get(position)?.isLiked = false
                listFeedData!!.get(position).likescount -= 1
                    holder.like_txt.text = (listFeedData!!.get(position).likescount).toString()

            }
            else {
                likeAndUnlike(listFeedData!!.get(position).id,mContext,position)
                holder.like_icon.setImageResource(R.drawable.ic_liked)
                listFeedData?.get(position)?.isLiked = true
                listFeedData!!.get(position).likescount += 1
                holder.like_txt.text = (listFeedData!!.get(position).likescount).toString()
                if(Prefs.getString(Constant.USERID,"").toInt() != listFeedData!![position].user.id) {
                    val myName = Prefs.getString(Constant.NAME,"")

                    if(listFeedData!![position].user.device_token != null) {
                        Utils.sentSimpleNotification(mContext,"Foodee","$myName likes your post",listFeedData!![position].user.device_token,"nothing")

                    }

                }
            }
        }

        holder.chat_bubble_icon.setOnClickListener {
            Hawk.put("DetailPost",listFeedData!!.get(position))
            mContext.startActivity(Intent(mContext, TimelinePostDetailActivity::class.java))
        }

        holder.imageSlider.setOnClickListener {
            Hawk.put("DetailPost",listFeedData!!.get(position))
            mContext.startActivity(Intent(mContext, TimelinePostDetailActivity::class.java))
        }

        holder.txt_content.setOnClickListener {
            Hawk.put("DetailPost",listFeedData!!.get(position))
            mContext.startActivity(Intent(mContext, TimelinePostDetailActivity::class.java))
        }

        /*holder.txt_view_more_comments.setOnClickListener {
            Hawk.put("DetailPost",listFeedData!!.get(position))
            mContext.startActivity(Intent(mContext, TimelinePostDetailActivity::class.java))
        }*/


        holder.profile_image.setOnClickListener {
            if (!isMyProfile) {
                if (listFeedData!!.get(position).userId.toString().equals(Prefs.getString(Constant.USERID, ""))) {
                    //val activity: HomeActivity = mContext as HomeActivity
                    //val myFragment = ProfileFragment()
                    //activity.supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, myFragment).addToBackStack(null).commit()
                    val intent = Intent(mContext, HomeActivity::class.java)
                    mContext.startActivity(intent)
                    Prefs.putBoolean("comingFromTimelineAdapter",true)
                } else {
                    val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
                    intent.putExtra("id", listFeedData!!.get(position).userId.toString())
                    mContext.startActivity(intent)
                }
            }
        }

        holder.user_name.setOnClickListener {
            if (!isMyProfile) {
                if (listFeedData!!.get(position).userId.toString().equals(Prefs.getString(Constant.USERID, ""))) {
                    //val activity: HomeActivity = mContext as HomeActivity
                    //val myFragment = ProfileFragment()
                    //activity.supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, myFragment).addToBackStack(null).commit()
                    val intent = Intent(mContext, HomeActivity::class.java)
                    mContext.startActivity(intent)
                    Prefs.putBoolean("comingFromTimelineAdapter",true)
                } else {
                    val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
                    intent.putExtra("id", listFeedData!!.get(position).userId.toString())
                    mContext.startActivity(intent)
                }
            }
        }

        holder.txt_tagged_user.setOnClickListener {
            val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
            intent.putExtra("id", listFeedData!!.get(position).tags.get(0).pivot.userId.toString())
            mContext.startActivity(intent)
        }

        holder.comment_profile_image.setOnClickListener {
            if (listFeedData!!.get(position).comments.get(listFeedData?.get(position)!!.comments.size-1).user.id.toString().equals(Prefs.getString(Constant.USERID, ""))) {
                val activity: HomeActivity = mContext as HomeActivity
                val myFragment = ProfileFragment()
                activity.supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, myFragment).addToBackStack(null).commit()
            } else {
                val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
                intent.putExtra("id", listFeedData!!.get(position).comments.get(listFeedData?.get(position)!!.comments.size-1).user.id.toString())
                mContext.startActivity(intent)
            }
        }

        holder.comment_user_name.setOnClickListener {
            if (listFeedData!!.get(position).comments.get(listFeedData?.get(position)!!.comments.size-1).user.id.toString().equals(Prefs.getString(Constant.USERID, ""))) {
                val activity: HomeActivity = mContext as HomeActivity
                val myFragment = ProfileFragment()
                activity.supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, myFragment).addToBackStack(null).commit()
            } else {
                val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
                intent.putExtra("id", listFeedData!!.get(position).comments.get(listFeedData?.get(position)!!.comments.size-1).user.id.toString())
                mContext.startActivity(intent)
            }
        }
    }

    inner class TimelineHolder(val view: View): RecyclerView.ViewHolder(view) {

        val profile_image:CircleImageView = view.findViewById(R.id.profile_image)
        val chat_bubble_icon:ImageView = view.findViewById(R.id.chat_bubble_icon)
        val img_is_with:ImageView = view.findViewById(R.id.img_is_with)
        val user_name:TextView = view.findViewById(R.id.user_name)
        val time_ago:TextView = view.findViewById(R.id.time_ago)
        val txt_content:TextView = view.findViewById(R.id.txt_content)
        val imageSlider:SliderLayout = view.findViewById(R.id.imageSlider)
        val comment_txt:TextView = view.findViewById(R.id.comment_txt)
        val like_txt:TextView = view.findViewById(R.id.like_txt)
        val edt_msg:EditText = view.findViewById(R.id.edt_msg)
        val btn_send_msg:Button = view.findViewById(R.id.btn_send_msg)
        val like_icon:ImageView = view.findViewById(R.id.like_icon)
        //val txt_view_more_comments:TextView = view.findViewById(R.id.txt_view_more_comments)
        val comment_profile_image:CircleImageView = view.findViewById(R.id.comment_profile_image)
        val comment_user_name:TextView = view.findViewById(R.id.comment_user_name)
        val comment_time_ago:TextView = view.findViewById(R.id.comment_time_ago)
        val txt_comment_content:TextView = view.findViewById(R.id.txt_comment_content)
        val lyt_comment:RelativeLayout = view.findViewById(R.id.lyt_comment)
        val txt_tagged_user:TextView = view.findViewById(R.id.txt_tagged_user)
        val txt_is_with: TextView = view.findViewById(R.id.txt_is_with)
    }

    fun update(list : List<FeedData>?){
        listFeedData = list
        notifyDataSetChanged()
    }

    private fun addComment(content:String,post_id:String,context: Context,position: Int) {
        val mService = ApiUtils.getSOService() as SOService
        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"
        val jsonObject = JSONObject()
        jsonObject.put("content", content)
        jsonObject.put("post_id", post_id)
        mService.addNewComment(hm, Utils.getRequestBody(jsonObject.toString())).enqueue(object : Callback<CommentResponse> {
            override fun onFailure(call: Call<CommentResponse>?, t: Throwable?) {
                Toasty.error(context,"There is a Network Connectivity issue.").show()
            }
            override fun onResponse(call: Call<CommentResponse>?, response: Response<CommentResponse>?) {
                Log.d("content",response!!.body().content)
                Log.d("CreatedAt",response!!.body().createdAt)
                Log.d("UpdatedAt",response!!.body().updatedAt)
                Log.d("post_id",""+response.body().postId)
                Log.d("user_id",""+response.body().userId)
                Log.d("comment_id",""+response.body().id)
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
                listFeedData!!.get(position).comments.add(commentData)
                update(listFeedData)

                if(Prefs.getString(Constant.USERID,"").toInt() != listFeedData!![position].user.id) {
                    val myName = Prefs.getString(Constant.NAME,"")

                    if(listFeedData!![position].user.device_token != null) {
                        Utils.sentSimpleNotification(mContext,"Foodee","$myName commented on your post",listFeedData!![position].user.device_token,"nothing")

                    }

                }
                Toasty.success(context,"Comment Posted Successfully").show()
            }
        })
    }

    private fun likeAndUnlike(post_id:Int,context: Context,position: Int) {
        val mService = ApiUtils.getSOService() as SOService
        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"
        mService.likeAndUnlike(post_id,hm).enqueue(object : Callback<LikeResponse> {
            override fun onFailure(call: Call<LikeResponse>?, t: Throwable?) {
                Toasty.error(context,"There is a Network Connectivity issue.").show()
            }
            override fun onResponse(call: Call<LikeResponse>?, response: Response<LikeResponse>?) {
                Log.d("status",response!!.body().status.toString())
                Log.d("post_count",response!!.body().postCount.toString())
            }
        })
    }



}