package foodie.app.rubikkube.foodie.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import kotlinx.android.synthetic.main.fragment_timeline.view.*

class TimelineFragment : Fragment() {

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

}
