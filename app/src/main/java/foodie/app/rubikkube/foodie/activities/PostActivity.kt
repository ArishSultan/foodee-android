package foodie.app.rubikkube.foodie.activities

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kaopiz.kprogresshud.KProgressHUD
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.R
import kotlinx.android.synthetic.main.activity_post.*
import foodie.app.rubikkube.foodie.adapter.MultimediaAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.AddNewPostResponse
import foodie.app.rubikkube.foodie.model.FeedResponse
import foodie.app.rubikkube.foodie.model.MeResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_other_user_profile_detail.*
import net.alhazmy13.mediapicker.Image.ImagePicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.ArrayList

class PostActivity : AppCompatActivity() {
    private var imageList: ArrayList<String>? = null
    private var multimediaGridAdapter: MultimediaAdapter? = null
    private var rv_grid: RecyclerView? = null
    private var post_btn:TextView? = null
    private var pd: KProgressHUD? = null
    private var file: File? = null
    private var postImagesParts: List<MultipartBody.Part>? = null
    private var postBody:RequestBody? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

            if (Hawk.contains("profileResponse")) {
                val meResponse = Hawk.get("profileResponse", "") as MeResponse
                Log.d("profile", meResponse.profile.avatar)
                dataBindMe(meResponse)
            }

        back_icon.setOnClickListener {
            finish()
        }

        rv_grid = findViewById(R.id.rv_grid)
        setMultimediaGridAdapter()
        post_btn = findViewById(R.id.post_btn)
        post_btn!!.setOnClickListener {
            addPost(txt_caption.text.toString(),this)
        }
    }

    private fun setMultimediaGridAdapter() {
        imageList = ArrayList()
        imageList!!.add("start")
        multimediaGridAdapter = MultimediaAdapter(this, imageList)
        rv_grid!!.setItemAnimator(DefaultItemAnimator())
        rv_grid!!.setAdapter(multimediaGridAdapter)
        rv_grid!!.setLayoutManager(LinearLayoutManager(this, LinearLayout.HORIZONTAL, false))
        rv_grid!!.addOnItemTouchListener(RecyclerTouchListener(this@PostActivity, rv_grid!!, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                val lastPos = imageList?.size?.minus(1)
                //String aray[] = multimedia.get(position).split("@");
                if (position == lastPos) {

                    ImagePicker.Builder(this@PostActivity)
                            .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .extension(ImagePicker.Extension.PNG)
                            .scale(600, 600)
                            .allowMultipleImages(false)
                            .enableDebuggingMode(true)
                            .build()
                }
            }
            override fun onLongClick(view: View?, position: Int) {
            }
        }))
        multimediaGridAdapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val mPaths = data!!.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH)
            Log.d("LOG",""+mPaths.get(0))
            imageList!!.add(0,mPaths.get(0))
            multimediaGridAdapter!!.notifyDataSetChanged()
            // Toast.makeText(DashboardActivity.this,"this is called here",Toast.LENGTH_LONG).show();
        }
    }

    fun dataBindMe(me: MeResponse) {

        user_name.text = me.username.toString()
        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)
        if(me.profile.avatar!=null) {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(user_avatar)
        }
        else
        {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(user_avatar)

        }
    }

    private fun addPost(content:String,context: Context) {
        pd = Utils.progressDialog(this@PostActivity, "", "Post Uploading...")
        pd!!.show()

        val mService = ApiUtils.getSOService() as SOService
        postImagesParts = ArrayList()
        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        for (i in imageList?.indices!!) {

            Log.d( "ImagePath","post image " + i + "  " + imageList!!.get(i))
            if(imageList!!.get(i).contains("start")){

                Log.d( "ImagePath","post image " + i + "  " + imageList!!.get(i))
                break
            }
            file = File(imageList!!.get(i))
            postBody = RequestBody.create(MediaType.parse("image/*"), file)
            (postImagesParts as ArrayList<MultipartBody.Part>).add(MultipartBody.Part.createFormData("photos["+i+"]", file!!.getName(), postBody))

        }
            mService.addNewPost(hm, postImagesParts,
                    RequestBody.create(MediaType.parse("text/plain"),
                    content),Prefs.getDouble("currentLat", 0.0),Prefs.getDouble("currentLng", 0.0)).enqueue(object : Callback<AddNewPostResponse> {

                override fun onFailure(call: Call<AddNewPostResponse>?, t: Throwable?) {
                    pd!!.dismiss()

                }

                override fun onResponse(call: Call<AddNewPostResponse>?, response: Response<AddNewPostResponse>?) {
                    pd!!.dismiss()
                    Log.d("Specific food", "" + response?.body())
                    Log.d("User_ID",""+response!!.body().id)
                    Log.d("content",response!!.body().content)
                    Log.d("CreatedAt",response!!.body().createdAt)
                    Log.d("UpdatedAt",response!!.body().updatedAt)
                    Log.d("post_id",""+response.body().id)
                    //Log.d("photo 1",response!!.body().photos.get(0))
                    //Log.d("photo 2",""+response.body().photos.get(1))

                    Toasty.success(context,"Post Updated Successfully").show()
                    finish()
                }
            })

    }


    class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener?) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {

                    return true

                }

                override fun onLongPress(e: MotionEvent) {

                    val child = recyclerView.findChildViewUnder(e.getX(), e.getY())

                    if (child != null && clickListener != null) {

                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))

                    }
                }
            })
        }


        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            val child = rv.findChildViewUnder(e.getX(), e.getY())

            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {

                clickListener.onClick(child, rv.getChildPosition(child))

            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }


        interface ClickListener {

            fun onClick(view: View, position: Int)

            fun onLongClick(view: View?, position: Int)
        }
    }


}

