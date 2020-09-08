package foodie.app.rubikkube.foodie.adapters

import java.util.*
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.models.Chats

class ChatAdapter(internal var context: Context,private var user_dp: Int?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var chatsList: MutableList<Chats> = ArrayList()
    internal var inflater: LayoutInflater = LayoutInflater.from(context)

    private val options =  RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .priority(Priority.HIGH)
            .centerCrop()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder? = when (viewType) {
            0 -> SenderViewHolder(inflater.inflate(R.layout.chat_list_item_sender, parent, false))
            1 -> ReceiverViewHolder(inflater.inflate(R.layout.chat_list_item_reciever, parent, false))
            else -> null
        }

        return holder!!
    }

    override fun getItemCount(): Int = this.chatsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chats = this.chatsList[position]

        if (holder is ReceiverViewHolder) {
            holder.message?.text = chats.getMessage()
            Glide.with(context).load(user_dp).apply(options).into(holder.image!!)
        } else if (holder is SenderViewHolder) {
            holder.message?.text = chats.getMessage()
        }
    }

    override fun getItemViewType(position: Int): Int = if (this.chatsList[position].getSender()!! % 2 == 0) 1 else 1

    fun setMessages(messages: MutableList<Chats>) {
        this.chatsList = messages
        notifyDataSetChanged()
    }

    fun setSingleMessage(singleChatMessage: Chats) {
        this.chatsList.add(singleChatMessage)
        notifyDataSetChanged()
    }

    open inner class Participant(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal var time = itemView.findViewById<View>(R.id.tv_reciever_time) as TextView?
        internal var image = itemView.findViewById<View>(R.id.img_reciever_dp) as CircleImageView?
        internal var message = itemView.findViewById<View>(R.id.tv_reciever_msg) as TextView?
    }

    inner class SenderViewHolder(itemView: View): Participant(itemView)
    inner class ReceiverViewHolder(itemView: View): Participant(itemView)
}
