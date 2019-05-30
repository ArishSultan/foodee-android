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
import foodie.app.rubikkube.foodie.model.User

class SearchUserAdapter(context: Context, list : List<User>)  : RecyclerView.Adapter<SearchUserAdapter.SearchUserHolder>() {

    val mContext = context
    var searchUser = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserHolder {

        val inflater = LayoutInflater.from(parent?.context)
        return SearchUserHolder(inflater.inflate(R.layout.item_user, parent, false))

    }

    override fun getItemCount(): Int {
        return searchUser.size
    }

    override fun onBindViewHolder(holder: SearchUserHolder, position: Int) {


        /*val requestOptionsCover = RequestOptions()
        requestOptionsCover.placeholder(R.drawable.profile_avatar)
        requestOptionsCover.error(R.drawable.profile_avatar)
        Glide.with(mContext).setDefaultRequestOptions(requestOptionsCover).load(ApiUtils.BASE_URL + "/storage/media/post/"+searchUser.get(position).profile.avatar).into(holder.searchUserImage)
        */
        holder.textSearchUser.text = searchUser.get(position).username
    }


    inner class SearchUserHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val searchUserImage: ImageView = view.findViewById(R.id.search_profile_image)
        val textSearchUser: TextView = view.findViewById(R.id.search_user_name)
    }

    fun getUser(position: Int):User{
        return searchUser.get(position)
    }


    fun update(list : List<User>){
        searchUser = list
        notifyDataSetChanged()
    }
}