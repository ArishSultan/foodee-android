package foodie.app.rubikkube.foodie.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ChatUserListAdapter
import foodie.app.rubikkube.foodie.model.ChatUserList
import kotlinx.android.synthetic.main.fragment_chat.view.*


/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {


    var userList : MutableList<ChatUserList>? = null
    val images = arrayOf(R.drawable.one,R.drawable.three,R.drawable.two,R.drawable.four,R.drawable.five,R.drawable.one,R.drawable.three,R.drawable.two,R.drawable.four,R.drawable.five)
    val names = arrayOf("Yasir Ameen","Jean Janet","John Shah","Alice Mary","Alina Sam","Yasir Ameen","Jean Janet","John Shah","Alice Mary","Alina Sam")
    val status = arrayOf("I am Batman","The world is glorious place to live","Food is in my blood","I like flowers and sunshine","Stones of Empires","I am Batman","The world is glorious place to live","Food is in my blood","I like flowers and sunshine","Stones of Empires")

    private var chatuserAdapter: ChatUserListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)


        userList = arrayListOf()
        for(i in images.indices) {

             val chatList = ChatUserList()
             chatList.name = names[i]
             chatList.userDP = images[i]
             chatList.status = status[i]

            (userList as ArrayList<ChatUserList>).add(chatList)
        }


        chatuserAdapter = ChatUserListAdapter(context!!)
        view.rv_chat_list.adapter = chatuserAdapter

        chatuserAdapter?.setChatUsers(userList!!)


        Log.d("daz",userList!![0].name)
        Log.d("daz",userList!![0].name)

        return view
    }

}// Required empty public constructor
