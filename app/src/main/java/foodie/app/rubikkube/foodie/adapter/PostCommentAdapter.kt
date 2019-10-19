package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.HomeActivity
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.classes.ObservableObject
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.model.CommentData
import foodie.app.rubikkube.foodie.model.FeedData
import foodie.app.rubikkube.foodie.utilities.Constant
import android.app.Activity
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.model.DeleteFoodAndPostResponse
import foodie.app.rubikkube.foodie.model.SimpleResponse
import foodie.app.rubikkube.foodie.utilities.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


class PostCommentAdapter(context: Context, listCommentData : MutableList<CommentData>?)  : androidx.recyclerview.widget.RecyclerView.Adapter<PostCommentAdapter.PostCommentHolder>() {

    val mContext = context
    var listCommentData = listCommentData!!
    var postFeedData : FeedData? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCommentAdapter.PostCommentHolder {

        val inflater = LayoutInflater.from(parent?.context)
        return PostCommentHolder(inflater.inflate(R.layout.holder_comment_item, parent, false))

    }

    override fun getItemCount(): Int {
        return listCommentData!!.size
    }

    override fun onBindViewHolder(holder: PostCommentAdapter.PostCommentHolder, position: Int) {

        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.food_bg)
        requestOptionsAvatar.error(R.drawable.food_bg)
        if (listCommentData!!.get(position).user.profile.avatar != null) {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + listCommentData!!.get(position).user.profile.userId + "/" + listCommentData!!.get(position).user.profile.avatar).into(holder.profile_image)
            Log.d("Avatar",ApiUtils.BASE_URL + "/storage/media/avatar/" + listCommentData!!.get(position).user.profile.userId + "/" + listCommentData!!.get(position).user.profile.avatar)
        }
        else{
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(holder.profile_image)
        }
        holder.user_name.text = listCommentData!![position].user.username
        holder.time_ago.text = listCommentData!![position].createdAt
        holder.txt_content.text = listCommentData!![position].content
        Log.d("UserName", listCommentData!![position].user.username)
        Log.d("timeAgo", listCommentData!![position].createdAt)
        Log.d("Content", listCommentData!![position].content)





        if(Prefs.getString(Constant.USERID, "").equals(postFeedData?.userId.toString())) {

            holder.img_delete_comment.visibility = View.VISIBLE


        }else {
            holder.img_delete_comment.visibility = View.GONE

        }

        holder.img_delete_comment.setOnClickListener {

            deleteComment(listCommentData[position].id,position)
        }

        holder.profile_image.setOnClickListener {
            if (listCommentData!!.get(position).user.id.toString() == Prefs.getString(Constant.USERID, "")) {
                Prefs.putBoolean("comingPostCommentAdapter",true)
                val intent = Intent(mContext, HomeActivity::class.java)
                mContext.startActivity(intent)

            } else {
                val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
                intent.putExtra("id", listCommentData!!.get(position).user.id.toString())
                mContext.startActivity(intent)
            }
        }

        holder.user_name.setOnClickListener {
            if (listCommentData!!.get(position).user.id.toString().equals(Prefs.getString(Constant.USERID, ""))) {
                Prefs.putBoolean("comingPostCommentAdapter",true)
                val intent = Intent(mContext, HomeActivity::class.java)
                mContext.startActivity(intent)
                //(mContext as Activity).finish()

            } else {
                val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
                intent.putExtra("id", listCommentData!!.get(position).user.id.toString())
                mContext.startActivity(intent)
                //(mContext as Activity).finish()
            }
        }
    }

    inner class PostCommentHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val profile_image: ImageView = view.findViewById(R.id.profile_image)
        val user_name: TextView = view.findViewById(R.id.user_name)
        val time_ago: TextView = view.findViewById(R.id.time_ago)
        val txt_content: TextView = view.findViewById(R.id.txt_content)
        val img_delete_comment: ImageView = view.findViewById(R.id.img_delete_comment)
    }

    fun update(listCommentData: MutableList<CommentData>){
        this.listCommentData = listCommentData
        notifyDataSetChanged()
    }

    fun updateFeed(postFeedData: FeedData){
        this.postFeedData = postFeedData
        notifyDataSetChanged()
    }

    private fun deleteComment(commentID: Int,position: Int) {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        val jsonObject = JSONObject()
        jsonObject.put("_method", "DELETE")
        mService.deleteComment(commentID, hm,Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<SimpleResponse> {
                    override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                        Toasty.error(mContext, "" + t!!.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {

                        if(response?.isSuccessful!!) {

                            listCommentData.removeAt(position)
                            notifyDataSetChanged()
                        }
                    }

                })





//                .enqueue(object : Callback<DeleteFoodAndPostResponse> {
//                    override fun onFailure(call: Call<DeleteFoodAndPostResponse>?, t: Throwable?) {
//                        Toasty.error(this@TimelinePostDetailActivity, "" + t!!.message, Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onResponse(call: Call<DeleteFoodAndPostResponse>?, andPostResponse: Response<DeleteFoodAndPostResponse>?) {
//                        if (andPostResponse!!.isSuccessful) {
//                            deletePostResponse = andPostResponse.body()
//                            Toasty.success(this@TimelinePostDetailActivity, deletePostResponse.message, Toast.LENGTH_SHORT).show()
//                            startActivity(Intent(this@TimelinePostDetailActivity, HomeActivity::class.java))
//                            finish()
//                        }
//                    }
//                })

    }

}