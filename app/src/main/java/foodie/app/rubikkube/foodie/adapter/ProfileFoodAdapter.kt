package foodie.app.rubikkube.foodie.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Food
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.*
import app.wi.lakhanipilgrimage.api.SOService
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.activities.EditProfileActivity
import foodie.app.rubikkube.foodie.activities.HomeActivity
import foodie.app.rubikkube.foodie.fragments.ProfileFragment
import foodie.app.rubikkube.foodie.model.AddFoodResp
import foodie.app.rubikkube.foodie.model.DeleteFoodResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.HashMap


class ProfileFoodAdapter(context: Context, list : ArrayList<Food>)  : RecyclerView.Adapter<ProfileFoodAdapter.ProfileFoodHolder>() {

    val mContext = context
    var foodList = list
    var deleteFoodResponse = DeleteFoodResponse()
    var KProgressHUD: KProgressHUD? = null
    internal var cv: MultipartBody.Part? = null
    private var image: ArrayList<Image>? = null
    private var dialog: AlertDialog? = null
    private var imageFile: File? = null
    private var isFoodPicAdded: Boolean = false
    private var food_pic : ImageView? = null
    private var photoPath: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFoodHolder {

        val inflater = LayoutInflater.from(parent?.context)
        return ProfileFoodHolder(inflater.inflate(R.layout.holder_profile_food, parent, false))

    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: ProfileFoodHolder, position: Int) {


        val requestOptionsCover = RequestOptions()
        requestOptionsCover.placeholder(R.drawable.food_bg)
        requestOptionsCover.error(R.drawable.food_bg)
        Glide.with(mContext).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/foods/"+foodList.get(position).photo).into(holder.foodImage)
        Glide.with(mContext).load(R.drawable.edit_image).into(holder.foodEditImage)
        Glide.with(mContext).load(R.drawable.delete).into(holder.foodDeleteImage)
        holder.foodText.text = foodList[position].name

        if(foodList[position].pivot.profileId != Prefs.getString(Constant.USERID,"")){
            holder.foodEditImage.visibility = View.INVISIBLE
            holder.foodDeleteImage.visibility = View.INVISIBLE
        }

        holder.foodEditImage.setOnClickListener {
            updateFoodBuilder(position)
        }

        holder.foodDeleteImage.setOnClickListener {
            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("Delete Food")
            alert.setMessage("Are you sure you want to delete the Food?")
            alert.setPositiveButton(android.R.string.yes) { dialog, which ->
                deleteSpecificFood(foodList[position].id.toString())
                removeFood(position)
            }
            alert.setNegativeButton(android.R.string.no) { dialog, which ->
                dialog.cancel()
            }
            alert.show()
        }
    }


