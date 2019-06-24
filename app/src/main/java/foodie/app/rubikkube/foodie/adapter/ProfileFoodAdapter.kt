package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Food
import android.support.v7.app.AlertDialog
import app.wi.lakhanipilgrimage.api.SOService
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.model.DeleteFoodResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


class ProfileFoodAdapter(context: Context, list : ArrayList<Food>)  : RecyclerView.Adapter<ProfileFoodAdapter.ProfileFoodHolder>() {

    val mContext = context
    var foodList = list
    var deleteFoodResponse = DeleteFoodResponse()

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

        if(!(foodList[position].pivot.profileId.equals(Prefs.getString(Constant.USERID,"")))){
            holder.foodEditImage.visibility = View.INVISIBLE
            holder.foodDeleteImage.visibility = View.INVISIBLE
        }

        holder.foodEditImage.setOnClickListener {
            Toasty.success(mContext,"Clicked on Edit Food Image", Toast.LENGTH_SHORT).show()
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
        val foodImage: ImageView = view.findViewById(foodie.app.rubikkube.foodie.R.id.food_image)
        val foodText: TextView = view.findViewById(foodie.app.rubikkube.foodie.R.id.food_text)
        val foodDeleteImage: ImageView = view.findViewById(foodie.app.rubikkube.foodie.R.id.delete_food_icon)
        val foodEditImage: ImageView = view.findViewById(R.id.edit_food_icon)
    }

    fun update(list : ArrayList<Food>){
        foodList = list
        notifyDataSetChanged()
    }

    fun removeFood(position: Int) {
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
}