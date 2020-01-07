package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.text.format.DateUtils
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
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.ReviewData
import foodie.app.rubikkube.foodie.model.Reviews
import foodie.app.rubikkube.foodie.utilities.Utils
import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException


class ReviewAdapter internal constructor(private val context: Context, _reviewList : List<ReviewData>) : BaseAdapter() {
    private var inflater: LayoutInflater? = null
    private var reviewList : List<ReviewData>? = null


    init {
        inflater = LayoutInflater.from(context)
        reviewList = _reviewList
    }

    override fun getCount(): Int {
        return reviewList?.size!!
    }

    override fun getItem(i: Int): Any {
        return reviewList?.size!!
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertview: View?, viewGroup: ViewGroup): View {
        var convertview = convertview
        val holder: ViewHolder
        if (convertview == null) {

            convertview = inflater!!.inflate(R.layout.review_list_item, null)
            holder = ViewHolder()
            holder.rating_bar = convertview.findViewById(R.id.ratingBar_rating_stars)
            holder.rating_img_user = convertview.findViewById(R.id.img_rating_user)
            holder.rating_txt_comment = convertview.findViewById(R.id.txt_rating_comment)
            holder.rating_txt_username = convertview.findViewById(R.id.txt_rating_username)
            holder.rating_txt_timeago = convertview.findViewById(R.id.txt_rating_timeago)
            convertview!!.tag = holder

        } else {
            holder = convertview.tag as ViewHolder
        }

        val review = reviewList!![i]
        holder.rating_bar?.rating = review.rate.toFloat()
        holder.rating_txt_comment?.text = review.feedback.toString()
        holder.rating_txt_username?.text = review.user.username
        holder.rating_txt_timeago?.setReferenceTime(Utils.getTimeinLong(review.createdAt))/* = timesAgo(review.created)*/
        Glide.with(context).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + review.user.profile.userId + "/" + review.user.profile.avatar).apply(RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)).into(holder.rating_img_user!!)

        return convertview
    }


    inner class ViewHolder {

        internal var rating_img_user: ImageView? = null
        internal var rating_txt_username: TextView? = null
        internal var rating_txt_comment: TextView? = null
        internal var rating_txt_timeago: RelativeTimeTextView? = null
        internal var rating_bar: RatingBar? = null

    }

    private fun timesAgo(createdAt : String) : String {


        val inputFormat =  SimpleDateFormat("mm/dd/yyyy HH:mm:ss")
        //val dateStr = inputFormat.format(createdAt)
        val date = inputFormat.parse(createdAt)


         return DateUtils.getRelativeTimeSpanString(date.time, Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS).toString()
        //val niceDateStr = getTimeAgo(date)


    }


}