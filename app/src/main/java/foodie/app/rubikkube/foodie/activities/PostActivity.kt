package foodie.app.rubikkube.foodie.activities

import android.app.Activity
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
import foodie.app.rubikkube.foodie.adapter.SearchUserAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.AddNewPostResponse
import foodie.app.rubikkube.foodie.model.MeResponse
import foodie.app.rubikkube.foodie.model.User
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
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
    private var imageUrlList: ArrayList<String>? = null
    private var multimediaGridAdapter: MultimediaAdapter? = null
    private var rv_grid: RecyclerView? = null
    private var rv_tag_user: RecyclerView?= null
    private var post_btn:TextView? = null
    private var pd: KProgressHUD? = null
    private var file: File? = null
    private var postImagesParts: List<MultipartBody.Part>? = null
    private var postBody:RequestBody? = null
    private var searchUserList:ArrayList<User> = ArrayList()
    private var userModel: User? = null
    var user: String = ""
    private var userid: ArrayList<Int>? = null
    private lateinit var searchUserAdapter: SearchUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        pd = Utils.progressDialog(this, "", "Please wait")
            if (Hawk.contains("profileResponse")) {
                val meResponse = Hawk.get("profileResponse", "") as MeResponse
                dataBindMe(meResponse)
            }

        back_icon.setOnClickListener {
            finish()
        }

        rv_grid = findViewById(R.id.rv_grid)
        rv_tag_user = findViewById(R.id.rv_tag_user)
        setMultimediaGridAdapter()
        setupRecyclerView()
        post_btn = findViewById(R.id.post_btn)
        post_btn!!.setOnClickListener {
            if(txt_caption.text.toString().equals("")){
                Toasty.error(this,"Enter post first.").show()
            }
            else
            {
                addPost(txt_caption.text.toString(),userid,this)
            }
        }

        img_search!!.setOnClickListener {
            user = edt_search_user.text.toString()
            if(user.equals("") == false){
                getSearchUserList(this, user)
            }
        }
    }

    private fun setupRecyclerView(){
        searchUserList = ArrayList()
        searchUserAdapter = SearchUserAdapter(this, searchUserList)
        rv_tag_user!!.itemAnimator = DefaultItemAnimator()
        rv_tag_user!!.adapter = searchUserAdapter
        rv_tag_user!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rv_tag_user!!.addOnItemTouchListener(RecyclerTouchListener(this@PostActivity, rv_tag_user!!, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                userModel = User()
                userid = ArrayList()
                userModel = searchUserAdapter.getUser(position)
                Log.d("User Model", userModel!!.username)
                userid!!.add(userModel!!.id)
                txt_tagged_user.visibility = View.VISIBLE
                tag_user_heading.visibility = View.VISIBLE
                txt_tagged_user.text = userModel!!.username
            }
            override fun onLongClick(view: View?, position: Int) {
            }
        }))
    }

    private fun setMultimediaGridAdapter() {
        imageList = ArrayList()
        imageUrlList = ArrayList()
        imageList!!.add("start")
        imageUrlList!!.add("start")
        multimediaGridAdapter = MultimediaAdapter(this, imageList,imageUrlList,"addPost")
        rv_grid!!.itemAnimator = DefaultItemAnimator()
        rv_grid!!.adapter = multimediaGridAdapter
        rv_grid!!.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
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
                            .allowMultipleImages(true)
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
            val mPaths:ArrayList<String> = data!!.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH)
            Log.d("LOG",""+mPaths)
            //imageList!!.add(0,mPaths.get(0))
            imageList!!.addAll(0,mPaths)
            imageUrlList!!.addAll(0,mPaths)
            //imageList!!.addAll(mPaths)
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
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.profile.userId + "/" + me.profile.avatar).into(user_avatar)
        }
        else
        {
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(user_avatar)

        }
    }

    private fun addPost(content:String,tag_id:ArrayList<Int>?,context: Context) {
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
                    content),tag_id,Prefs.getDouble("currentLat", 0.0),Prefs.getDouble("currentLng", 0.0)).enqueue(object : Callback<AddNewPostResponse> {

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

    private fun getSearchUserList(context: Context, username: String) {
        pd!!.show()

        val mService = ApiUtils.getSOService() as SOService

        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        mService.searchUser(hm, username)
                .enqueue(object : Callback<ArrayList<User>> {

                    override fun onFailure(call: Call<ArrayList<User>>?, t: Throwable?) {
                        pd!!.dismiss()
                    }

                    override fun onResponse(call: Call<ArrayList<User>>?, response: Response<ArrayList<User>>?) {
                        Log.d("Search Users", "" + response?.body())
                        pd!!.dismiss()
                        searchUserList = response!!.body()
                        searchUserAdapter.update(searchUserList)
                        Log.d("Search Users Model", "" + searchUserList)
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

