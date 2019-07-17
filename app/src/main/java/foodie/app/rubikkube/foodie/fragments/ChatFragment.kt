package foodie.app.rubikkube.foodie.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.AppClass

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ChatInboxListAdapter
import foodie.app.rubikkube.foodie.adapter.ChatUserListAdapter
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.classes.ObservableObject
import foodie.app.rubikkube.foodie.model.ChatUserList
import foodie.app.rubikkube.foodie.model.Food
import foodie.app.rubikkube.foodie.model.InboxListResponse
import foodie.app.rubikkube.foodie.model.MessageListResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment(), Observer {

    override fun update(o: Observable?, arg: Any?) {


        if(arg is MessageListResponse){

            messageListResponse = arg
            chatInboxListAdapter!!.iterateForNewMessageIndication(messageListResponse!!)


        }

    }


    var chatInboxListAdapter:ChatInboxListAdapter?= null
    var inboxUserListResponse:List<InboxListResponse>?= ArrayList()
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
        fromUserId = Prefs.getString(Constant.USERID,"")


    }

    private fun setUpRecyclerView(view: View) {

        chatInboxListAdapter = ChatInboxListAdapter(context!!,inboxUserListResponse!!)
        view.rv_chat_list.setHasFixedSize(false)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayout.VERTICAL

        view.rv_chat_list.layoutManager = layoutManager
        view.rv_chat_list.adapter = chatInboxListAdapter

    }

    private fun getInboxList(view: View){
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
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
                                chatInboxListAdapter!!.update(inboxUserListResponse!!)
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
        getInboxList(this.view!!)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}// Required empty public constructor
