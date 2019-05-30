package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.CommentData
import foodie.app.rubikkube.foodie.model.FeedData

class PostCommentAdapter(context: Context, listCommentData : List<CommentData>?)  : RecyclerView.Adapter<PostCommentAdapter.PostCommentHolder>() {

    val mContext = context
    var listCommentData = listCommentData

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
        holder.user_name.text = listCommentData!!.get(position).user.username
        holder.time_ago.text = listCommentData!!.get(position).createdAt
        holder.txt_content.text = listCommentData!!.get(position).content
        Log.d("UserName",listCommentData!!.get(position).user.username)
        Log.d("timeAgo",listCommentData!!.get(position).createdAt)
        Log.d("Content",listCommentData!!.get(position).content)

        holder.profile_image.setOnClickListener {
            var intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
            intent.putExtra("id", listCommentData!!.get(position).user.id.toString())
            mContext.startActivity(intent)
        }

        holder.user_name.setOnClickListener {
            var intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
            intent.putExtra("id", listCommentData!!.get(position).user.id.toString())
            mContext.startActivity(intent)
        }
    }

    inner class PostCommentHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val profile_image: ImageView = view.findViewById(R.id.profile_image)
        val user_name: TextView = view.findViewById(R.id.user_name)
        val time_ago: TextView = view.findViewById(R.id.time_ago)
        val txt_content: TextView = view.findViewById(R.id.txt_content)
    }

    fun update(listCommentData: List<CommentData>?){
        this.listCommentData = listCommentData
        notifyDataSetChanged()
    }
}