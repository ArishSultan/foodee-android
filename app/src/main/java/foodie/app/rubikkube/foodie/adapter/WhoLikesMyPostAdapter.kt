package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Like
import foodie.app.rubikkube.foodie.model.NotificationCenter
import foodie.app.rubikkube.foodie.utilities.Utils

class WhoLikesMyPostAdapter(context: Context, list : List<Like>?)  : androidx.recyclerview.widget.RecyclerView.Adapter<WhoLikesMyPostAdapter.NotificationCenterHolder>() {
    private val mContext = context
    private var list = list

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
        return list!!.size
    }
    override fun onBindViewHolder(holder: NotificationCenterHolder, position: Int) {

        if (list!![position].profile.avatar!= null) {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar!!).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + list!![position].profile.userId+ "/" + list!![position].profile.avatar).into(holder.userImg)
        } else {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar!!).load(R.drawable.profile_avatar).into(holder.userImg)
        }

        val spannable = SpannableStringBuilder(list!![position].username +" "+" liked your post")
        spannable.setSpan( android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, list!![position].username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannable.setSpan( ForegroundColorSpan(Color.BLACK), 0, list!![position].username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        holder.txtNotificationMessage.text = spannable
        holder.txtMinsAgo.text = list!![position].createdAt
        holder.view.setOnClickListener {
            //Prefs.putString("NotificationPostID", notificationList!![position].postId.toString())
            val intent = Intent(mContext, TimelinePostDetailActivity::class.java)
            intent.putExtra("PostID", list!![position].pivot.toString())
            mContext.startActivity(intent)
        }
    }

    inner class NotificationCenterHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
        val userImg: ImageView = view.findViewById(R.id.user_img)
        val txtNotificationMessage: TextView = view.findViewById(R.id.txt_notification_message)
        val txtMinsAgo: TextView = view.findViewById(R.id.txt_mins_ago)
    }

    fun update(updateNotificationList : List<Like>){
        list = updateNotificationList.asReversed()
        notifyDataSetChanged()
    }
}