package foodie.app.rubikkube.foodie.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.MultimediaAdapter
import foodie.app.rubikkube.foodie.adapter.SearchUserAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.AddNewPostResponse
import foodie.app.rubikkube.foodie.model.FeedData
import foodie.app.rubikkube.foodie.model.MeResponse
import foodie.app.rubikkube.foodie.model.User
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_post.back_icon
import kotlinx.android.synthetic.main.activity_post.edt_search_user
import kotlinx.android.synthetic.main.activity_post.img_search
import kotlinx.android.synthetic.main.activity_post.tag_user_heading
import kotlinx.android.synthetic.main.activity_post.txt_caption
import kotlinx.android.synthetic.main.activity_post.txt_tagged_user
import kotlinx.android.synthetic.main.activity_post.user_avatar
import kotlinx.android.synthetic.main.activity_post.user_name
import net.alhazmy13.mediapicker.Image.ImagePicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.ArrayList

class EditPostActivity : AppCompatActivity() {
    private var imageNameList: ArrayList<String>? = null
    private var imageUrlList: ArrayList<String>? = null
    private var imagePath:ArrayList<String>? = null
    private var multimediaGridAdapter: MultimediaAdapter? = null
    private var rv_grid: androidx.recyclerview.widget.RecyclerView? = null
    private var rv_tag_user: androidx.recyclerview.widget.RecyclerView?= null
    private var edit_post_btn:TextView? = null
    private var pd: KProgressHUD? = null
    private var file: File? = null
    private var postImagesParts: List<MultipartBody.Part>? = null
    private var postBody:RequestBody? = null
    private var searchUserList:ArrayList<User> = ArrayList()
    private var userModel: User? = null
    var user: String = ""
    private var userid: ArrayList<Int>? = null
    private var editPost:FeedData? = null
    private lateinit var searchUserAdapter: SearchUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        imageNameList = ArrayList()
        imageUrlList = ArrayList()
        userid = ArrayList()
        pd = Utils.progressDialog(this, "", "Please wait")
        if (Hawk.contains("profileResponse")) {
            val meResponse = Hawk.get("profileResponse", "") as MeResponse
            dataBindMe(meResponse)
        }
        if(Hawk.contains("EditPostObject")){
            editPost = Hawk.get("EditPostObject", "") as FeedData
            Prefs.putString("EditPostId",editPost!!.id.toString())
            dataBindPost(editPost!!)
            if(editPost!!.tags.size!=0) {
                userid!!.add(editPost!!.tags[0].pivot.userId)
            }
        }

        back_icon.setOnClickListener {
            finish()
        }

        rv_grid = findViewById(R.id.rv_grid)
        rv_tag_user = findViewById(R.id.rv_tag_user)
        setMultimediaGridAdapter()
        setupRecyclerView()

        edit_post_btn = findViewById(R.id.edit_post_btn)
        edit_post_btn!!.setOnClickListener {
            if(txt_caption.text.toString() == ""){
                Toasty.error(this,"Enter post first.").show()
            }
            else
            {
                updatePost(editPost!!.id,txt_caption.text.toString(),userid,this)
            }
        }

