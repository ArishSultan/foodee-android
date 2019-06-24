package foodie.app.rubikkube.foodie.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ChatListAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Chats
import foodie.app.rubikkube.foodie.model.MessageListResponse
import foodie.app.rubikkube.foodie.model.MessageReceiver
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.custom_toolbar_for_chatscreen.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {


    val options =  RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)

    internal var chatAdapter: ChatListAdapter? =null
    private var manager: LinearLayoutManager? = null
    internal var messageListResponse:MessageListResponse?= null
    internal var chatList: MutableList<Chats>? = ArrayList<Chats>()
    internal var tempChatList: MutableList<Chats>? = ArrayList<Chats>()
    private var fromUserId:String?= null
    internal var toUserId:String?= null
    internal var message:String?= null
    internal var thread_id:String?= null
    private var userID:String?= null
    private var userName:String?= null
    internal var avatar:String?= null
    internal var messageReciever:MessageReceiver?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        fromUserId = Prefs.getString("fromUserId", "")
        toUserId = Prefs.getString("toUserId", "")
        thread_id = Prefs.getString("threadId","")
        userName = Prefs.getString("userName","")
        userID = Prefs.getString("avatarUser","")
        avatar = Prefs.getString("avatar","")
        /* intent.putExtra("user_dp",item.userDP)
//        intent.putExtra("fcmToken",item.deviceId)
          intent.putExtra("name",item.name)*/

//        val userDP = intent.getIntExtra("user_dp",R.drawable.one)
//        val userName = intent.getStringExtra("name")

        if (avatar != null) {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + userID+ "/" + avatar).into(chat_user_img)
        }
        else {
            Glide.with(this@ChatActivity).load(R.drawable.profile_avatar).into(findViewById(R.id.chat_user_img))
        }
        chat_user_name.text = userName
        intializeAdapter()
        getMessageList()
        send_message_button.setOnClickListener {
            message = msg_text.text.toString().trim()
            if(message.equals("")) {
                Toast.makeText(this@ChatActivity,"Please enter your message....",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else {
                sendMessage()
                msg_text.text = null
            }
        }
    }

    private fun intializeAdapter() {
        chatAdapter = ChatListAdapter(this@ChatActivity)
        rv_chat?.adapter = chatAdapter
        manager = LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
        rv_chat?.layoutManager = manager
    }

    fun sendMessage() {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()

        val jsonObject = JSONObject()
        jsonObject.put("from_id", Prefs.getString(Constant.USERID,""))
        jsonObject.put("to_id", toUserId)
        jsonObject.put("message",message)

        val mService = ApiUtils.getSOService() as SOService
        mService.sendMessage(hm, Utils.getRequestBody(jsonObject.toString()))
            .enqueue(object : Callback<MessageListResponse> {
                override fun onResponse(call: Call<MessageListResponse>?, response: Response<MessageListResponse>?) {
                    messageListResponse = MessageListResponse()
                    messageListResponse!!.messageId = thread_id!!.toInt()
                    messageListResponse!!.message = message
                    messageListResponse!!.recipientId = toUserId!!.toInt()
                    messageListResponse!!.updatedAt = "2019-06-20 18:24:30"
                    messageListResponse!!.createdAt = "2019-06-20 18:24:30"
                    messageReciever = MessageReceiver()
                    messageReciever!!.id = toUserId!!.toInt()
                    messageListResponse!!.receiver = messageReciever
                    chatAdapter?.addSingleMessage(messageListResponse!!)
                    rv_chat?.scrollToPosition(chatAdapter!!.itemCount - 1)
                }

                override fun onFailure(call: Call<MessageListResponse>?, t: Throwable?) {
                    Toast.makeText(this@ChatActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun getMessageList() {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()

        val mService = ApiUtils.getSOService() as SOService
        mService.getMessageList(hm, fromUserId!!,toUserId!!)
                .enqueue(object : Callback<ArrayList<MessageListResponse>> {
                    override fun onResponse(call: Call<ArrayList<MessageListResponse>>?, response: Response<ArrayList<MessageListResponse>>?) {
                        if (response!!.body() != null) {
                            chatAdapter?.addMessageList(response!!.body())
                            rv_chat?.scrollToPosition(chatAdapter!!.itemCount - 1)
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<MessageListResponse>>?, t: Throwable?) {
                        Toast.makeText(this@ChatActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                    }
                })
    }
}
