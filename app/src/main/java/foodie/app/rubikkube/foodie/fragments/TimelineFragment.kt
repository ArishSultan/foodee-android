package foodie.app.rubikkube.foodie.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs


import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.PostActivity
import foodie.app.rubikkube.foodie.adapter.MultimediaAdapter
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.*
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.HashMap

class TimelineFragment : Fragment() {

    private var imageList: ArrayList<String>? = null
    private var multimediaGridAdapter: MultimediaAdapter? = null
    private var pd: KProgressHUD? = null
    private var rv_grid: RecyclerView? = null
    private var feedData:List<FeedData>?= ArrayList()

    private lateinit var timeLineAdapter: TimelineAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View =  inflater.inflate(R.layout.fragment_timeline, container, false)
//        view.toolbar_title.setText("TimeLine")
//        view.text.setText("TimeLine")

        Hawk.init(context!!).build();

        setUpRecyclerView(view)


        Log.d("Latitude",""+Prefs.getDouble("currentLat", 0.0))
        Log.d("Longitude",""+Prefs.getDouble("currentLng", 0.0))
        view.post.setOnClickListener {
            view.context.startActivity(Intent(activity,PostActivity::class.java))
        }

        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        getMe(this!!.view!!)
        getTimelinePost()
    }

    private fun setUpRecyclerView(view: View) {

        timeLineAdapter = TimelineAdapter(context!!,feedData)
        view.timeline_recyclerview.setHasFixedSize(false)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayout.VERTICAL

        view.timeline_recyclerview.layoutManager = layoutManager
        view.timeline_recyclerview.adapter = timeLineAdapter

    }

    private fun getTimelinePost(){
        pd = Utils.progressDialog(context!!, "", "Getting Timeline post").show()
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        mService.getTimelinePost(hm)
            .enqueue(object : Callback<FeedResponse> {
                override fun onFailure(call: Call<FeedResponse>?, t: Throwable?) {
                    pd?.dismiss()
                    Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<FeedResponse>?, response: Response<FeedResponse>?) {
                    pd?.dismiss()
                    if(response!!.isSuccessful){
                        Log.d("Response", Gson().toJson(response))
                        feedData = response.body().data
                        //intent.putExtra("foodList", response.body())
                        //foodList = response!!.body()
                        timeLineAdapter.update(feedData)
                    }
                }
            })
    }

    private fun getMe(view: View) {
        val mService = ApiUtils.getSOService() as SOService
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        mService.me(hm).enqueue(object : Callback<MeResponse> {
            override fun onFailure(call: Call<MeResponse>?, t: Throwable?) {
                Toast.makeText(activity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<MeResponse>?, response: Response<MeResponse>?) {
                if (response!!.isSuccessful) {
                    Hawk.put("profileResponse",response.body())
                    setValue(view, response.body())
                } else {
                    Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setValue(view: View, me: MeResponse) {
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        if(me.profile.avatar!=null) {
            Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(view.profile)
            Log.d("PRofileLink",ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar)
        }
        else
        {
            Glide.with(view).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(view.profile)

        }
    }
}
//    private fun setMultimediaGridAdapter() {
//        imageList = ArrayList<String>()
//        imageList?.add("start")
//        multimediaGridAdapter = MultimediaAdapter(getContext(), imageList)
//        rv_grid?.setItemAnimator(DefaultItemAnimator())
//        rv_grid?.setAdapter(multimediaGridAdapter)
//        rv_grid?.setLayoutManager(LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false))
//        rv_grid?.addOnItemTouchListener(RecyclerTouchListener(getActivity() , rv_grid!!, object : RecyclerTouchListener.ClickListener() {
//            fun onClick(view: View, position: Int) {
//
//                val lastPos = imageList.size - 1
//                //String aray[] = multimedia.get(position).split("@");
//                if (position == lastPos) {
//
//                    ImagePicker.Builder(getActivity())
//                            .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
//                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
//                            .directory(ImagePicker.Directory.DEFAULT)
//                            .extension(ImagePicker.Extension.PNG)
//                            .scale(600, 600)
//                            .allowMultipleImages(false)
//                            .enableDebuggingMode(true)
//                            .build()
//                }
//
//            }
//
//            fun onLongClick(view: View, position: Int) {
//
//            }
//        }))
//
//    }
