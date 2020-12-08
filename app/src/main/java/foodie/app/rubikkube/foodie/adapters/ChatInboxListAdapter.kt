package foodie.app.rubikkube.foodie.adapters

import android.content.Context
import android.content.Intent
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
import foodie.app.rubikkube.foodie.models.InboxListResponse
import foodie.app.rubikkube.foodie.models.MessageListResponse
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import foodie.app.rubikkube.foodie.apiUtils.SOService
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.JavaUtils
import foodie.app.rubikkube.foodie.MainActivity
import foodie.app.rubikkube.foodie.models.SimpleResponse
import foodie.app.rubikkube.foodie.ui.chats.NotificationViewModel
import foodie.app.rubikkube.foodie.utilities.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


class ChatInboxListAdapter(context: Context, list : MutableList<InboxListResponse>?)  : androidx.recyclerview.widget.RecyclerView.Adapter<ChatInboxListAdapter.ChatInboxHolder>() {
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
        holder.mins_ago.text = inboxUserList!![position].created_at

        if (inboxUserList!![position].newMessage) {
            holder.numOfMsgs.visibility = View.VISIBLE
        } else {
            holder.numOfMsgs.visibility = View.INVISIBLE
        }

        holder.view.setOnClickListener {
            if (inboxUserList!![position].newMessage) {
                Prefs.putInt("chatCount", Prefs.getInt("chatCount", 0) -1)

                if (MainActivity.navView != null) {
                    JavaUtils.removeBadge(MainActivity.navView, R.id.navigation_chat)
                }

                NotificationViewModel.chats.postValue(NotificationViewModel.chats.value?.minus(1))
            }

            Prefs.putString("avatarUser", inboxUserList!![position].userId.toString())
            Prefs.putString("toUserId", inboxUserList!![position].userId.toString())
            Prefs.putString("threadId", inboxUserList!![position].id.toString())
            Prefs.putString("userName", inboxUserList!![position].username)
            Prefs.putString("avatar", inboxUserList!![position].avatar)

            Prefs.putBoolean("new-msg-${inboxUserList!![position].userId}",false)

            val intent = Intent(mContext, ChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mContext.startActivity(intent)
        }

        holder.rel.setOnLongClickListener {
            val builder = AlertDialog.Builder(mContext)

            builder.setTitle("Delete Thread")
            builder.setMessage("Deleting thread will also delete the messages from receiver inbox.")
            builder.setPositiveButton("DELETE") { dialog, which ->
                DeleteChat(inboxUserList!![position].id,inboxUserList!![position],position)
            }
            builder.setNegativeButton("CANCEL") { dialog, which ->
                Toast.makeText(mContext,"You are not agree.",Toast.LENGTH_SHORT).show()
            }


            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()


            return@setOnLongClickListener  false
        }

    }


    inner class ChatInboxHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val userImg: ImageView = view.findViewById(R.id.user_img)
        val txtUsername: TextView = view.findViewById(R.id.txt_username)
        val txtStatus: TextView = view.findViewById(R.id.txt_status)
        val numOfMsgs: TextView = view.findViewById(R.id.num_of_msgs)
        val mins_ago: TextView = view.findViewById(R.id.mins_ago)
        val new_message: TextView = view.findViewById(R.id.new_message)
        val rel: RelativeLayout = view.findViewById(R.id.rel)
    }

    fun update(updateInboxList: MutableList<InboxListResponse>) {
        inboxUserList = updateInboxList
        notifyDataSetChanged()
    }

    fun iterateForNewMessageIndication(messageListResponse: MessageListResponse) {

//        for (i in inboxUserList!!.indices) {
//
//            if(inboxUserList!![i].userId == messageListResponse.messageSender.id) {
//
//                inboxUserList!![i].message_count++
//                inboxUserList!![i].newMessage = true
//                inboxUserList!![i].message = messageListResponse.message
//                inboxUserList!![i].created_at = messageListResponse.createdAt
//                notifyDataSetChanged()
//                break
//            }
//        }
    }


    private fun DeleteChat(threadId : Int, inbox : InboxListResponse, position: Int) {
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.deleteThread(threadId,hm)
                .enqueue(object : Callback<SimpleResponse> {
                    override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                        Toasty.error(mContext!!,"Something went wrong", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {

                        if(response?.isSuccessful!!) {

                            inboxUserList?.remove(inbox)
                            notifyDataSetChanged()

                        }else{
                            Toasty.error(mContext!!,response.message(), Toast.LENGTH_LONG).show()

                        }
                    }

                })


    }

}