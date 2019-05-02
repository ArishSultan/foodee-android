package foodie.app.rubikkube.foodie.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import foodie.app.rubikkube.foodie.R

class FoodListAdapter: RecyclerView.Adapter<FoodListAdapter.FoodListeHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListAdapter.FoodListeHolder {
        val inflater = LayoutInflater.from(parent?.context)
        return FoodListeHolder(inflater.inflate(R.layout.holder_timelinefragment, parent, false))
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: FoodListAdapter.FoodListeHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class FoodListeHolder(val view: View): RecyclerView.ViewHolder(view) {}

}