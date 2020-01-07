package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.curioustechizen.ago.RelativeTimeTextView
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Like
import foodie.app.rubikkube.foodie.model.ReviewData
import foodie.app.rubikkube.foodie.model.Reviews
import foodie.app.rubikkube.foodie.utilities.Utils
import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException


class WhoLikesBaseAdapter internal constructor(private val context: Context, _reviewList : List<Like>) : BaseAdapter() {
    private var inflater: LayoutInflater? = null
    private var likesList : List<Like>? = null


    var requestOptionsAvatar : RequestOptions? = null

    init {
        requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar?.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar?.error(R.drawable.profile_avatar)

        inflater = LayoutInflater.from(context)
        likesList = _reviewList

    }

    override fun getCount(): Int {
        return likesList?.size!!
    }

    override fun getItem(i: Int): Any {
        return likesList?.size!!
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertview: View?, viewGroup: ViewGroup): View {
        var convertview = convertview
        val holder: ViewHolder
        if (convertview == null) {

            convertview = inflater!!.inflate(R.layout.notification_list_item, null)
            holder = ViewHolder()


            holder.userImg = convertview.findViewById(R.id.user_img)
            holder.txtNotificationMessage = convertview.findViewById(R.id.txt_notification_message)
            holder.txtMinsAgo = convertview.findViewById(R.id.txt_mins_ago)

            convertview!!.tag = holder

        } else {
            holder = convertview.tag as ViewHolder
        }





        if (likesList!![position].profile.avatar!= null) {
            Glide.with(context).setDefaultRequestOptions(requestOptionsAvatar!!).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + likesList!![position].profile.userId+ "/" + likesList!![position].profile.avatar).into(holder?.userImg!!)
        } else {
            Glide.with(context).setDefaultRequestOptions(requestOptionsAvatar!!).load(R.drawable.profile_avatar).into(holder?.userImg!!)
        }

        val spannable = SpannableStringBuilder(likesList!![position].username +" "+" liked your post")
        spannable.setSpan( android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, likesList!![position].username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannable.setSpan( ForegroundColorSpan(Color.BLACK), 0, likesList!![position].username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        holder.txtNotificationMessage?.text = spannable
        holder.txtMinsAgo?.text = likesList!![position].createdAt
//        holder.view.setOnClickListener {
//            //Prefs.putString("NotificationPostID", notificationList!![position].postId.toString())
//            val intent = Intent(mContext, TimelinePostDetailActivity::class.java)
//            intent.putExtra("PostID", mList!![position].pivot.toString())
//            mContext.startActivity(intent)
//        }

//        val review = likesList!![i]
//        holder.rating_bar?.rating = review.rate.toFloat()
//        holder.rating_txt_comment?.text = review.feedback.toString()
//        holder.rating_txt_username?.text = review.user.username
//        holder.rating_txt_timeago?.setReferenceTime(Utils.getTimeinLong(review.createdAt))/* = timesAgo(review.created)*/
//        Glide.with(context).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + review.user.profile.userId + "/" + review.user.profile.avatar).apply(RequestOptions()
//                .centerCrop()
//                .placeholder(R.drawable.avatar)
//                .error(R.drawable.avatar)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .priority(Priority.HIGH)).into(holder.rating_img_user!!)

        return convertview
    }


    inner class ViewHolder {


        var userImg: ImageView? = null
        var txtNotificationMessage: TextView? = null
        var txtMinsAgo: TextView? = null
    }


    }

    private fun timesAgo(createdAt : String) : String {


        val inputFormat =  SimpleDateFormat("mm/dd/yyyy HH:mm:ss")
        //val dateStr = inputFormat.format(createdAt)
        val date = inputFormat.parse(createdAt)


         return DateUtils.getRelativeTimeSpanString(date.time, Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS).toString()
        //val niceDateStr = getTimeAgo(date)


    }


