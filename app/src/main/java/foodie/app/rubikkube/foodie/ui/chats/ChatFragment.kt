package foodie.app.rubikkube.foodie.ui.chats


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import foodie.app.rubikkube.foodie.apiUtils.SOService
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapters.ChatInboxListAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.classes.ObservableObject
import foodie.app.rubikkube.foodie.models.InboxListResponse
import foodie.app.rubikkube.foodie.models.MessageListResponse
import foodie.app.rubikkube.foodie.utilities.Constants
import kotlinx.android.synthetic.main.fragment_chat.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : androidx.fragment.app.Fragment(), Observer {

    override fun update(o: Observable?, arg: Any?) {
        if (arg is MessageListResponse) {
            messageListResponse = arg
            chatInboxListAdapter!!.iterateForNewMessageIndication(messageListResponse!!)
        }
    }


    var chatInboxListAdapter:ChatInboxListAdapter?= null
    var inboxUserListResponse:MutableList<InboxListResponse>?= ArrayList()
    var filterInboxUserListResponse:MutableList<InboxListResponse>?= ArrayList()
    var inboxListResponse:InboxListResponse?= null
    private var messageListResponse:MessageListResponse?= null
    private var fromUserId:String?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        ObservableObject.getInstance().addObserver(this)
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        setUpRecyclerView(view)


        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fromUserId = Prefs.getString(Constants.USER_ID,"")
    }

    private fun setUpRecyclerView(view: View) {

        chatInboxListAdapter = ChatInboxListAdapter(context!!,inboxUserListResponse!!)
        view.rv_chat_list.setHasFixedSize(false)

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayout.VERTICAL

        view.rv_chat_list.layoutManager = layoutManager
        view.rv_chat_list.adapter = chatInboxListAdapter

    }

    private fun getInboxList(view: View){
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.getInboxList(hm)
                .enqueue(object : Callback<ArrayList<InboxListResponse>> {
                    override fun onFailure(call: Call<ArrayList<InboxListResponse>>?, t: Throwable?) {
                        Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<ArrayList<InboxListResponse>>?, response: Response<ArrayList<InboxListResponse>>?) {
                        if(response!!.isSuccessful){

                            if(response.body().size!=0){
                                inboxUserListResponse = response.body()
                                filterInboxUserListResponse = arrayListOf()

                                for(i in inboxUserListResponse?.indices!!) {

                                   val newMessageTag = Prefs.getBoolean("new-msg-${inboxUserListResponse!![i].userId}",false)


                                    if(newMessageTag) {

                                        val user = inboxUserListResponse!![i]
                                        user.newMessage = true
                                        filterInboxUserListResponse?.add(user)

                                    }else {

                                        val user = inboxUserListResponse!![i]
                                        user.newMessage = false
                                        filterInboxUserListResponse?.add(user)

                                    }
                                }

                                chatInboxListAdapter!!.update(filterInboxUserListResponse!!)
                            }
                            else
                            {
                                Toasty.error(context!!,"There is no chat threads",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                })
    }



    override fun onResume() {
        super.onResume()

        if(inboxUserListResponse != null) {
            inboxUserListResponse?.clear()
        }
        getInboxList(this.view!!)
    }
}
