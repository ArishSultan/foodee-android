package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.ChatActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.InboxListResponse
import foodie.app.rubikkube.foodie.utilities.Utils

class ChatInboxListAdapter(context: Context, list : List<InboxListResponse>?)  : RecyclerView.Adapter<ChatInboxListAdapter.ChatInboxHolder>() {
    val mContext = context
    var inboxUserList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatInboxHolder {

        val inflater = LayoutInflater.from(parent.context)
        return ChatInboxHolder(inflater.inflate(R.layout.message_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return inboxUserList!!.size
    }

    override fun onBindViewHolder(holder: ChatInboxHolder, position: Int) {

        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)
        if (inboxUserList!!.get(position).avatar != null) {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + inboxUserList!![position].userId + "/" + inboxUserList!![position].avatar).into(holder.userImg)
        } else {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(holder.userImg)
        }
        holder.txtUsername.text = inboxUserList!![position].username
        holder.txtStatus.text = inboxUserList!![position].message
        holder.mins_ago.text = Utils.timeAgo(inboxUserList!![position].created_at)
        holder.view.setOnClickListener {
            Prefs.putString("toUserId",inboxUserList!![position].userId.toString())
            Prefs.putString("avatarUser",inboxUserList!![position].userId.toString())
            Prefs.putString("threadId", inboxUserList!![position].id.toString())
            Prefs.putString("userName",inboxUserList!![position].username)
            Prefs.putString("avatar",inboxUserList!![position].avatar)
            val intent = Intent(mContext, ChatActivity::class.java)
            mContext.startActivity(intent)
        }
    }

    inner class ChatInboxHolder(val view: View) : RecyclerView.ViewHolder(view){
        val userImg: ImageView = view.findViewById(R.id.user_img)
        val txtUsername: TextView = view.findViewById(R.id.txt_username)
        val txtStatus: TextView = view.findViewById(R.id.txt_status)
        val numOfMsgs: TextView = view.findViewById(R.id.num_of_msgs)
        val mins_ago: TextView = view.findViewById(R.id.mins_ago)
    }

    fun update(updateInboxList : List<InboxListResponse>){
        inboxUserList = updateInboxList
        notifyDataSetChanged()
    }
}