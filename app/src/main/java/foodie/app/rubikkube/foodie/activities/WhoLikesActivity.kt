package foodie.app.rubikkube.foodie.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import foodie.app.rubikkube.foodie.apiUtils.SOService
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapters.WhoLikesMyPostAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.models.Like
import foodie.app.rubikkube.foodie.ui.chats.NotificationViewModel
import foodie.app.rubikkube.foodie.utilities.Constants
import kotlinx.android.synthetic.main.activity_who_likes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class WhoLikesActivity : AppCompatActivity() {


    private var likeAdapter: WhoLikesMyPostAdapter? = null
    private var manager: androidx.recyclerview.widget.LinearLayoutManager? = null
    var likeList:List<Like> = ArrayList()
    var title_toolbar: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_who_likes)

        title_toolbar = toolbar_id!!.findViewById(R.id.toolbar_title)
        title_toolbar!!.text = "Who Like My Post"
        getWhoLikeMyPost(intent.getIntExtra("postId",0))

        if (intent.getBooleanExtra("fromNotification", false)) {
            val count2 = Prefs.getInt("notification", 0) - 1
            NotificationViewModel.notifications.postValue(if (count2 >= 0) count2 else 0)
            Prefs.putInt("notification", count2)
        }
    }

    private fun intializeAdapter(likeList : List<Like>) {
        likeAdapter = WhoLikesMyPostAdapter(this@WhoLikesActivity,likeList)
        mRv?.adapter = likeAdapter
        manager = androidx.recyclerview.widget.LinearLayoutManager(this@WhoLikesActivity, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        mRv?.layoutManager = manager
    }


    private fun getWhoLikeMyPost(postId : Int){
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.getWhoLikeMyPost(postId,hm)
                .enqueue(object : Callback<List<Like>> {
                    override fun onFailure(call: Call<List<Like>>?, t: Throwable?) {

                        Toast.makeText(this@WhoLikesActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<List<Like>>?, response: Response<List<Like>>?) {


                        if(response!!.isSuccessful){
                            if(response.body().size != 0){
                                likeList = response.body()
                                intializeAdapter(likeList)
                                Log.d("NotificationResponse",response.body().toString())
                            }
                            else
                            {
                                Toasty.error(this@WhoLikesActivity,"There is no Likes", Toast.LENGTH_LONG).show()
                            }
                        }

                    }

                })
    }
}
