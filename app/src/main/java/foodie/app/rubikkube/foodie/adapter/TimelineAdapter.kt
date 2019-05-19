package foodie.app.rubikkube.foodie.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.smarteist.autoimageslider.DefaultSliderView
import com.smarteist.autoimageslider.SliderLayout
import de.hdodenhof.circleimageview.CircleImageView
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.FeedData


class TimelineAdapter(context: Context, feedDate: List<FeedData>?) : RecyclerView.Adapter<TimelineAdapter.TimelineHolder>(){

    val mContext = context
    var listFeedData = feedDate

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineHolder {

        val inflater = LayoutInflater.from(parent?.context)
        return TimelineHolder(inflater.inflate(R.layout.holder_timelinefragment, parent, false))

    }

    override fun getItemCount(): Int {
        return listFeedData!!.size
    }

    override fun onBindViewHolder(holder: TimelineHolder, position: Int) {

        val requestOptionsAvatar = RequestOptions()
        requestOptionsAvatar.placeholder(R.drawable.profile_avatar)
        requestOptionsAvatar.error(R.drawable.profile_avatar)

        if(listFeedData?.get(position)!!.user.profile.avatar!=null) {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(ApiUtils.BASE_URL + "/storage/media/avatar/" + listFeedData?.get(position)!!.user.profile.id + "/" + listFeedData?.get(position)!!.user.profile.avatar).into(holder.profile_image)
        }
        else
        {
            Glide.with(mContext).setDefaultRequestOptions(requestOptionsAvatar).load(R.drawable.profile_avatar).into(holder.profile_image)
        }
        holder.imageSlider.clearSliderViews()
        if(listFeedData?.get(position)!!.photos!=null ){
            if(!(listFeedData?.get(position)!!.photos.contains(""))) {
                Log.d("EMpty", "" + listFeedData?.get(position)!!.photos.contains(""))
                Log.d("content",listFeedData?.get(position)!!.content)
                for (i in listFeedData?.get(position)!!.photos.indices) {
                    holder.imageSlider.visibility = View.VISIBLE
                    val sliderView = DefaultSliderView(mContext)
                    sliderView.imageUrl = ApiUtils.BASE_URL + "/storage/media/post/" + listFeedData?.get(position)!!.photos.get(i)
                    holder.imageSlider.addSliderView(sliderView)
                    //sliderView.setImageScaleType(ImageView.ScaleType.FIT_XY)
                    Log.d("ImageURL", ApiUtils.BASE_URL + "/storage/media/post/" + listFeedData?.get(position)!!.photos.get(i) + " Size " + listFeedData?.get(position)!!.photos.size)
                }
            }
            else
            {
                holder.imageSlider.visibility = View.GONE
            }
        }
        holder.user_name.text = listFeedData!!.get(position).user.username
        holder.txt_content.text = listFeedData!!.get(position).content
        holder.time_ago.text = listFeedData!!.get(position).createdAt

    }


    inner class TimelineHolder(val view: View): RecyclerView.ViewHolder(view) {

        val profile_image:CircleImageView = view.findViewById(R.id.profile_image)
        val user_name:TextView = view.findViewById(R.id.user_name)
        val time_ago:TextView = view.findViewById(R.id.time_ago)
        val imageSlider:SliderLayout = view.findViewById(R.id.imageSlider)
        val txt_content:TextView = view.findViewById(R.id.txt_content)
        val comment_txt:TextView = view.findViewById(R.id.comment_txt)
        val like_txt:TextView = view.findViewById(R.id.like_txt)
    }

    fun update(list : List<FeedData>?){
        listFeedData = list
        notifyDataSetChanged()
    }

}