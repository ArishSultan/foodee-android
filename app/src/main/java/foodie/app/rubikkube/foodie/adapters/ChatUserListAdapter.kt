package foodie.app.rubikkube.foodie.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.ChatActivity
import foodie.app.rubikkube.foodie.models.ChatUserList
import java.util.ArrayList

/**
 * Created by YASIR on 11/12/2017.
 */
class ChatUserListAdapter(private var context: Context) : BaseAdapter() {


    val options =  RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)

    private var inflater: LayoutInflater? = null
    internal var chatuserList: MutableList<ChatUserList> = ArrayList()

       init {
        inflater = LayoutInflater.from(context)
    }
    override fun getView(position: Int, convertview: View?, p2: ViewGroup?): View {
       val item = chatuserList.get(position)


        var convertview = convertview
        val holder: ChatUserListAdapter.ViewHolder
        if (convertview == null) {

            convertview = inflater!!.inflate(R.layout.message_list_item, null)
            holder = ViewHolder()

            holder._cv = convertview.findViewById<View>(R.id.user_img) as CircleImageView
            holder._title = convertview.findViewById<View>(R.id.txt_username) as TextView
            holder._numofmsgs = convertview.findViewById<View>(R.id.num_of_msgs) as TextView
            holder._status = convertview.findViewById<View>(R.id.txt_status) as TextView
            holder._minsago = convertview.findViewById<View>(R.id.mins_ago) as TextView
            holder._rel = convertview.findViewById<View>(R.id.rel) as RelativeLayout


            convertview.tag = holder

        } else {
            holder = convertview.tag as ViewHolder
        }


        holder?._status?.text = item.status
        Glide.with(context).load(item.userDP).apply(options).into(holder?._cv!!)
        holder?._title?.text = item.name
        //holder?._minsago?.text = item.timeDifference

//        if(item.count == 0) {
//            holder?._numofmsgs?.visibility = View.INVISIBLE
//        }else {
//            holder?._numofmsgs?.visibility = View.VISIBLE
//            holder?._numofmsgs?.text = item.count.toString()
//
//        }


//        holder?._cv?.setOnClickListener {
//
//            val intent = Intent(context, ChatActivity::class.java)
////            intent.putExtra("otherUserProfileImage", item.profilePic)
////            intent.putExtra("otherUserId", item.seatcheckUserId)
////            intent.putExtra("otherUserName",item.name)
//            context.startActivity(intent)
//
//        }


        holder._rel?.setOnClickListener {


            val intent = Intent(context,ChatActivity::class.java)
//            intent.putExtra("sender_id", Prefs.getString("userId",""))
//            intent.putExtra("receiver_id",item.seatcheckUserId)
//            intent.putExtra("chat_receiver_id",item.id)
            intent.putExtra("user_dp",item.userDP)
//            intent.putExtra("fcmToken",item.deviceId)
            intent.putExtra("name",item.name)
            context.startActivity(intent)
        }

        return convertview!!

    }

    override fun getItem(p0: Int): Any {
        return chatuserList.size
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return chatuserList.size

    }


    inner class ViewHolder {

        internal var _cv: CircleImageView? = null
        internal var _title: TextView? = null
        internal var _numofmsgs: TextView? = null
        internal var _status: TextView? = null
        internal var _minsago: TextView? = null
        internal var _rel: RelativeLayout? = null


    }

    fun setChatUsers(users: MutableList<ChatUserList>) {
        this.chatuserList = users
        notifyDataSetChanged()
    }

    fun getChatUserListItem(position: Int) : ChatUserList {

        return chatuserList[position]
    }


}