package foodie.app.rubikkube.foodie.activities

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import foodie.app.rubikkube.foodie.R
import kotlinx.android.synthetic.main.activity_post.*
import foodie.app.rubikkube.foodie.adapter.MultimediaAdapter
import net.alhazmy13.mediapicker.Image.ImagePicker
import java.util.ArrayList

class PostActivity : AppCompatActivity() {


    private var imageList: ArrayList<String>? = null
    private var multimediaGridAdapter: MultimediaAdapter? = null
    private var rv_grid: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)


        back_icon.setOnClickListener {
            finish()

        }

        rv_grid = findViewById(R.id.rv_grid)
        setMultimediaGridAdapter()

        post_btn.setOnClickListener {

        }
    }


    private fun setMultimediaGridAdapter() {
        imageList = ArrayList()
        imageList!!.add("start")
        multimediaGridAdapter = MultimediaAdapter(this, imageList)
        rv_grid!!.setItemAnimator(DefaultItemAnimator())
        rv_grid!!.setAdapter(multimediaGridAdapter)
        rv_grid!!.setLayoutManager(LinearLayoutManager(this, LinearLayout.HORIZONTAL, false))
        rv_grid!!.addOnItemTouchListener(RecyclerTouchListener(this@PostActivity, rv_grid!!, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {


                val lastPos = imageList?.size?.minus(1)
                //String aray[] = multimedia.get(position).split("@");
                if (position == lastPos) {

                    ImagePicker.Builder(this@PostActivity)
                            .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .extension(ImagePicker.Extension.PNG)
                            .scale(600, 600)
                            .allowMultipleImages(false)
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
            val mPaths = data!!.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH)
            Log.d("LOG",""+mPaths.get(0))
            imageList!!.add(0,mPaths.get(0))
            multimediaGridAdapter!!.notifyDataSetChanged()


            // Toast.makeText(DashboardActivity.this,"this is called here",Toast.LENGTH_LONG).show();

        }
    }

    class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener?) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {

                    return true

                }

                override fun onLongPress(e: MotionEvent) {

                    val child = recyclerView.findChildViewUnder(e.getX(), e.getY())

                    if (child != null && clickListener != null) {

                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))

                    }
                }
            })
        }


        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            val child = rv.findChildViewUnder(e.getX(), e.getY())

            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {

                clickListener.onClick(child, rv.getChildPosition(child))

            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }


        interface ClickListener {

            fun onClick(view: View, position: Int)

            fun onLongClick(view: View?, position: Int)
        }
    }


}
