package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
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
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.NotificationCenter
import foodie.app.rubikkube.foodie.utilities.Utils

class NotificationCenterAdapter(context: Context, list : List<NotificationCenter>?)  : RecyclerView.Adapter<NotificationCenterAdapter.NotificationCenterHolder>() {
    private val mContext = context
    private var notificationList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationCenterHolder {

        val inflater = LayoutInflater.from(parent.context)
        return NotificationCenterHolder(inflater.inflate(R.layout.notification_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return notificationList!!.size
    }

    override fun onBindViewHolder(holder: NotificationCenterHolder, position: Int) {
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)
        if (notificationList!![position].user.profile.avatar!= null) {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + notificationList!![position].author.profile.userId+ "/" + notificationList!![position].author.profile.avatar).into(holder.userImg)
        } else {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(holder.userImg)
        }

        val spannable = SpannableStringBuilder(notificationList!![position].user.username +" "+notificationList!![position].message)
        spannable.setSpan( android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, notificationList!![position].user.username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannable.setSpan( ForegroundColorSpan(Color.BLACK), 0, notificationList!![position].user.username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        holder.txtNotificationMessage.text = spannable
        holder.txtMinsAgo.text = notificationList!![position].createdAt
        holder.view.setOnClickListener {
//           val intent = Intent(mContext, ChatActivity::class.java)
//           mContext.startActivity(intent)
        }
    }

    inner class NotificationCenterHolder(val view: View) : RecyclerView.ViewHolder(view){
        val userImg: ImageView = view.findViewById(R.id.user_img)
        val txtNotificationMessage: TextView = view.findViewById(R.id.txt_notification_message)
        val txtMinsAgo: TextView = view.findViewById(R.id.txt_mins_ago)
    }

    fun update(updateNotificationList : List<NotificationCenter>){
        notificationList = updateNotificationList.asReversed()
        notifyDataSetChanged()
    }
}