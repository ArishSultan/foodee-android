package foodie.app.rubikkube.foodie.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import foodie.app.rubikkube.foodie.R


class TimelineAdapter : RecyclerView.Adapter<TimelineAdapter.TimelineHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineHolder {

        val inflater = LayoutInflater.from(parent?.context)
        return TimelineHolder(inflater.inflate(R.layout.holder_timelinefragment, parent, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(p0: TimelineHolder, p1: Int) {

    }


    inner class TimelineHolder(val view: View): RecyclerView.ViewHolder(view) {

//        val tvChallengesTitle: TextView = view.findViewById(R.id.challenge_title)
//        val tvChallengesStatus: TextView = view.findViewById(R.id.challenge_status)
//        val tvStake: TextView = view.findViewById(R.id.stake_value)
//        val tvProfit: TextView = view.findViewById(R.id.profit_value)
//        val tvStep: TextView = view.findViewById(R.id.step_value)

    }


}