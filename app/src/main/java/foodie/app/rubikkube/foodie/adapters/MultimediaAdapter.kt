package foodie.app.rubikkube.foodie.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.models.LoginSignUpResponse
import foodie.app.rubikkube.foodie.utilities.Constants
import foodie.app.rubikkube.foodie.utilities.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MultimediaAdapter(private val context: Context, private var filePaths: ArrayList<String>, private val fileUrls: ArrayList<String>, private val comingFrom: String): RecyclerView.Adapter<MultimediaAdapter.MyViewHolder>() {

    private val postId: String = Prefs.getString("EditPostId", "")
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val imageView: ImageView = itemView.findViewById(R.id.imgOne) as ImageView
        internal val deleteIcon: ImageView = itemView.findViewById(R.id.delete_btn) as ImageView
    }


    override fun getItemCount(): Int = this.fileUrls.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
            MyViewHolder(this.inflater.inflate(R.layout.multimedia_grid_item, parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val file = fileUrls[position]

        if (this.comingFrom == "addPost") {
            if (position == this.fileUrls.size - 1) {
                Glide.with(context).load(R.drawable.ic_add_black_24dp).into(holder.imageView)
                holder.deleteIcon.visibility = View.INVISIBLE
            } else {
                Glide.with(context).load(file).into(holder.imageView)
                holder.deleteIcon.visibility = View.VISIBLE
            }
        } else {
            if (position == filePaths.size - 1) {
                Glide.with(context).load(R.drawable.ic_add_black_24dp).into(holder.imageView)
                holder.deleteIcon.visibility = View.INVISIBLE
            } else {
                Glide.with(context).load(file).into(holder.imageView)
                holder.deleteIcon.visibility = View.VISIBLE
            }
        }

        holder.deleteIcon.setOnClickListener {
            if ("http" in this.fileUrls[position]) {
                AlertDialog.Builder(this.context)
                        .setTitle("Confirmation Message")
                        .setMessage("Are you sure you want to delete the post image?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            this.deletePostImage(this.filePaths[position], this.postId)
                            this.filePaths.removeAt(position)
                            this.fileUrls.removeAt(position)
                            super.notifyDataSetChanged()
                        }
            }
        }
    }

    private fun deletePostImage(photoUrl: String, postId: String) {
        val map = HashMap<String, String>()
        map["Authorization"] = Prefs.getString(Constants.TOKEN, "")

        ApiUtils.getSOService()?.deletePostImage(map, Utils.getRequestBody(JSONObject()
                .put("id", postId).put("photo", photoUrl).toString()))
                ?.enqueue(object: Callback<LoginSignUpResponse> {
            override fun onFailure(call: Call<LoginSignUpResponse>?, t: Throwable?) {}
            override fun onResponse(call: Call<LoginSignUpResponse>?, response: Response<LoginSignUpResponse>?) {}
        })
    }
}
