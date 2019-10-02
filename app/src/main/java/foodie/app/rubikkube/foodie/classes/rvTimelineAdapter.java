package foodie.app.rubikkube.foodie.classes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class rvTimelineAdapter extends RecyclerView.Adapter {

    //Constructor
    public rvTimelineAdapter(){

    }

    //Implemented Methods
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    } //typepass rvTimelineAdapter.rvViewHolder

    //Nested Class
    public class rvViewHolder extends RecyclerView.ViewHolder {
        public rvViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
