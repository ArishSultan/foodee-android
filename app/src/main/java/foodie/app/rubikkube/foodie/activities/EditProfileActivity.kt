package foodie.app.rubikkube.foodie.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ProfileFoodAdapter
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.*
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import foodie.app.rubikkube.foodie.utilities.Utils.Companion.progressDialog
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.ArrayList


class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    var foodList: ArrayList<Food> = ArrayList()
    private lateinit var profileAdapter: ProfileFoodAdapter

    lateinit var meResponse: MeResponse
    private var image: ArrayList<Image>? = null
    private var imageFile: File? = null

    private var imageType: String = ""
    private var photoPath: String = ""
    private var isFoodPicAdded: Boolean = false
    private var message: String = ""
    private var age: String = ""
    private var location: String = ""
    private var contribution: String = ""

    var KProgressHUD: KProgressHUD? = null
    internal var cv: MultipartBody.Part? = null
    private var dialog: android.support.v7.app.AlertDialog? = null

    private var food_pic : ImageView? = null

//    private fun getListOfFood(){
//        val hm = java.util.HashMap<String, String>()
//        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
//        val mService = ApiUtils.getSOService() as SOService
//        mService.getMyFoodList(hm)
//                .enqueue(object : Callback<ArrayList<Food>>{
//                    override fun onFailure(call: Call<ArrayList<Food>>?, t: Throwable?) {
//                        Toast.makeText(this@EditProfileActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onResponse(call: Call<ArrayList<Food>>?, response: Response<ArrayList<Food>>?) {
//                        if(response!!.isSuccessful){
//                            foodList = response!!.body()
//                            profileAdapter.update(foodList)
//                        }
//                    }
//                })
//
//    }

    override fun onClick(v: View?) {


        if (v?.id == R.id.back_icon) {
            finish()
        }
        if (v?.id == R.id.age_dropdown) {
            ageBuilder(this)
        }
        if (v?.id == R.id.location_dropdown) {

        }


        if (v?.id == R.id.female_card_bg) {
            meResponse.profile.gender = "Female"

            female_card_bg.setBackgroundResource(R.drawable.rounded_button)
            male_card_bg.setBackgroundResource(R.drawable.rectangular_line)
            female_txt.setTextColor(resources.getColor(R.color.white))
            male_txt.setTextColor(resources.getColor(R.color.d_gray))
        }
        if (v?.id == R.id.male_card_bg) {
            meResponse.profile.gender = "Male"
            female_card_bg.setBackgroundResource(R.drawable.rectangular_line)
            male_card_bg.setBackgroundResource(R.drawable.rounded_button)
            female_txt.setTextColor(resources.getColor(R.color.d_gray))
            male_txt.setTextColor(resources.getColor(R.color.white))
        }
        if (v?.id == R.id.twenty_precent) {


            contribution = "20%"
            twenty_precent.setBackgroundResource(R.drawable.rounded_button)
            thirty_percent.setBackgroundResource(R.drawable.rectangular_line)
            fifty_percent.setBackgroundResource(R.drawable.rectangular_line)
            treat_me.setBackgroundResource(R.drawable.rectangular_line)

            twenty_precent.setTextColor(resources.getColor(R.color.white))
            thirty_percent.setTextColor(resources.getColor(R.color.d_gray))
            fifty_percent.setTextColor(resources.getColor(R.color.d_gray))
            treat_me.setTextColor(resources.getColor(R.color.d_gray))
        }
        if (v?.id == R.id.thirty_percent) {
            contribution = "30%"

            twenty_precent.setBackgroundResource(R.drawable.rectangular_line)
            thirty_percent.setBackgroundResource(R.drawable.rounded_button)
            fifty_percent.setBackgroundResource(R.drawable.rectangular_line)
            treat_me.setBackgroundResource(R.drawable.rectangular_line)

            twenty_precent.setTextColor(resources.getColor(R.color.d_gray))
            thirty_percent.setTextColor(resources.getColor(R.color.white))
            fifty_percent.setTextColor(resources.getColor(R.color.d_gray))
            treat_me.setTextColor(resources.getColor(R.color.d_gray))
        }
        if (v?.id == R.id.fifty_percent) {
            contribution = "50%"
            twenty_precent.setBackgroundResource(R.drawable.rectangular_line)
            thirty_percent.setBackgroundResource(R.drawable.rectangular_line)
            fifty_percent.setBackgroundResource(R.drawable.rounded_button)
            treat_me.setBackgroundResource(R.drawable.rectangular_line)

            twenty_precent.setTextColor(resources.getColor(R.color.d_gray))
            thirty_percent.setTextColor(resources.getColor(R.color.d_gray))
            fifty_percent.setTextColor(resources.getColor(R.color.white))
            treat_me.setTextColor(resources.getColor(R.color.d_gray))
        }
        if (v?.id == R.id.treat_me) {
            contribution = "Treatme"

            twenty_precent.setBackgroundResource(R.drawable.rectangular_line)
            thirty_percent.setBackgroundResource(R.drawable.rectangular_line)
            fifty_percent.setBackgroundResource(R.drawable.rectangular_line)
            treat_me.setBackgroundResource(R.drawable.rounded_button)

            twenty_precent.setTextColor(resources.getColor(R.color.d_gray
            ))
            thirty_percent.setTextColor(resources.getColor(R.color.d_gray))
            fifty_percent.setTextColor(resources.getColor(R.color.d_gray))
            treat_me.setTextColor(resources.getColor(R.color.white))
        }
        if (v?.id == R.id.add_photo) {
//            selectImage("avatar")
            getImage()
            imageType = "avatar"
        }
        if (v?.id == R.id.change_photo) {
//            selectImage("cover")
            imageType = "cover"
            getImage()
        }
        if (v?.id == R.id.save_btn) {
           message = user_status.text.toString()
            age = user_age.text.toString()
            location = user_location.text.toString()
            val gender = ""

            if (formValidation(message, age, location, gender!!, contribution!!)) {
                updateProfile(meResponse.id)
            }
        }
        if (v?.id == R.id.food_add_btn) {
            addFoodBuilder()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        initializeListeners()

        val intent = getIntent();
        if (intent != null) {
            meResponse = intent.getSerializableExtra("meResponse") as MeResponse

            if(meResponse.profile.age != null){
                foodList = intent.getSerializableExtra("foodList") as ArrayList<Food>
            }
            setValue(meResponse)
        }
        setUpRecyclerView()
        Log.d("res", "" + meResponse.username)
    }

    private fun formValidation(status: String, age: String, location: String, gender: String, contribution: String): Boolean {
        if (status.isEmpty() || age.isEmpty() || location.isEmpty() || contribution!!.isEmpty()) {
            Toast.makeText(this@EditProfileActivity, "Please enter detail first", Toast.LENGTH_SHORT).show()
            return false
        } else if (status.isEmpty()) {
            Toast.makeText(this@EditProfileActivity, "Please enter Your Status", Toast.LENGTH_SHORT).show()
            return false
        } else if (age.isEmpty()) {
            Toast.makeText(this@EditProfileActivity, "Please enter Your Age", Toast.LENGTH_SHORT).show()
            return false
        } else if (location.isEmpty()) {
            Toast.makeText(this@EditProfileActivity, "Please enter Your Location", Toast.LENGTH_SHORT).show()
            return false
        } else if (contribution.isEmpty()) {
            Toast.makeText(this@EditProfileActivity, "Please enter Your Contribution", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Utils.isConnectedOnline(this)) {
            Toast.makeText(this@EditProfileActivity, "No internet Connection", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    private fun initializeListeners() {

        back_icon.setOnClickListener(this)
        save_btn.setOnClickListener(this)
        add_photo.setOnClickListener(this)
        change_photo.setOnClickListener(this)
        age_dropdown.setOnClickListener(this)
        location_dropdown.setOnClickListener(this)
        female_card_bg.setOnClickListener(this)
        male_card_bg.setOnClickListener(this)
        twenty_precent.setOnClickListener(this)
        thirty_percent.setOnClickListener(this)
        fifty_percent.setOnClickListener(this)
        treat_me.setOnClickListener(this)
        food_add_btn.setOnClickListener(this)

    }

    private fun ageBuilder(context: Context) {


        val country = arrayOf("Age:18-22", "Age:22-26", "Age:26-30", "Age:30-35", "Age:35-40")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Age")
                .setItems(country, DialogInterface.OnClickListener { dialogInterface, i ->
                    val selectedCountry = country[i]
                    select_age.text = selectedCountry.toString()
//                    Toast.makeText(context, selectedCountry, Toast.LENGTH_SHORT).show()
                })

        val dialog = builder.create()
        dialog.show()
    }

    private fun setValue(me: MeResponse) {
        if (me.profile == null) {
            user_name.setText(me.username.toString())
        } else {

            val requestOptionsCover = RequestOptions()
            requestOptionsCover.placeholder(R.drawable.cover_picture)
            requestOptionsCover.error(R.drawable.cover_picture)
            Glide.with(this).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/media/cover/" + me.id + "/" + me.profile.cover).into(cover_edit)


            val requestOptionsAvatar = RequestOptions()
            requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
            requestOptionsAvatar.error(R.drawable.profile_avatar)
            Glide.with(this).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + me.id + "/" + me.profile.avatar).into(profile_pic)

            user_name.setText(me.username.toString())
            user_status.setText(if(me.profile.message == null) "" else me.profile.message)
            user_age.setText(if(me.profile.age == null) "" else me.profile.age.toString())
            user_location.setText(if(me.profile.location == null) "" else me.profile.location.toString())

//            if (me.profile.gender.equals("Male")) {
////                me.profile.gender = "Male"
////                male_card_bg.setBackgroundResource(R.drawable.rounded_button)
////                male_txt.setTextColor(resources.getColor(R.color.white))
////
////            } else if (me.profile.gender.equals("Female")) {
////                me.profile.gender = "Female"
////
////                female_card_bg.setBackgroundResource(R.drawable.rounded_button)
////                female_txt.setTextColor(resources.getColor(R.color.white))
////            }

            select_age.text = if(me.profile.age == null) "" else me.profile.age.toString()
            select_age.text = if(me.profile.location == null) "" else me.profile.location
            contribution = me.profile.contribution.toString()

            if (me.profile.contribution.equals("20%")) {
                twenty_precent.setBackgroundResource(R.drawable.rounded_button)
                twenty_precent.setTextColor(resources.getColor(R.color.white))
            } else if (me.profile.contribution.equals("30%")) {
                thirty_percent.setBackgroundResource(R.drawable.rounded_button)
                thirty_percent.setTextColor(resources.getColor(R.color.white))
            } else if (me.profile.contribution.equals("50%")) {
                fifty_percent.setBackgroundResource(R.drawable.rounded_button)
                fifty_percent.setTextColor(resources.getColor(R.color.white))
            } else if (me.profile.contribution.equals("Treatme")) {
                treat_me.setBackgroundResource(R.drawable.rounded_button)
                treat_me.setTextColor(resources.getColor(R.color.white))
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            var selectedImage = ImagePicker.getFirstImageOrNull(data)
            photoPath = selectedImage.getPath();
            val task = imageTask()
            task.execute(photoPath)
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private inner class imageTask : AsyncTask<String, Void, File>() {

        private var compressedImage: File? = null

        override fun onPreExecute() {
            super.onPreExecute()
            KProgressHUD = progressDialog(this@EditProfileActivity, "", "Please wait for a moment...")
            KProgressHUD!!.show()
        }

        override fun doInBackground(vararg strings: String): File? {

            try {
                compressedImage = Compressor(this@EditProfileActivity)
                        .setMaxWidth(280)
                        .setMaxHeight(280)
                        .setQuality(50)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).absolutePath)
                        .compressToFile(File(strings[0]))
            } catch (e: IOException) {
                Toast.makeText(this@EditProfileActivity, "Some Error " + e.message, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

            return compressedImage
        }

        override fun onPostExecute(file: File) {
            super.onPostExecute(file)
            KProgressHUD!!.dismiss()
            imageFile = file
            if(!imageType.isEmpty()){
                uploadImage(imageType, imageFile)
            }else{
                Glide.with(this@EditProfileActivity).load(file.absoluteFile).into(food_pic!!)
            }

            if (imageType.equals("avatar")) {
                Glide.with(this@EditProfileActivity).load(file.absoluteFile).into(profile_pic!!)
            } else if (imageType.equals("cover")) {
                Glide.with(this@EditProfileActivity).load(file.absoluteFile).into(cover_edit!!)
            }

        }
    }

    fun getImage() {
        ImagePicker.create(this@EditProfileActivity)
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Foodie") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                .single() // single mode
                .showCamera(true) // show camera or not (true by default)
                .origin(image) // original selected images, used in multi mode
                .exclude(image) // exclude anything that in image.getPath()
                .enableLog(true) // disabling log
                .start() // start image picker activity with request code
    }

    fun uploadImage(imageTypeVal: String, file: File?) {
        KProgressHUD = progressDialog(this@EditProfileActivity, "", "Image Uploading...")
        KProgressHUD!!.show()

        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
//        hm["X-Requested-With"] = "XMLHttpRequest"
//        hm["Content-Type"] = "application/json"

        cv = MultipartBody.Part.createFormData("photo", if (file == null) "file" else file!!.name, RequestBody.create(MediaType.parse("image/*"), File(file!!.path)))
        val mService = ApiUtils.getSOService() as SOService
        mService.uploadImage(hm,
                RequestBody.create(MediaType.parse("text/plain"), imageTypeVal),
                cv
        ).enqueue(object : Callback<ImageUploadResp> {
            override fun onFailure(call: Call<ImageUploadResp>?, t: Throwable?) {
                KProgressHUD!!.dismiss()
                imageType = ""
                imageFile = null
                Toast.makeText(this@EditProfileActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ImageUploadResp>?, response: Response<ImageUploadResp>?) {
                imageType = ""
                imageFile = null
                KProgressHUD!!.dismiss()
                if (response!!.isSuccessful) {
                    if (response!!.body().type != null) {

                    } else {
                        Toast.makeText(this@EditProfileActivity, "Image not Upload", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Some thing worng your image is not upload try again later", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    fun updateProfile(userId: Int) {
        KProgressHUD = progressDialog(this@EditProfileActivity, "", "Please wait...")
        KProgressHUD!!.show()

        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
//        hm["X-Requested-With"] = "XMLHttpRequest"
//        hm["Content-Type"] = "application/json"

        //default category
        val category = arrayOf(1, 1)

        val jsonObject = JSONObject()
        jsonObject.put("username", meResponse.username)
        jsonObject.put("message", message)
        jsonObject.put("age",12)
        jsonObject.put("location", location)
        jsonObject.put("gender", "xyz")
        jsonObject.put("contribution", contribution)
//        jsonObject.put("categories", category)
        jsonObject.put("_method", "PATCH")

        val mService = ApiUtils.getSOService() as SOService
        mService.updateProfile(userId, hm, Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<UpdateProfileResp> {

                    override fun onFailure(call: Call<UpdateProfileResp>?, t: Throwable?) {
                        KProgressHUD!!.dismiss()
                        Toast.makeText(this@EditProfileActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<UpdateProfileResp>?, response: Response<UpdateProfileResp>?) {
                        KProgressHUD!!.dismiss()
                        Toast.makeText(this@EditProfileActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
//                        finish()


                    }
                })

    }

    override fun onStart() {
        super.onStart()
        Log.d("on start", "run")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume", "run")

    }

    fun addFoodBuilder(){

        imageFile = null
        imageType = ""

        val builder = android.support.v7.app.AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)

        val dialog_layout = inflater.inflate(R.layout.add_food_builder, null)
        builder.setView(dialog_layout)
        var food_image = dialog_layout.findViewById<View>(R.id.food_image_picker) as ImageView
        food_pic = food_image
        var change_text = dialog_layout.findViewById<View>(R.id.add_food) as TextView
        var food_name= dialog_layout.findViewById<View>(R.id.et_food_name) as EditText
        var add_food_btn = dialog_layout.findViewById<View>(R.id.btn_add_food) as LinearLayout

        change_text!!.setOnClickListener {
            isFoodPicAdded = true
            getImage()
        }

        add_food_btn!!.setOnClickListener(View.OnClickListener {
            val foodName = food_name.text.toString()

            if (TextUtils.isEmpty(foodName)){
                Toast.makeText(this@EditProfileActivity, "Please Enter Food Name", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                imageType = foodName
                addFood(imageType)
            }
        })

        dialog = builder.create()

        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_corner)
        dialog!!.show()
    }

    fun addFood(foodName : String){
        KProgressHUD = progressDialog(this@EditProfileActivity, "", "Please wait...")
        KProgressHUD!!.show()

        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()

        cv = MultipartBody.Part.createFormData("photo", if (imageFile == null) "file" else imageFile!!.name, RequestBody.create(MediaType.parse("image/*"), File(imageFile!!.path)))
        val mService = ApiUtils.getSOService() as SOService
        mService.addFood(hm,
                RequestBody.create(MediaType.parse("text/plain"), foodName),
                cv
        ).enqueue(object : Callback<AddFoodResp> {

            override fun onFailure(call: Call<AddFoodResp>?, t: Throwable?) {
                dialog!!.dismiss()
                KProgressHUD!!.dismiss()
                imageType = ""
                imageFile = null
                Toast.makeText(this@EditProfileActivity, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<AddFoodResp>?, response: Response<AddFoodResp>?) {
                dialog!!.dismiss()
                imageType = ""
                imageFile = null
                KProgressHUD!!.dismiss()
                if (response != null) {
                    if (response.body().success) {
                        foodList.add(0,response.body().data)
                        profileAdapter.update(foodList)
                        Toast.makeText(this@EditProfileActivity, ""+response.body().message, Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@EditProfileActivity, "your prefer Food not Upload yet...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Some thing worng your image is not upload try again later", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    fun setUpRecyclerView() {
        profileAdapter = ProfileFoodAdapter(this!!,foodList)
        friend_like_food.setHasFixedSize(false)
        val layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        friend_like_food.layoutManager = layoutManager
        friend_like_food.adapter = profileAdapter
    }

}
