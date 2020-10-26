package foodie.app.rubikkube.foodie.ui.home

import android.content.Context
import android.content.Intent
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import com.smarteist.autoimageslider.SliderView
import com.smarteist.autoimageslider.SliderViewAdapter
import de.hdodenhof.circleimageview.CircleImageView
import foodie.app.rubikkube.foodie.JavaUtils
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.EditPostActivity
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.activities.TimelinePostDetailActivity
import foodie.app.rubikkube.foodie.activities.WhoLikesActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.models.*
import foodie.app.rubikkube.foodie.models.feed.Feed
import foodie.app.rubikkube.foodie.utilities.Constants
import foodie.app.rubikkube.foodie.utilities.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedAdapter: PagedListAdapter<Feed, FeedAdapter.FeedViewHolder>(Feed.CALLBACK) {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        this.context = parent.context
        Hawk.init(context).build()

        return FeedViewHolder(LayoutInflater.from(this.context).inflate(R.layout.holder_timelinefragment, parent, false))
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val requestOptions = RequestOptions()
                .error(R.drawable.profile_avatar)
                .placeholder(R.drawable.profile_avatar)

        if (super.getItemCount() == 0) return
        val feed: Feed = this.getItem(position)!!

        Glide.with(this.context).setDefaultRequestOptions(requestOptions)
                .load(
                    if (feed.user.profile?.avatar != null)
                        "${ApiUtils.BASE_URL}/storage/media/avatar/${feed.user.profile?.userId}/${feed.user.profile?.avatar}"
                    else R.drawable.profile_avatar
                ).into(holder.profileImage)

        if (feed.photos != null) {
            if (feed.photos!!.isNotEmpty()) {
                holder.imageSlider.isVisible = true
                holder.imageSlider.setSliderAdapter(SliderAdapterExample(feed.photos!!, context, feed.id.toString()))
            } else {
                holder.imageSlider.isVisible = false
            }
        }

        holder.likeIcon.setImageResource(
            if (feed.isLiked) R.drawable.ic_liked
            else R.drawable.like_bubble_icon
        )

        holder.txtContent.text = feed.content

        holder.shareIcon.setOnClickListener {
            sharePost(this.context, feed.content)
        }

        /// Handle Tags.
        if (feed.tags.isNotEmpty()) {
            holder.apply {
                imgIsWith.isVisible = true
                txtTaggedUser.isVisible = true
                txtTaggedUser.text = feed.tags.first().username
            }
        } else {
            holder.apply {
                imgIsWith.isVisible = false
                txtTaggedUser.isVisible = false
            }
        }

        /// Handle Comments.
        if (feed.comments.isNotEmpty()) {
            holder.lytComment.isVisible = true

            /// Last Comment on this Feed / Post.
            val lastComment = feed.comments.last()

            Glide.with(this.context).setDefaultRequestOptions(requestOptions)
                    .load(
                        if (lastComment.user?.profile?.avatar != null)
                            "${ApiUtils.BASE_URL}/storage/media/avatar/${lastComment.user?.id}/${lastComment.user?.profile?.avatar}"
                        else R.drawable.profile_avatar
                    ).into(holder.commentProfileImage)

            holder.apply {
                commentContent.text = lastComment.content
                commentTimeAgo.text = lastComment.createdAt
                commentUsername.text = lastComment.user?.username ?: "No Name"
            }
        } else holder.lytComment.isVisible = false

        holder.apply {
            timeAgo.text = feed.createdAt
            username.text = feed.user.username
            likeText.text = feed.likesCount.toString()
            commentTxt.text = feed.commentsCount.toString()

            txtUserLink.isVisible = false
            txtViewOptions.isVisible =
                    feed.userId.toString() == Prefs.getString(Constants.USER_ID, "")

            /// Like Text Action
            likeText.setOnClickListener {
                context.startActivity(
                    Intent(context, WhoLikesActivity::class.java)
                        .putExtra("postId", feed.id))
            }
        }

        val content = feed.content
        val url = JavaUtils.checkUserId(content)

        if (url.isNotEmpty()) {
            val uID = url.split("/")
            val linkUrl = "https://foodee/friend/${uID[4]}"
            val contentWithUrl = content.replace(url, linkUrl)

            holder.txtContent.text = contentWithUrl
            holder.txtContent.makeLinks(Pair(linkUrl, View.OnClickListener {
                if (holder.txtViewOptions.isVisible) {
                    val intent = Intent(context, OtherUserProfileDetailActivity::class.java)
                    intent.putExtra("id", uID[4])
                    context.startActivity(intent)
                } else holder.txtContent.text = content
            }))
        }
        holder.likeIcon.setOnClickListener {
            handleLike(feed.id, it)

            if(feed.isLiked) {
                feed.isLiked = false
                holder.likeIcon.setImageResource(R.drawable.like)
                holder.likeText.text = (--feed.likesCount).toString()
            } else {
                feed.isLiked = true
                holder.likeIcon.setImageResource(R.drawable.ic_liked)
                holder.likeText.text = (++feed.likesCount).toString()

                if (Prefs.getString(Constants.USER_ID, "").toInt() != feed.user.id)
                    Utils.sendSimpleNotification(
                            this.context,
                            "Foodee",
                            "${Prefs.getString(Constants.NAME, "No Name")} likes your Post",
                            feed.user.deviceToken ?: "", "nothing", "like", feed.id.toString())
            }
        }
        holder.txtContent.setOnClickListener {
            Log.d("TAPPED HERE2", "TAPPED")

            this.context.startActivity(Intent(this.context, TimelinePostDetailActivity::class.java)
                    .putExtra("PostID", feed.id.toString()))
        }
        holder.txtUserLink.setOnClickListener {
            /// Check if this post is by current user
            if (holder.txtViewOptions.isVisible) {
//                val mURl = JavaUtils.checkUserId(content)
//                val uID = mURl?.split("/")
//                val intent = Intent(this.context, OtherUserProfileDetailActivity::class.java)
//                intent.putExtra("id", uID!![4])
//                this.context.startActivity(intent)
//
                Utils.navigateToUserProfile(this.context, feed.userId.toString())
            }

        }
        holder.btnSendMessage.setOnClickListener {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            if (holder.editMessage.text.isEmpty()) {
                Snackbar.make(it, "Enter Comment first", Snackbar.LENGTH_SHORT).show()
                imm.hideSoftInputFromWindow(holder.view.windowToken, 0)
            } else {
                addComment(holder.editMessage.text.toString(), feed, context, this, it)

                holder.editMessage.text.clear()
                imm.hideSoftInputFromWindow(holder.view.windowToken, 0)
                holder.commentTxt.text = (++feed.commentsCount).toString()
            }
        }
        holder.txtViewOptions.setOnClickListener {
            PopupMenu(this.context, holder.txtViewOptions).apply {
                inflate(R.menu.post_option_menu)

                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.update_post -> {
                            Hawk.put("feed", feed)
                            context.startActivity(Intent(context, EditPostActivity::class.java))
                        }

                        R.id.delete_post -> {
                            AlertDialog.Builder(context)
                                .setTitle("Delete Post")
                                .setMessage("Are you sure you want to delete the Post?")
                                .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                                    deletePost(feed.id)
                                }
                                .setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ ->
                                    dialog.cancel()
                                }
                                .show()
                        }
                    }

                    true
                }
            }.show()
        }
        holder.chatBubbleIcon.setOnClickListener {
        }

        holder.username.setOnClickListener {
            Utils.navigateToUserProfile(this.context, feed.userId.toString())
        }
        holder.profileImage.setOnClickListener {
            Utils.navigateToUserProfile(this.context, feed.userId.toString())
        }
        holder.txtTaggedUser.setOnClickListener {
            Utils.navigateToUserProfile(this.context, feed.tags[0].pivot.userId.toString())
        }

        holder.commentUsername.setOnClickListener {
            Utils.navigateToUserProfile(this.context, feed.comments.last().userId.toString())
        }
        holder.commentProfileImage.setOnClickListener {
            Utils.navigateToUserProfile(this.context, feed.comments.last().userId.toString())
        }
    }

    /// Extension Functions
    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }


    /// Utility Function
    companion object {
        private fun handleLike(postId: Int, view: View) {
            val service = ApiUtils.getSOService()!!
            val hm = HashMap<String, String>()
            hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
            hm["X-Requested-With"] = "XMLHttpRequest"

            service.likeAndUnlike(postId, hm).enqueue(object : Callback<LikeResponse> {
                override fun onFailure(call: Call<LikeResponse>?, t: Throwable?) {
                    Snackbar.make(view, "No Internet is Available", 600)
                }
                override fun onResponse(call: Call<LikeResponse>?, response: Response<LikeResponse>?) {}
            })
        }

        private fun addComment(content: String, feed: Feed, context: Context, adapter: PagedListAdapter<*, *>, view: View) {
            val service = ApiUtils.getSOService()!!
            val hm = java.util.HashMap<String, String>()
            hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
            hm["X-Requested-With"] = "XMLHttpRequest"

            val jsonObject = JSONObject()
            jsonObject.put("content", content)
            jsonObject.put("post_id", feed.id)

            service.addNewComment(hm, Utils.getRequestBody(jsonObject.toString())).enqueue(object : Callback<CommentResponse> {
                override fun onFailure(call: Call<CommentResponse>?, t: Throwable?) {
                    Snackbar.make(view, "No Internet Connection Found", 600)
                }

                override fun onResponse(call: Call<CommentResponse>?, response: Response<CommentResponse>?) {
                    val me = Hawk.get<MeResponse>("profileResponse")

                    val comment = CommentData()

                    response?.body().apply {
                        comment.id = this?.id
                        comment.postId = this?.postId?.toInt()
                        comment.content = this?.content
                        comment.createdAt = "Just now"
                    }

                    val user = User()
                    val profile = Profile()

                    profile.avatar = me.profile!!.avatar

                    user.apply {
                        this.id = me.id
                        this.profile = profile
                        this.username = me.username
                    }

                    comment.user = user

                    feed.comments.add(comment)
                    adapter.notifyDataSetChanged()

                    if(Prefs.getString(Constants.USER_ID,"").toInt() != feed.user.id) {
                            Utils.sendSimpleNotification(
                                    context,
                                    "Foodee",
                                    "${Prefs.getString(Constants.NAME, "No Name")} commented on your post",
                                    feed.user.deviceToken ?: "",
                                    "nothing", "post", feed.id.toString())
                    }

                    Snackbar.make(view, "Comment Posted Successfully!", 600)
                }
            })
        }

        private fun deletePost(postId: Int) {
            val hm = HashMap<String, String>()
            hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()

            val service = ApiUtils.getSOService()!!
            val jsonObject = JSONObject()

            jsonObject.put("_method", "DELETE")
            service.deletePost(postId, hm, Utils.getRequestBody(jsonObject.toString())).enqueue(object : Callback<DeleteFoodAndPostResponse> {
                override fun onFailure(call: Call<DeleteFoodAndPostResponse>?, t: Throwable?) {
                    Log.d("Remove Post Err", t?.message ?: "No Message")
                }

                override fun onResponse(call: Call<DeleteFoodAndPostResponse>?, andPostResponse: Response<DeleteFoodAndPostResponse>?) {
                    Log.d("Remove Post Rsp", andPostResponse?.body().toString())
                }
            })
        }

        private fun sharePost(context: Context, text: String) {
             context.startActivity(Intent.createChooser(Intent()
                     .setAction(Intent.ACTION_SEND)
                     .putExtra(Intent.EXTRA_TEXT, text)
                     .putExtra(Intent.EXTRA_SUBJECT, "https://play.google.com/store/apps/details?id=foodie.app.rubikkube.foodie&hl=en")
                     .setType("text/plain"), "send"))
        }
    }

    inner class FeedViewHolder(val view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val timeAgo = view.findViewById<TextView>(R.id.time_ago)!!
        val likeIcon = view.findViewById<ImageView>(R.id.like_icon)!!
        val likeText = view.findViewById<TextView>(R.id.like_txt)!!
        val username = view.findViewById<TextView>(R.id.user_name)!!
        val imgIsWith = view.findViewById<ImageView>(R.id.img_is_with)!!
        val shareIcon = view.findViewById<ImageView>(R.id.share_icon)!!
        val txtContent = view.findViewById<TextView>(R.id.txt_content)!!
        val lytComment = view.findViewById<RelativeLayout>(R.id.lyt_comment)!!
        val commentTxt = view.findViewById<TextView>(R.id.comment_txt)!!
        val editMessage = view.findViewById<EditText>(R.id.edt_msg)!!
        val txtUserLink = view.findViewById<TextView>(R.id.txt_user_link)!!
        val imageSlider = view.findViewById<SliderView>(R.id.imageSlider)!!
        val profileImage = view.findViewById<CircleImageView>(R.id.profile_image)!!
        val showImgSlide = view.findViewById<ImageView>(R.id.show_img_slide)!!
        val txtTaggedUser = view.findViewById<TextView>(R.id.txt_tagged_user)!!
        val chatBubbleIcon = view.findViewById<ImageView>(R.id.chat_bubble_icon)!!
        val btnSendMessage = view.findViewById<Button>(R.id.btn_send_msg)!!
        val txtViewOptions = view.findViewById<TextView>(R.id.txtViewOptions)!!
        val commentTimeAgo = view.findViewById<TextView>(R.id.comment_time_ago)!!
        val commentUsername = view.findViewById<TextView>(R.id.comment_user_name)!!
        val commentContent = view.findViewById<TextView>(R.id.txt_comment_content)!!
        val commentProfileImage = view.findViewById<CircleImageView>(R.id.comment_profile_image)!!
    }
}

class SliderAdapterExample(private val images: ArrayList<String>, val context: Context, val id: String?) : SliderViewAdapter<SliderAdapterExample.SliderAdapterVH?>() {
    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun getCount(): Int = images.size

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        val requestOptions = RequestOptions()
                .error(R.drawable.profile_avatar)
                .placeholder(R.drawable.profile_avatar)

        Glide.with(viewHolder!!.itemView).setDefaultRequestOptions(requestOptions)
                .load(ApiUtils.BASE_URL + "/storage/media/post/" + images[position])
                .into(viewHolder.image)

        if (id != null) {
            viewHolder.itemView.setOnClickListener {
                this.context.startActivity(Intent(this.context, TimelinePostDetailActivity::class.java).putExtra("PostID", id))
            }
        }
    }

    class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)!!
    }
}