    inner class ProfileFoodHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val foodImage: ImageView = view.findViewById(R.id.food_image)
        val foodText: TextView = view.findViewById(R.id.food_text)
        val foodDeleteImage: ImageView = view.findViewById(R.id.delete_food_icon)
        val foodEditImage: ImageView = view.findViewById(R.id.edit_food_icon)
    }

    fun update(list : ArrayList<Food>){
        foodList = list
        notifyDataSetChanged()
    }

    private fun removeFood(position: Int) {
        foodList.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun deleteSpecificFood(food_id:String){
        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val mService = ApiUtils.getSOService() as SOService
        val jsonObject = JSONObject()
        jsonObject.put("_method", "DELETE")
        mService.deleteMyFood(hm,food_id, Utils.getRequestBody(jsonObject.toString()))
        .enqueue(object : Callback<DeleteFoodResponse> {
            override fun onFailure(call: Call<DeleteFoodResponse>?, t: Throwable?) {
                Toasty.error(mContext, ""+t!!.message, Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<DeleteFoodResponse>?, response: Response<DeleteFoodResponse>?) {
                if(response!!.isSuccessful){
                    deleteFoodResponse = response.body()
                    Toasty.success(mContext,deleteFoodResponse.message,Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun updateFoodBuilder(position: Int){

        imageFile = null

        val builder = AlertDialog.Builder(mContext)
        val inflater = LayoutInflater.from(mContext)

        val dialog_layout = inflater.inflate(R.layout.add_food_builder, null)
        builder.setView(dialog_layout)
        var food_image = dialog_layout.findViewById<View>(R.id.food_image_picker) as ImageView
        var change_text = dialog_layout.findViewById<View>(R.id.add_food) as TextView
        var food_name= dialog_layout.findViewById<View>(R.id.et_food_name) as EditText
        var update_food_btn = dialog_layout.findViewById<View>(R.id.btn_add_food) as LinearLayout
        var txt_add_food_btn = dialog_layout.findViewById<View>(R.id.txt_add_food_btn) as TextView
        val requestOptionsCover = RequestOptions()
        requestOptionsCover.placeholder(R.drawable.food_bg)
        requestOptionsCover.error(R.drawable.food_bg)
        Glide.with(mContext).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/foods/"+ foodList[position].photo).into(food_image)
        change_text.text = "Change Food Image"
        txt_add_food_btn.text = "Update Food"
        food_name.setText(foodList[position].name.toString())

        change_text!!.setOnClickListener {
            isFoodPicAdded = true

        }

        update_food_btn!!.setOnClickListener(View.OnClickListener {
            /*val foodName = food_name.text.toString()

            if (TextUtils.isEmpty(foodName)) {
                Toast.makeText(mContext, "Please Enter Food Name", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else{
                if (imageFile != null) {
                    updateFood(foodList[position].name,foodList[position].id.toString(),position)
                }
                else{
                    Toasty.error(mContext, "Please Upload Food Image", Toast.LENGTH_SHORT).show()
                }
            }*/
        })

        dialog = builder.create()

        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_corner)
        dialog!!.show()
    }


    private inner class imageTask : AsyncTask<String, Void, File>() {

        private var compressedImage: File? = null

        override fun onPreExecute() {
            super.onPreExecute()
            KProgressHUD = Utils.progressDialog(mContext, "", "Please wait for a moment...")
            KProgressHUD!!.show()
        }

        override fun doInBackground(vararg strings: String): File? {

            try {
                compressedImage = Compressor(mContext)
                        .setMaxWidth(280)
                        .setMaxHeight(280)
                        .setQuality(50)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).absolutePath)
                        .compressToFile(File(strings[0]))
            } catch (e: IOException) {
                Toast.makeText(mContext, "Some Error " + e.message, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

            return compressedImage
        }

        override fun onPostExecute(file: File) {
            super.onPostExecute(file)
            KProgressHUD!!.dismiss()
            imageFile = file
            if(imageFile!=null){
                Glide.with(mContext).load(file.absoluteFile).into(food_pic!!)
            }
        }
    }

//    fun getImage() {
//        ImagePicker.create(EditProfileActivity)
//                .folderMode(true) // folder mode (false by default)
//                .toolbarFolderTitle("Foodie") // folder selection title
//                .toolbarImageTitle("Tap to select") // image selection title
//                .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
//                .single() // single mode
//                .showCamera(true) // show camera or not (true by default)
//                .origin(image) // original selected images, used in multi mode
//                .exclude(image) // exclude anything that in image.getPath()
//                .enableLog(true) // disabling log
//                .start() // start image picker activity with request code
//
//
//    }


    fun updateFood(foodName : String,foodId:String,position: Int){
        KProgressHUD = Utils.progressDialog(mContext, "", "Please wait...")
        KProgressHUD!!.show()

        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        val jsonObject = JSONObject()
        jsonObject.put("_method", "PUT")
        cv = MultipartBody.Part.createFormData("photo", if (imageFile == null) "file" else imageFile!!.name, RequestBody.create(MediaType.parse("image/*"), File(imageFile!!.path)))
        val mService = ApiUtils.getSOService() as SOService
                        mService.updateFood(hm,
                RequestBody.create(MediaType.parse("text/plain"), foodName),
                cv,
                foodId,
                Utils.getRequestBody(jsonObject.toString())
                ).enqueue(object : Callback<AddFoodResp> {

            override fun onFailure(call: Call<AddFoodResp>?, t: Throwable?) {
                dialog!!.dismiss()
                KProgressHUD!!.dismiss()
                imageFile = null
                Toast.makeText(mContext, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<AddFoodResp>?, response: Response<AddFoodResp>?) {
                dialog!!.dismiss()
                imageFile = null
                KProgressHUD!!.dismiss()
                if (response != null) {
                    if (response.body().success) {
                        foodList.add(position,response.body().data)
                        //profileAdapter.update(foodList)
                        Toast.makeText(mContext, ""+response.body().message, Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(mContext, "your prefer Food not Upload yet...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(mContext, "Some thing worng your image is not upload try again later", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }
}