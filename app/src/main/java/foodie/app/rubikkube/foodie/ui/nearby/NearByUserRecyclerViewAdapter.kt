package foodie.app.rubikkube.foodie.ui.nearby

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.ChatActivity
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.models.NearByUser
import foodie.app.rubikkube.foodie.utilities.Constants
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class NearByUserRecyclerViewAdapter(private val values: Array<NearByUser>, private val context: Context)
    : RecyclerView.Adapter<NearByUserRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_near_by, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.username ?: "No Name"

        val distance = (item.distance ?: 0.0).roundToInt()
        holder.contentView.text = "$distance km away"

        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        if (item.avatar != null) {
            Glide.with(context).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + item.id + "/" + item.avatar).into(holder.avatar)
        } else {
            Glide.with(context).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(holder.avatar)
        }

        holder.holder.setOnClickListener {
            context.startActivity(
                Intent(context, OtherUserProfileDetailActivity::class.java)
                        .putExtra("id", item.id!!.toString())
            )
        }

        holder.startChat.setOnClickListener {
            Prefs.putString("toUserId", item.id!!.toString())
            Prefs.putString("fromUserId", Prefs.getString(Constants.USER_ID,""))
            Prefs.putString("avatarUser", item.id!!.toString())
            Prefs.putString("userName", item.username!!.toString())
            Prefs.putString("toUserFcmToken", item.deviceToken ?: "")
            context.startActivity(Intent(context, ChatActivity::class.java))
        }
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.nearby_user_name)
        val startChat: ImageView = view.findViewById(R.id.start_chat)
        val avatar: ImageView = view.findViewById(R.id.nearby_image)
        val contentView: TextView = view.findViewById(R.id.nearby_user_distance)
//        val contentView: TextView = view.findViewById(R.id.nearby_user_distance)

        val holder: RelativeLayout = view.findViewById(R.id.nearby_user)
    }
}