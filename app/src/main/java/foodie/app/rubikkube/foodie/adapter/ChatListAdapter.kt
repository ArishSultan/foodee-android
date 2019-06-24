package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pixplicity.easyprefs.library.Prefs
import de.hdodenhof.circleimageview.CircleImageView
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.MessageListResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils


private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2
class ChatListAdapter (val context: Context) : RecyclerView.Adapter<MessageViewHolder>() {
    private var messages: ArrayList<MessageListResponse> = ArrayList()

    fun addMessageList(update_messages: ArrayList<MessageListResponse>){
        messages =  update_messages
        notifyDataSetChanged()
    }

    fun addSingleMessage(singleChatMessage: MessageListResponse) {
        this.messages?.add(singleChatMessage)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        return if(!(Prefs.getString(Constant.USERID,"").equals(message.recipientId.toString()))) {
            VIEW_TYPE_MY_MESSAGE
        }
        else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if(viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_list_item_sender, parent, false))
        } else {
            OtherMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_list_item_reciever, parent, false))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages.get(position)

        holder?.bind(message)
    }

    inner class MyMessageViewHolder (view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.findViewById(R.id.tv_sender_msg) as TextView
        private var timeText: TextView = view.findViewById<View>(R.id.tv_message_time) as TextView
        private var messageStatus: ImageView = view.findViewById<View>(R.id.img_status) as ImageView

        override fun bind(message: MessageListResponse) {
            messageText.text = message.message
            //timeText.text = Utils.fromMillisToTimeString(message.updatedAt.toLong())
            timeText.text = message.updatedAt
            //Log.d("avatar sender",ApiUtils.BASE_URL + "/storage/media/avatar/" + message.receiver.id+ "/" + message.receiver.profile.avatar)

        }
    }

    inner class OtherMessageViewHolder (view: View) : MessageViewHolder(view) {
        private var profileImage: CircleImageView = view.findViewById<View>(R.id.img_reciever_dp) as CircleImageView
        private var messageText: TextView = view.findViewById<View>(R.id.tv_reciever_msg) as TextView
        private var timeText: TextView = view.findViewById<View>(R.id.tv_reciever_time) as TextView

        override fun bind(message: MessageListResponse) {
            val requestOptionsAvatar = RequestOptions()
            requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
            requestOptionsAvatar.error(R.drawable.profile_avatar)
            messageText.text = message.message
            timeText.text = message.updatedAt
            if (message.receiver.profile.avatar != null) {
                Glide.with(context).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + message.messageSender.profile.userId+ "/" + message.messageSender.profile.avatar).into(profileImage)
            } else {
                Glide.with(context).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(profileImage)
            }
        }
    }
}

open class MessageViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message:MessageListResponse) {}
}