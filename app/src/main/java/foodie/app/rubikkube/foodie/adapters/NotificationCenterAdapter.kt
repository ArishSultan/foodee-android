package foodie.app.rubikkube.foodie.adapters

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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import foodie.app.rubikkube.foodie.apiUtils.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.models.NotificationCenter
import foodie.app.rubikkube.foodie.models.SimpleResponse
import foodie.app.rubikkube.foodie.utilities.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class NotificationCenterAdapter(context: Context, list : MutableList<NotificationCenter>?)  : androidx.recyclerview.widget.RecyclerView.Adapter<NotificationCenterAdapter.NotificationCenterHolder>() {
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
        if (notificationList!![position].user!!.profile!!.avatar!= null) {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + notificationList!![position].user!!.profile!!.userId+ "/" + notificationList!![position].user!!.profile!!.avatar).into(holder.userImg)
        } else {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(holder.userImg)
        }

        val spannable = SpannableStringBuilder(notificationList!![position].user.username +" "+notificationList!![position].message)
        spannable.setSpan( android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, (notificationList!![position].user.username ?: "").length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannable.setSpan( ForegroundColorSpan(Color.BLACK), 0, (notificationList!![position].user.username ?: "").length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        holder.txtNotificationMessage.text = spannable
        holder.txtMinsAgo.text = notificationList!![position].createdAt
        holder.view.setOnClickListener {
            if (holder.txtNotificationMessage.text.contains("Reviewed")) {
                val intent = Intent(mContext, OtherUserProfileDetailActivity::class.java).putExtra("id", Prefs.getString(Constants.USER_ID, ""))
                mContext.startActivity(intent)
            } else {
                val intent = Intent(mContext, TimelinePostDetailActivity::class.java)
                intent.putExtra("PostID", notificationList!![position].postId.toString())
                mContext.startActivity(intent)

            }
        }

        holder.rel.setOnLongClickListener {


            // Initialize a new instance of
            val builder = AlertDialog.Builder(mContext)

            // Set the alert dialog title
            builder.setTitle("Delete Notification")

            // Display a message on alert dialog
            builder.setMessage("Are sure you want to delete seleted notification.")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("DELETE"){dialog, which ->
                // Do something when user press the positive button
                DeleteSingleNotification(notificationList!![position].id,notificationList!![position],position)

            }


            // Display a negative button on alert dialog
            builder.setNegativeButton("CANCEL"){dialog,which ->
                Toast.makeText(mContext,"You are not agree.",Toast.LENGTH_SHORT).show()
            }




            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()


            return@setOnLongClickListener  false
        }
    }

    inner class NotificationCenterHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
        val userImg: ImageView = view.findViewById(R.id.user_img)
        val txtNotificationMessage: TextView = view.findViewById(R.id.txt_notification_message)
        val txtMinsAgo: TextView = view.findViewById(R.id.txt_mins_ago)
        val rel: RelativeLayout = view.findViewById(R.id.rel)
    }

    fun update(updateNotificationList : MutableList<NotificationCenter>){
        notificationList = updateNotificationList.asReversed()
        notifyDataSetChanged()
    }

    fun removeNotif(){
        notificationList?.clear()
        notifyDataSetChanged()
    }


    private fun DeleteSingleNotification(notifId : Int, notification : NotificationCenter, position: Int) {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.deleteSingleNotification(notifId,hm)
                .enqueue(object : Callback<SimpleResponse> {
                    override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                        Toasty.error(mContext!!,"Something went wrong", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {

                        if(response?.isSuccessful!!) {

                            notificationList?.remove(notification)
                            notifyDataSetChanged()

                        }else{
                            Toasty.error(mContext!!,response.message(), Toast.LENGTH_LONG).show()

                        }
                    }

                })


    }

}