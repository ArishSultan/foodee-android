package foodie.app.rubikkube.foodie.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.adapter.ProfileFoodAdapter
import foodie.app.rubikkube.foodie.adapter.TimelineAdapter
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var profileAdapter: ProfileFoodAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false) as View
        setUpRecyclerView(view)

        view.female_card.setOnClickListener {
            view.female_card_bg.setBackgroundResource(R.drawable.rounded_button)
            view.male_card_bg.setBackgroundResource(R.drawable.rectangular_line)
            view.female_txt.setTextColor(resources.getColor(R.color.white))
            view.male_txt.setTextColor(resources.getColor(R.color.d_gray))
        }

        view.male_card.setOnClickListener {
            view.female_card_bg.setBackgroundResource(R.drawable.rectangular_line)
            view.male_card_bg.setBackgroundResource(R.drawable.rounded_button)
            view.female_txt.setTextColor(resources.getColor(R.color.d_gray))
            view.male_txt.setTextColor(resources.getColor(R.color.white))
        }

        return view
    }

    private fun setUpRecyclerView(view: View) {

        profileAdapter = ProfileFoodAdapter()
        view.friend_like_food.setHasFixedSize(false)

        val layoutManager = LinearLayoutManager(activity,LinearLayout.HORIZONTAL,false)

        view.friend_like_food.layoutManager = layoutManager
        view.friend_like_food.adapter = profileAdapter

    }

}// Required empty public constructor
