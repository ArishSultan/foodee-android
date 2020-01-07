package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Like


class WhoLikesMyPostAdapter(context: Context, list : List<Like>?)  : androidx.recyclerview.widget.RecyclerView.Adapter<WhoLikesMyPostAdapter.NotificationCenterHolder>() {
    private val mContext = context
    private var mList = list

    val requestOptionsAvatar : RequestOptions? = null

    init {
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationCenterHolder {

        val inflater = LayoutInflater.from(parent.context)
        return NotificationCenterHolder(inflater.inflate(R.layout.notification_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }
    override fun onBindViewHolder(holder: NotificationCenterHolder, position: Int) {


        Glide.with(mContext).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + mList!![position].profile.userId + "/" + mList!![position].profile.avatar).apply(RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)).into(holder.userImg!!)



        val spannable = SpannableStringBuilder(mList!![position].username +" "+" liked your post")
        spannable.setSpan( android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, mList!![position].username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannable.setSpan( ForegroundColorSpan(Color.BLACK), 0, mList!![position].username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        holder.txtNotificationMessage.text = spannable
        holder.txtMinsAgo.text = mList!![position].createdAt
        holder.view.setOnClickListener {
            //Prefs.putString("NotificationPostID", notificationList!![position].postId.toString())
            val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java)
            intent.putExtra("id", mList!![position].profile.userId.toString())
            mContext.startActivity(intent)
        }
    }

    inner class NotificationCenterHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
        val userImg: ImageView = view.findViewById(R.id.user_img)
        val txtNotificationMessage: TextView = view.findViewById(R.id.txt_notification_message)
        val txtMinsAgo: TextView = view.findViewById(R.id.txt_mins_ago)
    }

    fun update(updateNotificationList : List<Like>){
        this.mList = updateNotificationList
        notifyDataSetChanged()
    }
}