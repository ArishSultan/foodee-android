package foodie.app.rubikkube.foodie.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import app.yasirameen.life.adapter.ChatAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.model.Chats
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.custom_toolbar_for_chatscreen.*


class ChatActivity : AppCompatActivity() {


    val options =  RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)

    internal var chatAdapter: ChatAdapter? =null
    internal var manager: LinearLayoutManager? = null
    internal var chatList: MutableList<Chats>? = ArrayList<Chats>()
    internal var tempChatList: MutableList<Chats>? = ArrayList<Chats>()
    var senderId : Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        /* intent.putExtra("user_dp",item.userDP)
//        intent.putExtra("fcmToken",item.deviceId)
          intent.putExtra("name",item.name)*/

//        val userDP = intent.getIntExtra("user_dp",R.drawable.one)
//        val userName = intent.getStringExtra("name")

        Glide.with(this@ChatActivity).load(R.drawable.one).into(findViewById(R.id.chat_user_img))
        chat_user_name.text = "Johnshah"

        intializeAdapter(R.drawable.one)
        send_message_button.setOnClickListener {


            if(msg_text.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(this@ChatActivity,"Please enter your message....",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else {



                val singleChatMessage = Chats()
                singleChatMessage.setMessage(msg_text.text.toString().trim())
                singleChatMessage.setSender(senderId)
                chatAdapter?.setSingleMessage(singleChatMessage)
                rv_chat?.scrollToPosition(chatAdapter!!.itemCount - 1)
                senderId = senderId?.plus(1)

                msg_text.text = null

            }

        }
    }

    private fun intializeAdapter(userdp: Int?) {

        chatAdapter = ChatAdapter(this@ChatActivity,userdp)
        rv_chat?.adapter = chatAdapter

        manager = LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
        rv_chat?.layoutManager = manager

    }
}
