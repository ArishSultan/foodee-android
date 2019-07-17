package foodie.app.rubikkube.foodie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import foodie.app.rubikkube.foodie.R;


/**
 * Created by hp on 11/8/2016.
 */

public class MultimediaAdapter extends RecyclerView.Adapter<MultimediaAdapter.myViewHolder>  {


    private final LayoutInflater inflater;
    private Context context;
    public static ArrayList<String> filePaths = new ArrayList<String>();
    private String comingFrom;

    public MultimediaAdapter(Context context, ArrayList<String> _filePath,String comingFrom){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.filePaths = _filePath;
        this.comingFrom = comingFrom;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.multimedia_grid_item,parent,false);
        myViewHolder holder = new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, final int position) {


        String file = filePaths.get(position);
        //int lastPosition = filePaths.size() - 1;

        if(this.comingFrom.equals("addPost")) {
            if (position == filePaths.size() - 1) {
                Glide.with(context).load(R.drawable.ic_add_black_24dp).into(holder.imgOne);
                holder._deleteIcon.setVisibility(View.INVISIBLE);
            } else {
                Glide.with(context).load(file).into(holder.imgOne);
                holder._deleteIcon.setVisibility(View.VISIBLE);
            }
        }
        else{
            if (position == filePaths.size() - 1) {
                Glide.with(context).load(R.drawable.ic_add_black_24dp).into(holder.imgOne);
                holder._deleteIcon.setVisibility(View.INVISIBLE);
            } else {
                Glide.with(context).load(file).into(holder.imgOne);
                holder._deleteIcon.setVisibility(View.VISIBLE);
            }
        }

        holder._deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filePaths.remove(position);
                notifyDataSetChanged();

         }
        });

    }


    @Override
    public int getItemCount() {
        return filePaths.size();
    }


    public void addMultimedia(ArrayList<String> filePath){
        filePaths = filePath;
        notifyDataSetChanged();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgOne;
        private RelativeLayout _add_item;
        private ImageView _deleteIcon;

        public myViewHolder(View itemView) {
            super(itemView);

            imgOne = (ImageView) itemView.findViewById(R.id.imgOne);
            _deleteIcon = (ImageView) itemView.findViewById(R.id.delete_btn);
            _add_item = (RelativeLayout) itemView.findViewById(R.id.add_item);


        }
    }



}
