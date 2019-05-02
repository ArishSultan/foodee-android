package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Food
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFoodAdapter(context: Context, list : ArrayList<Food>)  : RecyclerView.Adapter<ProfileFoodAdapter.ProfileFoodHolder>() {

    val mContext = context
    var foodList = list

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
        holder.foodText.text = foodList.get(position).name
    }


    inner class ProfileFoodHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val foodImage: ImageView = view.findViewById(R.id.food_image)
        val foodText: TextView = view.findViewById(R.id.food_text)
    }

    fun update(list : ArrayList<Food>){
        foodList = list
        notifyDataSetChanged()
    }
}