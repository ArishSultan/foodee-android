package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.CommentResponse
import foodie.app.rubikkube.foodie.model.FeedData
import foodie.app.rubikkube.foodie.model.LikeResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TimelineAdapter(context: Context, feedDate: List<FeedData>?) : RecyclerView.Adapter<TimelineAdapter.TimelineHolder>(){

    val mContext = context
    var listFeedData = feedDate
    var imageFlat = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineHolder {
        val inflater = LayoutInflater.from(parent?.context)
        Hawk.init(mContext).build();
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

        if(listFeedData!!.get(position).commentsCount>0){
            holder.txt_view_more_comments.visibility = View.VISIBLE
        }
        else{
            holder.txt_view_more_comments.visibility = View.GONE
        }

        holder.user_name.text = listFeedData!!.get(position).user.username
        holder.txt_content.text = listFeedData!!.get(position).content
        holder.time_ago.text = listFeedData!!.get(position).createdAt
        holder.comment_txt.text = listFeedData!!.get(position).commentsCount.toString()
        holder.like_txt.text = listFeedData!!.get(position).likescount.toString()

        holder.btn_send_msg.setOnClickListener {
            addComment(holder.edt_msg.text.toString(),listFeedData!!.get(position).id.toString(),mContext,position)
            holder.edt_msg.text.clear()
        }

        holder.like_icon.setOnClickListener {

            var imageFlat = listFeedData?.get(position)?.isLiked
            if(imageFlat!!) {
                likeAndUnlike(listFeedData!!.get(position).id,mContext,position)
                holder.like_icon.setImageResource(R.drawable.like)
                listFeedData?.get(position)?.isLiked = false
            }
            else {
                likeAndUnlike(listFeedData!!.get(position).id,mContext,position)
                holder.like_icon.setImageResource(R.drawable.ic_liked)
                listFeedData?.get(position)?.isLiked = true
            }
        }

        holder.txt_view_more_comments.setOnClickListener {
            Hawk.put("DetailPost",listFeedData!!.get(position))
            mContext.startActivity(Intent(mContext, TimelinePostDetailActivity::class.java))
        }
    }

    inner class TimelineHolder(val view: View): RecyclerView.ViewHolder(view) {

        val profile_image:CircleImageView = view.findViewById(R.id.profile_image)
        val user_name:TextView = view.findViewById(R.id.user_name)
        val time_ago:TextView = view.findViewById(R.id.time_ago)
        val imageSlider:SliderLayout = view.findViewById(R.id.imageSlider)
        val txt_content:TextView = view.findViewById(R.id.txt_content)
        val comment_txt:TextView = view.findViewById(R.id.comment_txt)
        val like_txt:TextView = view.findViewById(R.id.like_txt)
        val edt_msg:EditText = view.findViewById(R.id.edt_msg)
        val btn_send_msg:Button = view.findViewById(R.id.btn_send_msg)
        val like_icon:ImageView = view.findViewById(R.id.like_icon)
        val txt_view_more_comments:TextView = view.findViewById(R.id.txt_view_more_comments)
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
                Toasty.success(context,"Comment Posted Successfully").show()
            }
        })
    }

    private fun likeAndUnlike(post_id:Int,context: Context,position: Int) {
        val mService = ApiUtils.getSOService() as SOService
        val hm = java.util.HashMap<String, String>()
        var flag = false
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