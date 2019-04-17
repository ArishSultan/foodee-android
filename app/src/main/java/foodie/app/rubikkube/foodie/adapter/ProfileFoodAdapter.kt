package foodie.app.rubikkube.foodie.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import foodie.app.rubikkube.foodie.R

class ProfileFoodAdapter : RecyclerView.Adapter<ProfileFoodAdapter.ProfileFoodHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFoodHolder {

        val inflater = LayoutInflater.from(parent?.context)
        return ProfileFoodHolder(inflater.inflate(R.layout.holder_profile_food, parent, false))

    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(p0: ProfileFoodHolder, p1: Int) {
    }


    inner class ProfileFoodHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        val tvChallengesTitle: TextView = view.findViewById(R.id.challenge_title)
    }

}