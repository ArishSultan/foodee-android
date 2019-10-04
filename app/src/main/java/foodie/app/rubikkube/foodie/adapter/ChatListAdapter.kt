package foodie.app.rubikkube.foodie.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pixplicity.easyprefs.library.Prefs
import de.hdodenhof.circleimageview.CircleImageView
import foodie.app.rubikkube.foodie.JavaUtils
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.MessageListResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils


private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2
class ChatListAdapter (val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<MessageViewHolder>() {
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
            //timeText.text = Utils.fromMillisToTimeString(message.updatedAt.toLong())
            timeText.text = message.updatedAt

            var url = JavaUtils.checkUserId(message.message)
            if(!url.equals("")) {
                messageText.text = message.message

                messageText.makeLinks(Pair(url,View.OnClickListener {

                    val mURl = JavaUtils.checkUserId(url)
                    val uID = mURl?.split("/")
                    val intent = Intent(context, OtherUserProfileDetailActivity::class.java)
                    intent.putExtra("id", uID!![4])
                    context.startActivity(intent)
                }))

            }else {
                messageText.text = message.message

            }

            messageText.setOnLongClickListener {

                val clipboard = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("msg", message.message)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(context,"text message copied..",Toast.LENGTH_SHORT).show()

                return@setOnLongClickListener true
            }

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

            var url = JavaUtils.checkUserId(message.message)
            if(!url.equals("")) {
                messageText.text = message.message

                messageText.makeLinks(Pair(url,View.OnClickListener {


                    val mURl = JavaUtils.checkUserId(url)
                    val uID = mURl?.split("/")
                    val intent = Intent(context, OtherUserProfileDetailActivity::class.java)
                    intent.putExtra("id", uID!![4])
                    context.startActivity(intent)

                }))

            }else {
                messageText.text = message.message

            }
            timeText.text = message.updatedAt
            if (message.receiver.profile.avatar != null) {
                Glide.with(context).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + message.messageSender.profile.userId+ "/" + message.messageSender.profile.avatar).into(profileImage)
            } else {
                Glide.with(context).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(profileImage)
            }


            messageText.setOnLongClickListener {

                val clipboard = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("msg", message.message)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(context,"text message copied..",Toast.LENGTH_SHORT).show()

                return@setOnLongClickListener true
            }
        }
    }



    fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}



open class MessageViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    open fun bind(message:MessageListResponse) {}
}