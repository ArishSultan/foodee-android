package foodie.app.rubikkube.foodie.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout


import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.MultimediaAdapter
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import net.alhazmy13.mediapicker.Image.ImagePicker
import java.util.ArrayList

class TimelineFragment : Fragment() {

    private var imageList: ArrayList<String>? = null
    private var multimediaGridAdapter: MultimediaAdapter? = null
    private var rv_grid: RecyclerView? = null

    private lateinit var timeLineAdapter: TimelineAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View =  inflater.inflate(R.layout.fragment_timeline, container, false)
//        view.toolbar_title.setText("TimeLine")
//        view.text.setText("TimeLine")
        setUpRecyclerView(view)

        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpRecyclerView(view: View) {

        timeLineAdapter = TimelineAdapter()
        view.timeline_recyclerview.setHasFixedSize(false)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayout.VERTICAL

        view.timeline_recyclerview.layoutManager = layoutManager
        view.timeline_recyclerview.adapter = timeLineAdapter

    }

    private fun setMultimediaGridAdapter() {
        imageList = ArrayList<String>()
        imageList?.add("start")
        multimediaGridAdapter = MultimediaAdapter(getContext(), imageList)
        rv_grid?.setItemAnimator(DefaultItemAnimator())
        rv_grid?.setAdapter(multimediaGridAdapter)
        rv_grid?.setLayoutManager(LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false))
        rv_grid?.addOnItemTouchListener(RecyclerTouchListener(getActivity() , rv_grid!!, object : RecyclerTouchListener.ClickListener() {
            fun onClick(view: View, position: Int) {

                val lastPos = imageList.size - 1
                //String aray[] = multimedia.get(position).split("@");
                if (position == lastPos) {

                    ImagePicker.Builder(getActivity())
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

            fun onLongClick(view: View, position: Int) {

            }
        }))

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
