package foodie.app.rubikkube.foodie.classes

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stfalcon.frescoimageviewer.adapter.ViewHolder

class RecyclerViewTimelineAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //Constructor
//    public RecyclerViewTimelineAdapter(){
//
//    }
//
//    //Implemented Methods
    override fun getItemCount(): Int = 0
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder = null!!

    class RecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}