        img_search!!.setOnClickListener {
            user = edt_search_user.text.toString()
            if(user != ""){
                getSearchUserList(this, user)
            }
        }
    }

    private fun setupRecyclerView(){
        searchUserList = ArrayList()
        searchUserAdapter = SearchUserAdapter(this, searchUserList)
        rv_tag_user!!.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        rv_tag_user!!.adapter = searchUserAdapter
        rv_tag_user!!.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rv_tag_user!!.addOnItemTouchListener(RecyclerTouchListener(this@EditPostActivity, rv_tag_user!!, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                userModel = User()
                userid  =   ArrayList()
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
        imageNameList!!.add("start")
        imageUrlList!!.add("start")
        multimediaGridAdapter = MultimediaAdapter(this, imageNameList,imageUrlList,"updatePost")
        rv_grid!!.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        rv_grid!!.adapter = multimediaGridAdapter
        rv_grid!!.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        rv_grid!!.addOnItemTouchListener(RecyclerTouchListener(this@EditPostActivity, rv_grid!!, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                val lastPos = imageNameList?.size?.minus(1)
                //String aray[] = multimedia.get(position).split("@");
                if (position == lastPos) {

                    ImagePicker.Builder(this@EditPostActivity)
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
            //imageNameList!!.add(0,mPaths.get(0))
            //imageNameList!!.addAll(mPaths)
            imageNameList!!.addAll(0,mPaths)
            imageUrlList!!.addAll(0,mPaths)
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

    fun dataBindPost(editPost: FeedData) {
        txt_caption.setText(editPost.content)
        if(editPost.tags.size!=0) {
            tag_user_heading.visibility = View.VISIBLE
            txt_tagged_user.visibility = View.VISIBLE
            txt_tagged_user.text = editPost.tags[0].username
        }
        else {
            tag_user_heading.visibility = View.GONE
            txt_tagged_user.visibility = View.GONE
        }
        if(editPost.photos.size!=0){
            imagePath = ArrayList()
            if(!editPost.photos.contains("")) {
                for (i in editPost!!.photos.indices) {
                    imagePath!!.add(i,editPost!!.photos[i])
                    imageUrlList!!.add(i,ApiUtils.BASE_URL + "/storage/media/post/" + editPost!!.photos[i])
                    Log.d("Image$i",editPost!!.photos[i])
                }
            }
            imageNameList!!.addAll(0,imagePath!!)
            multimediaGridAdapter = MultimediaAdapter(this, imageNameList,imageUrlList,"updatePost")
        }
    }

    private fun updatePost(postId:Int,content:String,tag_id:ArrayList<Int>?,context:Context) {
        pd = Utils.progressDialog(this@EditPostActivity, "", "Post Uploading...")
        pd!!.show()

        val mService = ApiUtils.getSOService() as SOService
        postImagesParts = ArrayList()
        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"
        val jsonObject = JSONObject()
        jsonObject.put("_method", "PATCH")

        for (i in imageUrlList?.indices!!) {

            Log.d( "ImagePath","post image " + i + "  " + imageUrlList!![i])
            if(imageUrlList!![i].contains("start") || imageUrlList!![i].contains("http")){

                Log.d( "ImagePath","post image " + i + "  " + imageUrlList!![i])
                break
            }
            file = File(imageUrlList!![i])
            postBody = RequestBody.create(MediaType.parse("image/*"), file!!)
            (postImagesParts as ArrayList<MultipartBody.Part>).add(MultipartBody.Part.createFormData("photos[$i]", file!!.name, postBody!!))

        }
        mService.updatePost(postId,hm, postImagesParts,
                RequestBody.create(MediaType.parse("text/plain"), content),
                tag_id,Prefs.getDouble("currentLat", 0.0),
                Prefs.getDouble("currentLng", 0.0),
                RequestBody.create(MediaType.parse("text/plain"),"PATCH")).enqueue(object : Callback<AddNewPostResponse> {

            override fun onFailure(call: Call<AddNewPostResponse>?, t: Throwable?) {
                pd!!.dismiss()
            }

            override fun onResponse(call: Call<AddNewPostResponse>?, response: Response<AddNewPostResponse>?) {
                pd!!.dismiss()
                Log.d("Edit",response!!.body().toString())


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



    class RecyclerTouchListener(context: Context, recyclerView: androidx.recyclerview.widget.RecyclerView, private val clickListener: ClickListener?) : androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {

                    return true

                }

                override fun onLongPress(e: MotionEvent) {

                    val child = recyclerView.findChildViewUnder(e.x, e.y)

                    if (child != null && clickListener != null) {

                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))

                    }
                }
            })
        }


        override fun onInterceptTouchEvent(rv: androidx.recyclerview.widget.RecyclerView, e: MotionEvent): Boolean {

            val child = rv.findChildViewUnder(e.x, e.y)

            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {

                clickListener.onClick(child, rv.getChildPosition(child))

            }
            return false
        }

        override fun onTouchEvent(rv: androidx.recyclerview.widget.RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }


        interface ClickListener {

            fun onClick(view: View, position: Int)

            fun onLongClick(view: View?, position: Int)
        }
    }
}

