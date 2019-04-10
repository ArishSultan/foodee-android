package app.yasirameen.life.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.model.Chats

import java.util.*


class ChatAdapter(internal var context: Context,private var user_dp: Int?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    internal var inflater: LayoutInflater = LayoutInflater.from(context)
    internal var chatList: MutableList<Chats> = ArrayList()
    val options =  RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var item_view: View?
        var holder: RecyclerView.ViewHolder? = null
        if (viewType == 0) {
            item_view = inflater.inflate(R.layout.chat_list_item_sender, parent, false)
            holder = SenderViewHolder(item_view)
        } else if (viewType == 1) {
            item_view = inflater.inflate(R.layout.chat_list_item_reciever, parent, false)
            holder = ReceiverViewHolder(item_view)
        }


        return holder!!
    }

    override fun getItemCount(): Int {
        return this.chatList?.size!!
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val chats = this.chatList!![position]

        if (holder is ReceiverViewHolder) {

            holder._tv_reciever_msg?.text = chats.getMessage()
            //holder._tv_reciever_time?.text = chats.getTime()
            Glide.with(context).load(user_dp).apply(options).into(holder._img_reciever_dp!!)


        } else if (holder is SenderViewHolder) {

            holder._tv_sender_msg?.text = chats.getMessage()


        }

        /*else if(holder is MessagesTimeViewHolder) {

           var previousTs: Long? = 0
           if(position >= 1) {

               val PreviousChatMsg =  chatList.get(position - 1)
               previousTs = PreviousChatMsg.createdAt?.toLong()
           }
           val cal1 = Calendar.getInstance()
           val cal2 = Calendar.getInstance()
           cal1.timeInMillis = chatList.get(position).createdAt?.toLong()?.times(1000)!!
           cal2.timeInMillis = previousTs?.times(1000)!!
           //holder._group_msg_time_view
       }

*/
    }


    override fun getItemViewType(position: Int): Int {


        val chatmessage = this.chatList!![position]
        return  if(chatmessage.getSender()!! % 2 == 0) {
            1
        } else {
            0
        }


    }
    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        internal  var _tv_reciever_msg: TextView? = null
        internal  var _img_reciever_dp: CircleImageView? = null
        internal  var _tv_reciever_time: TextView? = null
        internal  var _tv_reciever_isseen: TextView? = null



        init {

            _tv_reciever_msg = itemView.findViewById<View>(R.id.tv_reciever_msg) as TextView?
            _tv_reciever_time = itemView.findViewById<View>(R.id.tv_reciever_time) as TextView?
            _img_reciever_dp = itemView.findViewById<View>(R.id.img_reciever_dp) as CircleImageView?

            //_tv_reciever_isseen = itemView.findViewById(R.id.tv_reciever_isseen) as TextView?

        }
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        internal var _tv_sender_msg: TextView? = null
        internal var _tv_message_time: TextView? = null
        internal var _img_waiting_clock: ImageView? = null


        init {

            _tv_sender_msg = itemView.findViewById<View>(R.id.tv_sender_msg) as TextView?
            _tv_message_time = itemView.findViewById<View>(R.id.tv_message_time) as TextView?
            _img_waiting_clock = itemView.findViewById<View>(R.id.img_status) as ImageView?

        }
    }

    fun setMessages(messages: MutableList<Chats>) {
        this.chatList = messages
        notifyDataSetChanged()

    }

    fun updateMoreMessages(chatList: MutableList<Chats>) {


    }

//    fun updateMessageId(randomId: String, id: Int?) {
//
//        for(chats in chatList) {
//
//            if(chats.randomId.equals(randomId)) {
//                chats.id = id
//            }
//        }
//        notifyDataSetChanged()
//    }
//
//    fun marMessageRead() {
//
//        chatList.filter { it -> it.isRead == 0 }.map {  it -> it.isRead = 1}.let { notifyDataSetChanged() }
//    }

    fun setSingleMessage(singleChatMessage: Chats) {


        this.chatList?.add(singleChatMessage)
        notifyDataSetChanged()

        /*if(this.chatList.size == 0) {
            this.chatList?.add(singleChatMessage)
            notifyDataSetChanged()

        }else {
            this.chatList?.add(chatList.size - 1,singleChatMessage)
            notifyDataSetChanged()
        }*/

    }

}
