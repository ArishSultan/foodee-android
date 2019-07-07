package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.NotificationCenterAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.NotificationCenter
import foodie.app.rubikkube.foodie.utilities.Constant
import kotlinx.android.synthetic.main.activity_notification_center.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class NotificationCenterActivity : AppCompatActivity() {

    private var notificationCenterAdapter: NotificationCenterAdapter? = null
    private var manager: LinearLayoutManager? = null
    var notificationCenterList:ArrayList<NotificationCenter> = ArrayList()
    var img_bell:ImageView? = null
    var title_toolbar:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_center)


        img_bell = toolbar_id!!.findViewById(R.id.img_notification_bell)
        title_toolbar = toolbar_id!!.findViewById(R.id.toolbar_title)
        img_bell!!.visibility = View.GONE
        title_toolbar!!.text = "Notifications"
        getNotificationList()
        intializeAdapter()
    }

    private fun intializeAdapter() {
        notificationCenterAdapter = NotificationCenterAdapter(this@NotificationCenterActivity,notificationCenterList)
        rv_notification?.adapter = notificationCenterAdapter
        manager = LinearLayoutManager(this@NotificationCenterActivity, LinearLayoutManager.VERTICAL, false)
        rv_notification?.layoutManager = manager
    }

    private fun getNotificationList(){
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.getNotificationList(hm)
            .enqueue(object : Callback<ArrayList<NotificationCenter>> {
                override fun onFailure(call: Call<ArrayList<NotificationCenter>>?, t: Throwable?) {
                    Toast.makeText(this@NotificationCenterActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<ArrayList<NotificationCenter>>?, response: Response<ArrayList<NotificationCenter>>?) {
                    if(response!!.isSuccessful){
                        if(response.body().size!=0){
                            notificationCenterList = response.body()
                            notificationCenterAdapter!!.update(notificationCenterList!!)
                            Log.d("NotificationResponse",response.body().toString())
                        }
                        else
                        {
                            Toasty.error(this@NotificationCenterActivity,"There is no Notification", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
    }
}
