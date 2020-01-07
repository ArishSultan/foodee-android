package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.AppClass
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ChatListAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.MessageListResponse
import foodie.app.rubikkube.foodie.model.MessageReceiver
import foodie.app.rubikkube.foodie.model.Profile
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.custom_toolbar_for_chatscreen.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    private var myProfilePicture: String? = ""
    private var toUserFcmToken: String? = ""
    val options =  RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)

    internal var chatAdapter: ChatListAdapter? =null
    private var manager: androidx.recyclerview.widget.LinearLayoutManager? = null
    internal var messageListResponse:MessageListResponse?= null
    private var fromUserId:String?= null
    internal var toUserId:String?= null
    internal var message:String?= null
    internal var thread_id:String?= null
    private var userID:String?= null
    private var userName:String?= null
    internal var avatar:String?= null
    internal var messageReceiver:MessageReceiver?= null
    internal var profile: Profile? = null
    var sdfDate:SimpleDateFormat?= null
    private var now:Date?= null
    var strDate:String?= null
    private var mSocket: Socket?= null
    private var onMessageReceived: Emitter.Listener? = null
    private var gson:Gson?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val app = application as AppClass
        mSocket = app.socket

       // mSocket?.on("user-global-$fromUserId:new_message",onMessageReceived)

        mSocket!!.connect()

        socketListener()
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)


        fromUserId = Prefs.getString(Constant.USERID, "")
        toUserId = Prefs.getString("toUserId", "")
        thread_id = Prefs.getString("threadId","")
        userName = Prefs.getString("userName","")
        userID = Prefs.getString("avatarUser","")
        avatar = Prefs.getString("avatar","")
        //toUserFcmToken = Prefs.getString("toUserFcmToken","")
        toUserFcmToken = Utils.getfcmToken()
        sdfDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")//dd/MM/yyyy
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

        chat_user_img.setOnClickListener {
            val intent = Intent(this, OtherUserProfileDetailActivity::class.java)
            intent.putExtra("id", userID)
            startActivity(intent)
        }
    }


    private fun intializeAdapter() {
        chatAdapter = ChatListAdapter(this@ChatActivity)
        rv_chat?.adapter = chatAdapter
        manager = androidx.recyclerview.widget.LinearLayoutManager(this@ChatActivity, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        rv_chat?.layoutManager = manager
    }


    fun sendMessage() {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()

        val jsonObject = JSONObject()
        jsonObject.put("from_id", fromUserId)
        jsonObject.put("to_id", toUserId)
        jsonObject.put("message",message)

        val mService = ApiUtils.getSOService() as SOService
        mService.sendMessage(hm, Utils.getRequestBody(jsonObject.toString()))
            .enqueue(object : Callback<MessageListResponse> {
                override fun onResponse(call: Call<MessageListResponse>?, response: Response<MessageListResponse>?) {
                    messageListResponse = MessageListResponse()
                    now = Date()
                    strDate = sdfDate!!.format(now)
                   // messageListResponse!!.messageId = thread_id!!.toInt()
                    messageListResponse!!.message = message
                    messageListResponse!!.recipientId = toUserId!!.toInt()
                    messageListResponse!!.updatedAt = strDate
                    messageListResponse!!.createdAt = strDate
                    messageReceiver = MessageReceiver()
                    messageReceiver!!.id = toUserId!!.toInt()
                    messageListResponse!!.receiver = messageReceiver
                    chatAdapter?.addSingleMessage(messageListResponse!!)
                    rv_chat?.scrollToPosition(chatAdapter!!.itemCount - 1)

                    /* toUserId = Prefs.getString("toUserId", "")
        thread_id = Prefs.getString("threadId","")
        userName = Prefs.getString("userName","")
        userID = Prefs.getString("avatarUser","")
        avatar = Prefs.getString("avatar","")*/


                    if(toUserFcmToken != null) {

                        Utils.sentMessageNotification(this@ChatActivity,"Foodee",
                                message!!,
                                toUserId!!,
                                Prefs.getString(Constant.USERID, "")!!,
                                ApiUtils.BASE_URL + "/storage/media/avatar/" + userID+ "/" + avatar!!,
                                toUserFcmToken!!,
                                "nothing",
                                Prefs.getString(Constant.NAME,"Unknown"),userName!!)
                     }
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

                            if (response.body()[0].messageSender.id.toString() == Prefs.getString(Constant.USERID, "")) {
                                myProfilePicture = ApiUtils.BASE_URL + "/storage/media/avatar/" + Prefs.getString(Constant.USERID, "") + "/" + response.body()[0].messageSender.profile.avatar
                            }
                            else{
                                myProfilePicture = ApiUtils.BASE_URL + "/storage/media/avatar/" + Prefs.getString(Constant.USERID, "") + "/" + response.body()[0].messageReceiver.profile.avatar
                            }


                            if (response.body()[0].messageSender.id.toString() != Prefs.getString(Constant.USERID, "")) {
                                toUserFcmToken = response.body()[0].messageSender.device_token
                            }
                            else{
                                toUserFcmToken = response.body()[0].messageReceiver.device_token
                            }
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<MessageListResponse>>?, t: Throwable?) {
                        Toast.makeText(this@ChatActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    fun socketListener(){



                onMessageReceived = Emitter.Listener { args ->

                    runOnUiThread {

                        try {

                            val jsonObject = JSONObject(args[0].toString())
                            Log.d("SocketResponse",""+jsonObject)
                            messageListResponse = MessageListResponse()
                            gson = Gson()
                            messageListResponse = gson!!.fromJson(jsonObject.toString(),MessageListResponse::class.java)

                            /*messageListResponse!!.id = jsonObject.getInt("id")
                            messageListResponse!!.messageId = jsonObject.getInt("message_id")
                            messageListResponse!!.message = jsonObject.getString("message")
                            messageListResponse!!.type = jsonObject.getString("type")
                            messageListResponse!!.recipientId = jsonObject.getInt("recipient_id")
                            messageListResponse!!.sender_id = jsonObject.getInt("sender_id")
                            messageListResponse!!.updatedAt = jsonObject.getString("updated_at")
                            messageListResponse!!.createdAt = jsonObject.getString("created_at")
                            val receiverJsonObject = jsonObject.getJSONObject("receiver")
                            messageReceiver = MessageReceiver()
                            messageReceiver!!.id = receiverJsonObject.getInt("id")
                            messageReceiver!!.username = receiverJsonObject.getString("username")
                            val receiverProfileJsonObject = receiverJsonObject.getJSONObject("profile")
                            profile = Profile()
                            profile!!.userId = receiverProfileJsonObject.getInt("user_id")
                            profile!!.avatar = receiverProfileJsonObject.getString("avatar")
                            messageReceiver!!.profile = profile
                            messageListResponse!!.receiver = messageReceiver

                            val senderJsonObject = jsonObject.getJSONObject("sender")
                            messageSender = MessageReceiver()
                            messageSender!!.id = senderJsonObject.getInt("id")
                            messageSender!!.username = senderJsonObject.getString("username")
                            val senderProfileJsonObject = senderJsonObject.getJSONObject("profile")
                            profile = Profile()
                            profile!!.userId = senderProfileJsonObject.getInt("user_id")
                            profile!!.avatar = senderProfileJsonObject.getString("avatar")
                            messageSender!!.profile = profile
                            messageListResponse!!.messageSender = messageSender*/
                            chatAdapter?.addSingleMessage(messageListResponse!!)
                            rv_chat?.scrollToPosition(chatAdapter!!.itemCount - 1)

                        }catch (ex : Exception) {
                            ex.printStackTrace()
                        }
                }

            }
        }




    override fun onResume() {
        super.onResume()

        if(mSocket != null) {

            mSocket?.on("user-global-$fromUserId:new_message",onMessageReceived)
        }
    }

    override fun onPause() {
        super.onPause()

        if(mSocket != null) {

            mSocket?.off("user-global-$fromUserId:new_message",onMessageReceived)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(mSocket != null) {

            mSocket?.off("user-global-$fromUserId:send_msg",onMessageReceived)
        }

    }





}
