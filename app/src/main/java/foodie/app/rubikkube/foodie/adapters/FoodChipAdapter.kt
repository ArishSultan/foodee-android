package foodie.app.rubikkube.foodie.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import foodie.app.rubikkube.foodie.R

class FoodChipAdapter(context: Context, foodName: EditText)  : androidx.recyclerview.widget.RecyclerView.Adapter<FoodChipAdapter.FoodsChipsViewHolder>() {

    val mContext = context
    val mEditText = foodName
//    var mSelectedFood = selectedFood
//    val mDialog = dialog
    val foodArray = arrayOf("Pizza","‎‎Vegetarian","Chineese Food","Biryani","Indian Food","Fast Food","Fried","Halal Food","Lasagna","SeaFood")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodsChipsViewHolder {

        val inflater = LayoutInflater.from(mContext)
        return FoodsChipsViewHolder(inflater.inflate(R.layout.chips_layout_item, parent, false))

    }

    override fun getItemCount(): Int {
        return foodArray.size
    }

    override fun onBindViewHolder(holder: FoodsChipsViewHolder, position: Int) {


        /*val requestOptionsCover = RequestOptions()
        requestOptionsCover.placeholder(R.drawable.profile_avatar)
        requestOptionsCover.error(R.drawable.profile_avatar)
        Glide.with(mContext).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/media/post/"+searchUser.get(position).profile.avatar).into(holder.searchUserImage)
        */
        holder.chipsText.text = foodArray[position]

        holder.chipsText.setOnClickListener {


            mEditText.setText(foodArray[position])


        }
    }


    inner class FoodsChipsViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val chipsText: TextView = view.findViewById(R.id.chips_text)
    }


}